package com.payflow.api.controller;

import com.payflow.api.dto.request.DepositRequest;
import com.payflow.api.dto.response.TransactionResponse;
import com.payflow.api.dto.response.WalletResponse;
import com.payflow.api.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallets")
@Tag(name = "Carteiras", description = "Consulta de saldo, depósitos e extrato de transações")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Consultar saldo da carteira por ID do usuário")
    @ApiResponse(responseCode = "200", description = "Saldo consultado com sucesso")
    @ApiResponse(responseCode = "404", description = "Carteira não encontrada")
    public ResponseEntity<WalletResponse> getWalletByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getWalletByUserId(userId));
    }

    @PostMapping("/deposit")
    @Operation(summary = "Realizar depósito em uma carteira")
    @ApiResponse(responseCode = "200", description = "Depósito efetuado com sucesso")
    @ApiResponse(responseCode = "400", description = "Valor inválido")
    public ResponseEntity<WalletResponse> deposit(@Valid @RequestBody DepositRequest request) {
        return ResponseEntity.ok(walletService.deposit(request));
    }

    @GetMapping("/users/{userId}/statement")
    @Operation(summary = "Extrato de movimentações financeiras")
    @ApiResponse(responseCode = "200", description = "Extrato retornado com sucesso")
    public ResponseEntity<List<TransactionResponse>> getStatement(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getStatement(userId));
    }
}
