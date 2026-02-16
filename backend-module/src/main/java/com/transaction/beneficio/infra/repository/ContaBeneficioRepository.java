package com.transaction.beneficio.infra.repository;

import com.transaction.beneficio.domain.ContaBeneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaBeneficioRepository extends JpaRepository<ContaBeneficio, Long> {
}
