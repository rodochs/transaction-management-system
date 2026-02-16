package com.transaction.beneficio.api.dto;

import java.math.BigDecimal;

public class ContaBeneficioResponse {

    private Long id;
    private Long clienteId;
    private Long beneficioId;
    private BigDecimal saldo;

    public ContaBeneficioResponse(Long id, Long clienteId, Long beneficioId, BigDecimal saldo) {
        this.id = id;
        this.clienteId = clienteId;
        this.beneficioId = beneficioId;
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public Long getBeneficioId() {
        return beneficioId;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
}
