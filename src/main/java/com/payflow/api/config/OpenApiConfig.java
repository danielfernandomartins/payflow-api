package com.payflow.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PayFlow API — Motor de Carteira Digital & Pagamentos")
                        .description("API RESTful de alta performance e consistência transacional desenvolvida em Java 21 e Spring Boot 3.3. Implementa proteção contra concorrência/deadlocks, regras de negócio financeiras, RFC 7807 e mensageria assíncrona.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Daniel Fernando Martins")
                                .email("dfernandom@outlook.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
