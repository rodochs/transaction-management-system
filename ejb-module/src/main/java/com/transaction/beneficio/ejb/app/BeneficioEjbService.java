package com.transaction.beneficio.ejb.app;

import com.transaction.beneficio.ejb.domain.ContaBeneficio;
import com.transaction.beneficio.ejb.domain.TransacaoBeneficio;
import com.transaction.beneficio.ejb.exception.EntidadeNaoEncontradaException;
import com.transaction.beneficio.ejb.exception.SaldoInsuficienteException;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;

@Stateless
public class BeneficioEjbService {

    @PersistenceContext
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        validateInput(fromId, toId, amount);

        ContaBeneficio fromAccount = em.find(ContaBeneficio.class, fromId, LockModeType.PESSIMISTIC_WRITE);
        if (fromAccount == null) {
            throw new EntidadeNaoEncontradaException("Source account not found: id=" + fromId);
        }

        ContaBeneficio toAccount = em.find(ContaBeneficio.class, toId, LockModeType.PESSIMISTIC_WRITE);
        if (toAccount == null) {
            throw new EntidadeNaoEncontradaException("Target account not found: id=" + toId);
        }

        if (fromAccount.getSaldo().compareTo(amount) < 0) {
            throw new SaldoInsuficienteException("Insufficient balance in source account: id=" + fromId);
        }

        fromAccount.debit(amount);
        toAccount.credit(amount);

        TransacaoBeneficio transaction = new TransacaoBeneficio(
                fromAccount,
                toAccount,
                amount,
                TransacaoBeneficio.TipoTransacao.TRANSFERENCIA
        );

        em.persist(transaction);
        em.merge(fromAccount);
        em.merge(toAccount);
    }

    private void validateInput(Long fromId, Long toId, BigDecimal amount) {
        if (fromId == null || toId == null) {
            throw new IllegalArgumentException("Source and target account ids must not be null");
        }
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Source and target accounts must be different");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }
}
