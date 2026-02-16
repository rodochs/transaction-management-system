package com.transaction.beneficio.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class ContaBeneficioRequest {

    @NotNull
    private Long clienteId;

    @NotNull
    private Long beneficioId;

    @NotNull
    @PositiveOrZero
    private BigDecimal saldoInicial;

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getBeneficioId() {
        return beneficioId;
    }

    public void setBeneficioId(Long beneficioId) {
        this.beneficioId = beneficioId;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }
}
