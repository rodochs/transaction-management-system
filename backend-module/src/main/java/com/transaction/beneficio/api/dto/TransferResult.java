package com.transaction.beneficio.api.dto;

import java.math.BigDecimal;

public class TransferResult {

    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;

    public TransferResult(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
