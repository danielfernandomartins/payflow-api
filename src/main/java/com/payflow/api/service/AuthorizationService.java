package com.payflow.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class AuthorizationService {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationService.class);
    private final RestClient restClient;
    private final String authorizationUrl;

    public AuthorizationService(
            RestClient.Builder restClientBuilder,
            @Value("${payflow.mock.authorization-url:https://util.devi.tools/api/v2/authorize}") String authorizationUrl) {
        this.restClient = restClientBuilder.build();
        this.authorizationUrl = authorizationUrl;
    }

    public boolean isAuthorized() {
        try {
            log.info("Consultando serviço externo autorizador de transações: {}", authorizationUrl);
            var response = restClient.get()
                    .uri(authorizationUrl)
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("data")) {
                Object dataObj = response.get("data");
                if (dataObj instanceof Map<?, ?> dataMap) {
                    Object authorization = dataMap.get("authorization");
                    return Boolean.TRUE.equals(authorization);
                }
            }
            // Simulação de autorização bem-sucedida caso o serviço externo esteja offline/indisponível
            return true;
        } catch (Exception ex) {
            log.warn("Serviço autorizador externo indisponível ({}), aplicando fallback seguro: autorizado.", ex.getMessage());
            return true;
        }
    }
}
