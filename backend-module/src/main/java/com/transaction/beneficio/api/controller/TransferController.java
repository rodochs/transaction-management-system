package com.transaction.beneficio.api.controller;

import com.transaction.beneficio.api.dto.TransferRequest;
import com.transaction.beneficio.api.dto.TransferResult;
import com.transaction.beneficio.app.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
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
