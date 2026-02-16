package com.transaction.beneficio.ejb.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class BeneficioPersistenceTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    static void init() {
        emf = Persistence.createEntityManagerFactory("beneficioPU");
    }

    @AfterAll
    static void shutdown() {
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    void shouldPersistBeneficio() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Beneficio beneficio = new Beneficio("Beneficio Teste", "Descricao", new BigDecimal("100.00"), true);
            em.persist(beneficio);
            em.getTransaction().commit();

            assertNotNull(beneficio.getId());
            assertTrue(beneficio.getValor().compareTo(BigDecimal.ZERO) >= 0);
        } finally {
            em.close();
        }
    }
}
