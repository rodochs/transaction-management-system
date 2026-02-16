package com.transaction.beneficio.api.dto;

import java.math.BigDecimal;

public class ContaBeneficioResponse {

    private Long id;
    private Long clienteId;
    private String clienteNome;
    private Long beneficioId;
    private BigDecimal saldo;

    public ContaBeneficioResponse(Long id, Long clienteId, String clienteNome, Long beneficioId, BigDecimal saldo) {
        this.id = id;
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
        this.beneficioId = beneficioId;
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public Long getBeneficioId() {
        return beneficioId;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
}
