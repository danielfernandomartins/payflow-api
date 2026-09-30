package com.payflow.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "Dados para depósito em carteira")
public record DepositRequest(
    @NotNull(message = "O ID da carteira é obrigatório.")
    @Schema(example = "1")
    Long walletId,

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que R$ 0,00.")
    @Schema(example = "250.00")
    BigDecimal amount
) {}
