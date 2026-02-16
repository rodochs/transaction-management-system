package com.transaction.beneficio.infra.repository;

import com.transaction.beneficio.domain.Beneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {
}
