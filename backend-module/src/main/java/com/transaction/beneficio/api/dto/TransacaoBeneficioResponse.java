package com.transaction.beneficio.api.dto;

import com.transaction.beneficio.domain.TransacaoBeneficio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransacaoBeneficioResponse {

    private Long id;
    private Long contaOrigemId;
    private Long contaDestinoId;
    private BigDecimal valor;
    private TransacaoBeneficio.TipoTransacao tipo;
    private LocalDateTime dataHora;

    public TransacaoBeneficioResponse(Long id,
                                      Long contaOrigemId,
                                      Long contaDestinoId,
                                      BigDecimal valor,
                                      TransacaoBeneficio.TipoTransacao tipo,
                                      LocalDateTime dataHora) {
        this.id = id;
        this.contaOrigemId = contaOrigemId;
        this.contaDestinoId = contaDestinoId;
        this.valor = valor;
        this.tipo = tipo;
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public Long getContaOrigemId() {
        return contaOrigemId;
    }

    public Long getContaDestinoId() {
        return contaDestinoId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public TransacaoBeneficio.TipoTransacao getTipo() {
        return tipo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
