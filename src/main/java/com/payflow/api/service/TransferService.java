package com.payflow.api.service;

import com.payflow.api.domain.entity.Transaction;
import com.payflow.api.domain.entity.User;
import com.payflow.api.domain.entity.Wallet;
import com.payflow.api.domain.enums.TransactionStatus;
import com.payflow.api.dto.request.TransferRequest;
import com.payflow.api.dto.response.TransferResponse;
import com.payflow.api.exception.BusinessException;
import com.payflow.api.exception.EntityNotFoundException;
import com.payflow.api.exception.UnauthorizedTransactionException;
import com.payflow.api.repository.TransactionRepository;
import com.payflow.api.repository.UserRepository;
import com.payflow.api.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final AuthorizationService authorizationService;
    private final NotificationService notificationService;

    public TransferService(
            UserRepository userRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            AuthorizationService authorizationService,
            NotificationService notificationService) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.authorizationService = authorizationService;
        this.notificationService = notificationService;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public TransferResponse transfer(TransferRequest request) {
        log.info("Iniciando transferência de R$ {} do usuário ID {} para o usuário ID {}",
                request.value(), request.payerId(), request.payeeId());

        // 1. Validação de auto-transferência
        if (request.payerId().equals(request.payeeId())) {
            throw new BusinessException("Não é permitido realizar transferências para a própria conta.");
        }

        // 2. Busca e validação dos usuários
        User payer = userRepository.findById(request.payerId())
                .orElseThrow(() -> new EntityNotFoundException("Pagador não encontrado com o ID: " + request.payerId()));

        User payee = userRepository.findById(request.payeeId())
                .orElseThrow(() -> new EntityNotFoundException("Beneficiário não encontrado com o ID: " + request.payeeId()));

        // 3. Regra de Negócio: Lojistas só podem receber transferências, nunca enviar
        if (payer.isMerchant()) {
            throw new BusinessException("Lojistas (MERCHANT) não têm permissão para enviar transferências, apenas receber.");
        }

        // 4. Bloqueio pessimista ordenado para prevenir deadlocks concorrentes
        Long firstLockUserId = Math.min(payer.getId(), payee.getId());
        Long secondLockUserId = Math.max(payer.getId(), payee.getId());

        Wallet walletFirst = walletRepository.findByUserIdWithLock(firstLockUserId)
                .orElseThrow(() -> new EntityNotFoundException("Carteira não encontrada para usuário: " + firstLockUserId));
        Wallet walletSecond = walletRepository.findByUserIdWithLock(secondLockUserId)
                .orElseThrow(() -> new EntityNotFoundException("Carteira não encontrada para usuário: " + secondLockUserId));

        Wallet payerWallet = payer.getId().equals(firstLockUserId) ? walletFirst : walletSecond;
        Wallet payeeWallet = payee.getId().equals(firstLockUserId) ? walletFirst : walletSecond;

        // 5. Verificação de autorizador externo (Serviço de Autorização)
        boolean authorized = authorizationService.isAuthorized();
        if (!authorized) {
            Transaction failedTx = new Transaction(payer, payee, request.value(), TransactionStatus.FAILED);
            transactionRepository.save(failedTx);
            throw new UnauthorizedTransactionException("A transferência foi recusada pelo serviço autorizador externo.");
        }

        // 6. Débito e Crédito das carteiras
        payerWallet.debit(request.value());
        payeeWallet.credit(request.value());

        walletRepository.save(payerWallet);
        walletRepository.save(payeeWallet);

        // 7. Registro da transação no banco de dados
        Transaction transaction = new Transaction(payer, payee, request.value(), TransactionStatus.SUCCESS);
        Transaction savedTransaction = transactionRepository.save(transaction);

        // 8. Disparo de notificação assíncrona para o recebedor
        notificationService.sendTransferNotification(payee, request.value());

        log.info("Transferência ID {} de R$ {} concluída com sucesso.", savedTransaction.getId(), request.value());

        return TransferResponse.fromEntity(savedTransaction, "Transferência realizada com sucesso!");
    }
}
