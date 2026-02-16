package com.transaction.beneficio.integration;

import com.transaction.beneficio.domain.Beneficio;
import com.transaction.beneficio.domain.Cliente;
import com.transaction.beneficio.domain.ContaBeneficio;
import com.transaction.beneficio.infra.repository.BeneficioRepository;
import com.transaction.beneficio.infra.repository.ClienteRepository;
import com.transaction.beneficio.infra.repository.ContaBeneficioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestTransferCorePortConfig.class)
@Transactional
class ContaBeneficioServiceIntegrationTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private BeneficioRepository beneficioRepository;

    @Autowired
    private ContaBeneficioRepository contaBeneficioRepository;

    @Test
    void shouldCreateContaBeneficioWithH2Database() {
        Cliente cliente = new Cliente("Joao da Silva", "joao@example.com");
        cliente = clienteRepository.save(cliente);

        Beneficio beneficio = new Beneficio("Vale", "Descricao", new BigDecimal("500.00"), true);
        beneficio = beneficioRepository.save(beneficio);

        ContaBeneficio conta = new ContaBeneficio(cliente, beneficio, new BigDecimal("100.00"));
        conta = contaBeneficioRepository.save(conta);

        assertThat(conta.getId()).isNotNull();
        assertThat(conta.getSaldo()).isEqualByComparingTo("100.00");
        assertThat(conta.getCliente().getId()).isEqualTo(cliente.getId());
        assertThat(conta.getBeneficio().getId()).isEqualTo(beneficio.getId());
    }
}
