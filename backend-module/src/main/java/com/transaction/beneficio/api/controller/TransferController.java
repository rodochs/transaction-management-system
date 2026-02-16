package com.transaction.beneficio.api.controller;

import com.transaction.beneficio.api.dto.TransferRequest;
import com.transaction.beneficio.api.dto.TransferResult;
import com.transaction.beneficio.app.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfers")
@Tag(name = "Transferências", description = "Operações de transferência entre contas de benefício")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @Operation(summary = "Realizar transferência", 
               description = "Transfere um valor de uma conta de benefício para outra")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou saldo insuficiente"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    public ResponseEntity<TransferResult> transfer(@Valid @RequestBody TransferRequest request) {
        transferService.transfer(request.getFromAccountId(), request.getToAccountId(), request.getAmount());
        TransferResult result = new TransferResult(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount()
        );
        return ResponseEntity.ok(result);
    }
}
