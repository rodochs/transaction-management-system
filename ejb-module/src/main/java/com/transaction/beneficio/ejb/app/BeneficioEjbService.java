package com.transaction.beneficio.ejb.app;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;

/**
 * EJB application service skeleton.
 *
 * The real transfer logic will be implemented in later steps (5 and 6)
 * once the rich domain model is available in the ejb.domain package.
 */
@Stateless
public class BeneficioEjbService {

    @PersistenceContext
    private EntityManager em;

    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // TODO: implement safe transfer logic using Beneficio entity
        // This will be done when the domain model and locking rules are introduced.
    }
}
