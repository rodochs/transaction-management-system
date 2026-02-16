package com.transaction.beneficio.infra.repository;

import com.transaction.beneficio.domain.Beneficio;
import com.transaction.beneficio.domain.Cliente;
import com.transaction.beneficio.domain.ContaBeneficio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ContaBeneficioRepositoryTest {

    @Autowired
    private ContaBeneficioRepository contaRepository;

    @Autowired
    private BeneficioRepository beneficioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void shouldPersistContaBeneficio() {
        Cliente cliente = new Cliente("Cliente Teste", "cliente.backend@example.com");
        Beneficio beneficio = new Beneficio("Beneficio Teste", "Descricao", new BigDecimal("100.00"), true);

        Cliente savedCliente = clienteRepository.save(cliente);
        Beneficio savedBeneficio = beneficioRepository.save(beneficio);

        ContaBeneficio conta = new ContaBeneficio(savedCliente, savedBeneficio, new BigDecimal("50.00"));
        ContaBeneficio saved = contaRepository.save(conta);

        assertNotNull(saved.getId());
        assertTrue(saved.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
    }
}
