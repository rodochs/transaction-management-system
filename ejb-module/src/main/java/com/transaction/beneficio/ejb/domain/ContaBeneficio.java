package com.transaction.beneficio.ejb.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CONTA_BENEFICIO",
       uniqueConstraints = @UniqueConstraint(name = "UK_CONTA_BENEFICIO_CLIENTE_BENEFICIO",
                                            columnNames = {"CLIENTE_ID", "BENEFICIO_ID"}))
public class ContaBeneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENTE_ID", nullable = false,
                foreignKey = @ForeignKey(name = "FK_CONTA_BENEFICIO_CLIENTE"))
    private Cliente cliente;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "BENEFICIO_ID", nullable = false,
                foreignKey = @ForeignKey(name = "FK_CONTA_BENEFICIO_BENEFICIO"))
    private Beneficio beneficio;

    @Column(name = "SALDO", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Version
    @Column(name = "VERSION")
    private Long version;

    protected ContaBeneficio() {
        // JPA only
    }

    public ContaBeneficio(Cliente cliente, Beneficio beneficio, BigDecimal saldoInicial) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente must not be null");
        }
        if (beneficio == null) {
            throw new IllegalArgumentException("Beneficio must not be null");
        }
        if (saldoInicial == null || saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance must not be negative");
        }
        this.cliente = cliente;
        this.beneficio = beneficio;
        this.saldo = saldoInicial;
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Beneficio getBeneficio() {
        return beneficio;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public Long getVersion() {
        return version;
    }

    public void credit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.saldo = this.saldo.add(amount);
    }

    public void debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (this.saldo.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        this.saldo = this.saldo.subtract(amount);
    }
}
