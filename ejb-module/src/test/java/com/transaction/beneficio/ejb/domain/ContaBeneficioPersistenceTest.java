package com.transaction.beneficio.ejb.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ContaBeneficioPersistenceTest {

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
    void shouldPersistContaBeneficioWithOptimisticLockingField() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Cliente cliente = new Cliente("Cliente Teste", "cliente.teste@example.com");
            em.persist(cliente);

            Beneficio beneficio = new Beneficio("Beneficio Teste", "Descricao", new BigDecimal("100.00"), true);
            em.persist(beneficio);

            ContaBeneficio conta = new ContaBeneficio(cliente, beneficio, new BigDecimal("50.00"));
            em.persist(conta);

            em.getTransaction().commit();

            assertNotNull(conta.getId());
            assertNotNull(conta.getVersion());
            assertTrue(conta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
        } finally {
            em.close();
        }
    }
}
