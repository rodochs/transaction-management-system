package com.transaction.beneficio.infra.repository;

import com.transaction.beneficio.domain.Beneficio;
import com.transaction.beneficio.domain.Cliente;
import com.transaction.beneficio.domain.ContaBeneficio;
import com.transaction.beneficio.domain.TransacaoBeneficio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransacaoBeneficioRepositoryTest {

    @Autowired
    private TransacaoBeneficioRepository transacaoRepository;

    @Autowired
    private ContaBeneficioRepository contaRepository;

    @Autowired
    private BeneficioRepository beneficioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    void shouldPersistTransacaoBeneficio() {
        Cliente cliente = new Cliente("Cliente Teste", "cliente.transacao@example.com");
        Cliente savedCliente = clienteRepository.save(cliente);

        Beneficio beneficio = new Beneficio("Beneficio Teste", "Descricao", new BigDecimal("100.00"), true);
        Beneficio savedBeneficio = beneficioRepository.save(beneficio);

        ContaBeneficio contaOrigem = new ContaBeneficio(savedCliente, savedBeneficio, new BigDecimal("100.00"));
        ContaBeneficio savedOrigem = contaRepository.save(contaOrigem);

        TransacaoBeneficio transacao = new TransacaoBeneficio(
                savedOrigem,
                null,
                new BigDecimal("10.00"),
                TransacaoBeneficio.TipoTransacao.DEBITO
        );

        TransacaoBeneficio saved = transacaoRepository.save(transacao);

        assertNotNull(saved.getId());
        assertEquals(TransacaoBeneficio.TipoTransacao.DEBITO, saved.getTipo());
    }
}
