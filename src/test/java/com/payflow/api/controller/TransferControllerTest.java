package com.payflow.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflow.api.domain.enums.TransactionStatus;
import com.payflow.api.dto.request.TransferRequest;
import com.payflow.api.dto.response.TransferResponse;
import com.payflow.api.service.TransferService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransferService transferService;

    @Test
    @DisplayName("Deve retornar HTTP 201 Created quando payload de transferência for válido")
    void shouldReturn201WhenTransferRequestIsValid() throws Exception {
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("100.00"));
        TransferResponse response = new TransferResponse(
                50L, 1L, "Alice", 2L, "Bob",
                new BigDecimal("100.00"), TransactionStatus.SUCCESS,
                "Transferência realizada com sucesso!", LocalDateTime.now()
        );

        when(transferService.transfer(any(TransferRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value(50L))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @DisplayName("Deve retornar HTTP 400 Bad Request quando valor for menor que zero")
    void shouldReturn400WhenAmountIsNegativeOrZero() throws Exception {
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("-10.00"));

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Requisição Inválida"));
    }
}
