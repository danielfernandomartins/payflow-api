package com.payflow.api.dto.request;

import com.payflow.api.domain.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "Dados para cadastro de novo usuário")
public record CreateUserRequest(
    @NotBlank(message = "O nome completo é obrigatório.")
    @Schema(example = "Carlos Eduardo Souza")
    String fullName,

    @NotBlank(message = "O documento (CPF ou CNPJ) é obrigatório.")
    @Schema(example = "12345678909")
    String document,

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    @Schema(example = "carlos.souza@email.com")
    String email,

    @NotBlank(message = "A senha é obrigatória.")
    @Schema(example = "SenhaForte@123")
    String password,

    @NotNull(message = "O tipo de usuário deve ser informado (COMMON ou MERCHANT).")
    @Schema(example = "COMMON")
    UserType userType,

    @DecimalMin(value = "0.00", message = "O saldo inicial não pode ser negativo.")
    @Schema(example = "500.00")
    BigDecimal initialBalance
) {}
