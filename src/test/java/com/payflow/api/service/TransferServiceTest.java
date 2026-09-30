package com.payflow.api.service;

import com.payflow.api.domain.entity.Transaction;
import com.payflow.api.domain.entity.User;
import com.payflow.api.domain.entity.Wallet;
import com.payflow.api.domain.enums.TransactionStatus;
import com.payflow.api.domain.enums.UserType;
import com.payflow.api.dto.request.TransferRequest;
import com.payflow.api.dto.response.TransferResponse;
import com.payflow.api.exception.BusinessException;
import com.payflow.api.exception.InsufficientBalanceException;
import com.payflow.api.exception.UnauthorizedTransactionException;
import com.payflow.api.repository.TransactionRepository;
import com.payflow.api.repository.UserRepository;
import com.payflow.api.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TransferService transferService;

    private User payer;
    private User payee;
    private Wallet payerWallet;
    private Wallet payeeWallet;

    @BeforeEach
    void setUp() {
        payer = new User(1L, "Alice Silva", "12345678901", "alice@payflow.com", "pass123", UserType.COMMON);
        payee = new User(2L, "Bob Santos", "98765432100", "bob@payflow.com", "pass123", UserType.COMMON);

        payerWallet = new Wallet(payer, new BigDecimal("1000.00"));
        payeeWallet = new Wallet(payee, new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("Deve realizar transferência com sucesso entre usuários comuns")
    void shouldTransferSuccessfullyBetweenCommonUsers() {
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("150.00"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(payer));
        when(userRepository.findById(2L)).thenReturn(Optional.of(payee));
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(payerWallet));
        when(walletRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(payeeWallet));
        when(authorizationService.isAuthorized()).thenReturn(true);

        Transaction savedTx = new Transaction(payer, payee, new BigDecimal("150.00"), TransactionStatus.SUCCESS);
        savedTx.setId(100L);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTx);

        TransferResponse response = transferService.transfer(request);

        assertThat(response).isNotNull();
        assertThat(response.amount()).isEqualByComparingTo("150.00");
        assertThat(payerWallet.getBalance()).isEqualByComparingTo("850.00");
        assertThat(payeeWallet.getBalance()).isEqualByComparingTo("350.00");

        verify(walletRepository).save(payerWallet);
        verify(walletRepository).save(payeeWallet);
        verify(notificationService).sendTransferNotification(payee, new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando pagador tentar transferir para si mesmo")
    void shouldThrowExceptionWhenPayerTransfersToSelf() {
        TransferRequest request = new TransferRequest(1L, 1L, new BigDecimal("50.00"));

        assertThatThrownBy(() -> transferService.transfer(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Não é permitido realizar transferências para a própria conta");

        verifyNoInteractions(walletRepository, transactionRepository, authorizationService);
    }

    @Test
    @DisplayName("Deve lançar exceção quando lojista tentar enviar transferência")
    void shouldThrowExceptionWhenMerchantTriesToTransfer() {
        User merchant = new User(3L, "Loja LTDA", "12345678000100", "loja@loja.com", "pass", UserType.MERCHANT);
        TransferRequest request = new TransferRequest(3L, 2L, new BigDecimal("100.00"));

        when(userRepository.findById(3L)).thenReturn(Optional.of(merchant));
        when(userRepository.findById(2L)).thenReturn(Optional.of(payee));

        assertThatThrownBy(() -> transferService.transfer(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Lojistas (MERCHANT) não têm permissão para enviar transferências");

        verifyNoInteractions(walletRepository, transactionRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando pagador não tiver saldo suficiente")
    void shouldThrowExceptionWhenBalanceIsInsufficient() {
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("1500.00"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(payer));
        when(userRepository.findById(2L)).thenReturn(Optional.of(payee));
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(payerWallet));
        when(walletRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(payeeWallet));
        when(authorizationService.isAuthorized()).thenReturn(true);

        assertThatThrownBy(() -> transferService.transfer(request))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("Saldo insuficiente");

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve lançar exceção e registrar transação como FAILED quando autorizador recusar")
    void shouldThrowExceptionWhenExternalAuthorizationFails() {
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("100.00"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(payer));
        when(userRepository.findById(2L)).thenReturn(Optional.of(payee));
        when(walletRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(payerWallet));
        when(walletRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(payeeWallet));
        when(authorizationService.isAuthorized()).thenReturn(false);

        assertThatThrownBy(() -> transferService.transfer(request))
                .isInstanceOf(UnauthorizedTransactionException.class)
                .hasMessageContaining("recusada pelo serviço autorizador");

        verify(transactionRepository).save(argThat(tx -> tx.getStatus() == TransactionStatus.FAILED));
        verify(notificationService, never()).sendTransferNotification(any(), any());
    }
}
