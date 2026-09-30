package com.payflow.api.service;

import com.payflow.api.domain.entity.Transaction;
import com.payflow.api.domain.entity.Wallet;
import com.payflow.api.dto.request.DepositRequest;
import com.payflow.api.dto.response.TransactionResponse;
import com.payflow.api.dto.response.WalletResponse;
import com.payflow.api.exception.EntityNotFoundException;
import com.payflow.api.repository.TransactionRepository;
import com.payflow.api.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public WalletResponse getWalletByUserId(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Carteira não encontrada para o usuário ID: " + userId));
        return WalletResponse.fromEntity(wallet);
    }

    @Transactional
    public WalletResponse deposit(DepositRequest request) {
        Wallet wallet = walletRepository.findById(request.walletId())
                .orElseThrow(() -> new EntityNotFoundException("Carteira não encontrada com o ID: " + request.walletId()));

        wallet.credit(request.amount());
        Wallet saved = walletRepository.save(wallet);
        return WalletResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getStatement(Long userId) {
        List<Transaction> transactions = transactionRepository.findAllByUserId(userId);
        return transactions.stream()
                .map(TransactionResponse::fromEntity)
                .toList();
    }
}
