package com.payflow.api.controller;

import com.payflow.api.dto.request.TransferRequest;
import com.payflow.api.dto.response.TransferResponse;
import com.payflow.api.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transfers")
@Tag(name = "Transferências", description = "Operações de pagamento e transferência financeira entre contas")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @Operation(summary = "Realizar transferência", description = "Transfere valor monetário entre usuários com controle transacional e concorrência")
    @ApiResponse(responseCode = "201", description = "Transferência realizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Requisição inválida ou lojista tentando enviar")
    @ApiResponse(responseCode = "404", description = "Pagador ou beneficiário não encontrado")
    @ApiResponse(responseCode = "422", description = "Saldo insuficiente")
    @ApiResponse(responseCode = "403", description = "Transação recusada pelo autorizador externo")
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        TransferResponse response = transferService.transfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
