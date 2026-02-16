package com.transaction.beneficio.ejb.app;

import com.transaction.beneficio.ejb.domain.Beneficio;
import com.transaction.beneficio.ejb.domain.Cliente;
import com.transaction.beneficio.ejb.domain.ContaBeneficio;
import com.transaction.beneficio.ejb.exception.EntidadeNaoEncontradaException;
import com.transaction.beneficio.ejb.exception.SaldoInsuficienteException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class BeneficioEjbServiceTest {

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

    private BeneficioEjbService createService(EntityManager em) {
        BeneficioEjbService service = new BeneficioEjbService();
        // Field injection via reflection for test purposes only
        try {
            var field = BeneficioEjbService.class.getDeclaredField("em");
            field.setAccessible(true);
            field.set(service, em);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return service;
    }

    private Long createAccount(EntityManager em, BigDecimal saldoInicial) {
        em.getTransaction().begin();

        Cliente cliente = new Cliente("Cliente Teste", "cliente.teste+" + Math.random() + "@example.com");
        em.persist(cliente);

        Beneficio beneficio = new Beneficio("Beneficio Teste", "Descricao", new BigDecimal("100.00"), true);
        em.persist(beneficio);

        ContaBeneficio conta = new ContaBeneficio(cliente, beneficio, saldoInicial);
        em.persist(conta);

        em.getTransaction().commit();
        return conta.getId();
    }

    @Test
    void shouldTransferSuccessfullyBetweenAccounts() {
        EntityManager em = emf.createEntityManager();
        try {
            Long fromId = createAccount(em, new BigDecimal("200.00"));
            Long toId = createAccount(em, new BigDecimal("50.00"));

            BeneficioEjbService service = createService(em);

            em.getTransaction().begin();
            service.transfer(fromId, toId, new BigDecimal("100.00"));
            em.getTransaction().commit();

            ContaBeneficio from = em.find(ContaBeneficio.class, fromId);
            ContaBeneficio to = em.find(ContaBeneficio.class, toId);

            assertEquals(new BigDecimal("100.00"), from.getSaldo());
            assertEquals(new BigDecimal("150.00"), to.getSaldo());
        } finally {
            em.close();
        }
    }

    @Test
    void shouldFailWhenInsufficientBalance() {
        EntityManager em = emf.createEntityManager();
        try {
            Long fromId = createAccount(em, new BigDecimal("50.00"));
            Long toId = createAccount(em, new BigDecimal("50.00"));

            BeneficioEjbService service = createService(em);

            em.getTransaction().begin();
            assertThrows(SaldoInsuficienteException.class,
                    () -> service.transfer(fromId, toId, new BigDecimal("100.00")));
            em.getTransaction().rollback();

            ContaBeneficio from = em.find(ContaBeneficio.class, fromId);
            ContaBeneficio to = em.find(ContaBeneficio.class, toId);

            // balances must remain unchanged
            assertEquals(new BigDecimal("50.00"), from.getSaldo());
            assertEquals(new BigDecimal("50.00"), to.getSaldo());
        } finally {
            em.close();
        }
    }

    @Test
    void shouldFailWhenAccountDoesNotExist() {
        EntityManager em = emf.createEntityManager();
        try {
            Long existingId = createAccount(em, new BigDecimal("100.00"));
            Long nonExistingId = 999999L;

            BeneficioEjbService service = createService(em);

            em.getTransaction().begin();
            assertThrows(EntidadeNaoEncontradaException.class,
                    () -> service.transfer(existingId, nonExistingId, new BigDecimal("10.00")));
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    @Test
    void shouldFailWhenAmountIsInvalid() {
        EntityManager em = emf.createEntityManager();
        try {
            Long fromId = createAccount(em, new BigDecimal("100.00"));
            Long toId = createAccount(em, new BigDecimal("100.00"));

            BeneficioEjbService service = createService(em);

            em.getTransaction().begin();
            assertThrows(IllegalArgumentException.class,
                    () -> service.transfer(fromId, toId, BigDecimal.ZERO));
            assertThrows(IllegalArgumentException.class,
                    () -> service.transfer(fromId, toId, new BigDecimal("-10.00")));
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    @Test
    void shouldHandleSimpleConcurrentTransfersSafely() throws InterruptedException {
        EntityManager em1 = emf.createEntityManager();
        EntityManager em2 = emf.createEntityManager();

        try {
            Long fromId;
            Long toId;

            // Prepare shared accounts
            EntityManager setupEm = emf.createEntityManager();
            try {
                fromId = createAccount(setupEm, new BigDecimal("200.00"));
                toId = createAccount(setupEm, new BigDecimal("0.00"));
            } finally {
                setupEm.close();
            }

            BeneficioEjbService service1 = createService(em1);
            BeneficioEjbService service2 = createService(em2);

            ExecutorService executor = Executors.newFixedThreadPool(2);
            CountDownLatch latch = new CountDownLatch(2);

            Runnable task1 = () -> {
                try {
                    em1.getTransaction().begin();
                    service1.transfer(fromId, toId, new BigDecimal("50.00"));
                    em1.getTransaction().commit();
                } finally {
                    latch.countDown();
                }
            };

            Runnable task2 = () -> {
                try {
                    em2.getTransaction().begin();
                    service2.transfer(fromId, toId, new BigDecimal("50.00"));
                    em2.getTransaction().commit();
                } finally {
                    latch.countDown();
                }
            };

            executor.submit(task1);
            executor.submit(task2);

            latch.await(10, TimeUnit.SECONDS);
            executor.shutdownNow();

            EntityManager verifyEm = emf.createEntityManager();
            try {
                ContaBeneficio from = verifyEm.find(ContaBeneficio.class, fromId);
                ContaBeneficio to = verifyEm.find(ContaBeneficio.class, toId);

                // Two transfers of 50.00 each: total 100.00 moved from origin to destination
                assertEquals(new BigDecimal("100.00"), from.getSaldo());
                assertEquals(new BigDecimal("100.00"), to.getSaldo());
            } finally {
                verifyEm.close();
            }
        } finally {
            em1.close();
            em2.close();
        }
    }
}
