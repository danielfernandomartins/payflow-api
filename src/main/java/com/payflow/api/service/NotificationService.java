package com.payflow.api.service;

import com.payflow.api.domain.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final RestClient restClient;
    private final String notificationUrl;

    public NotificationService(
            RestClient.Builder restClientBuilder,
            @Value("${payflow.mock.notification-url:https://util.devi.tools/api/v1/notify}") String notificationUrl) {
        this.restClient = restClientBuilder.build();
        this.notificationUrl = notificationUrl;
    }

    @Async
    public void sendTransferNotification(User payee, BigDecimal amount) {
        log.info("[ASYNC NOTIFICATION] Notificando usuário {} ({}) sobre recebimento de R$ {}", 
                payee.getFullName(), payee.getEmail(), amount);
        try {
            restClient.post()
                    .uri(notificationUrl)
                    .body(Map.of(
                            "email", payee.getEmail(),
                            "amount", amount,
                            "message", "Você recebeu um pagamento no valor de R$ " + amount
                    ))
                    .retrieve()
                    .toBodilessEntity();
            log.info("[ASYNC NOTIFICATION] Notificação enviada com sucesso para {}", payee.getEmail());
        } catch (Exception ex) {
            log.warn("[ASYNC NOTIFICATION] Falha ao enviar notificação externa para {}: {}", payee.getEmail(), ex.getMessage());
        }
    }
}
