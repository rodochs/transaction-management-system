package com.transaction.beneficio.app;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferServiceImpl implements TransferService {

    private final TransferCorePort transferCorePort;

    public TransferServiceImpl(TransferCorePort transferCorePort) {
        this.transferCorePort = transferCorePort;
    }

    @Override
    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        transferCorePort.transfer(fromAccountId, toAccountId, amount);
    }
}
