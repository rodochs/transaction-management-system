package com.transaction.beneficio.app;

import java.math.BigDecimal;

public interface TransferCorePort {

    void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount);
}
