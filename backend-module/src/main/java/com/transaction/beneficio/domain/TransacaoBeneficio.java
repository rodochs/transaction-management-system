package com.transaction.beneficio.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSACAO_BENEFICIO")
public class TransacaoBeneficio {

    public enum TipoTransacao {
        CREDITO,
        DEBITO,
        TRANSFERENCIA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTA_ORIGEM_ID",
            foreignKey = @ForeignKey(name = "FK_TRANSACAO_CONTA_ORIGEM"))
    private ContaBeneficio contaOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTA_DESTINO_ID",
            foreignKey = @ForeignKey(name = "FK_TRANSACAO_CONTA_DESTINO"))
    private ContaBeneficio contaDestino;

    @Column(name = "VALOR", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoTransacao tipo;

    @Column(name = "DATA_HORA", nullable = false)
    private LocalDateTime dataHora;

    protected TransacaoBeneficio() {
    }

    public TransacaoBeneficio(ContaBeneficio contaOrigem,
                              ContaBeneficio contaDestino,
                              BigDecimal valor,
                              TipoTransacao tipo) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo must not be null");
        }
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.valor = valor;
        this.tipo = tipo;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public ContaBeneficio getContaOrigem() {
        return contaOrigem;
    }

    public ContaBeneficio getContaDestino() {
        return contaDestino;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
