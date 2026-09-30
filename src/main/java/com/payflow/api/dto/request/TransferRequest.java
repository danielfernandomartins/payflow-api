package com.payflow.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "Dados para envio de transferência financeira")
public record TransferRequest(
    @NotNull(message = "O ID do pagador (payerId) é obrigatório.")
    @Schema(example = "1", description = "ID do usuário que está enviando o dinheiro")
    Long payerId,

    @NotNull(message = "O ID do beneficiário (payeeId) é obrigatório.")
    @Schema(example = "2", description = "ID do usuário ou lojista que receberá o valor")
    Long payeeId,

    @NotNull(message = "O valor da transferência é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor da transferência deve ser de no mínimo R$ 0,01.")
    @Schema(example = "150.50", description = "Quantia monetária a transferir")
    BigDecimal value
) {}
