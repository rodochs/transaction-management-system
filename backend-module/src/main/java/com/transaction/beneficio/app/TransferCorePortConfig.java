package com.transaction.beneficio.app;

import com.transaction.beneficio.domain.ContaBeneficio;
import com.transaction.beneficio.infra.repository.ContaBeneficioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Configuration
public class TransferCorePortConfig {

    @Bean
    @Transactional
    public TransferCorePort transferCorePort(ContaBeneficioRepository contaBeneficioRepository) {
        return (fromAccountId, toAccountId, amount) -> {
            if (fromAccountId == null || toAccountId == null || amount == null) {
                throw new IllegalArgumentException("Invalid transfer parameters");
            }
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount must be greater than zero");
            }

            ContaBeneficio origem = contaBeneficioRepository.findById(fromAccountId)
                    .orElseThrow(() -> new IllegalArgumentException("Conta origem nao encontrada: " + fromAccountId));
            ContaBeneficio destino = contaBeneficioRepository.findById(toAccountId)
                    .orElseThrow(() -> new IllegalArgumentException("Conta destino nao encontrada: " + toAccountId));

            if (origem.getSaldo().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Saldo insuficiente na conta de origem");
            }

            origem.setSaldo(origem.getSaldo().subtract(amount));
            destino.setSaldo(destino.getSaldo().add(amount));

            contaBeneficioRepository.save(origem);
            contaBeneficioRepository.save(destino);
        };
    }
}
