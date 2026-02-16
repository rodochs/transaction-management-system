package com.transaction.beneficio.app;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;

public interface TransferService {

    @Transactional
    void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount);
}
