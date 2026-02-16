package com.transaction.beneficio.infra.repository;

import com.transaction.beneficio.domain.Beneficio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BeneficioRepositoryTest {

    @Autowired
    private BeneficioRepository repository;

    @Test
    void shouldPersistAndFindBeneficio() {
        Beneficio beneficio = new Beneficio("Beneficio Teste", "Descricao", new BigDecimal("100.00"), true);
        Beneficio saved = repository.save(beneficio);

        assertNotNull(saved.getId());
        assertTrue(saved.getValor().compareTo(BigDecimal.ZERO) >= 0);

        Beneficio found = repository.findById(saved.getId()).orElseThrow();
        assertEquals("Beneficio Teste", found.getNome());
    }
}
