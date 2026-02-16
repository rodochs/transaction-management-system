package com.transaction.beneficio.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig
class TransferServiceTest {

    @TestConfiguration
    static class Config {
        @Bean
        TransferService transferService(TransferCorePort transferCorePort) {
            return new TransferServiceImpl(transferCorePort);
        }
    }

    @Autowired
    private TransferService transferService;

    @MockBean
    private TransferCorePort transferCorePort;

    @Test
    void shouldDelegateTransferToCorePort() {
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = new BigDecimal("100.00");

        transferService.transfer(fromId, toId, amount);

        verify(transferCorePort, times(1)).transfer(eq(fromId), eq(toId), eq(amount));
    }
}
