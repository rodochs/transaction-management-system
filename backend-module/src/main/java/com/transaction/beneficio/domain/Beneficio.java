package com.transaction.beneficio.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "BENEFICIO")
public class Beneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "DESCRICAO", length = 255)
    private String descricao;

    @Column(name = "VALOR", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo = Boolean.TRUE;

    @Version
    @Column(name = "VERSION")
    private Long version;

    protected Beneficio() {
        // JPA only
    }

    public Beneficio(String nome, String descricao, BigDecimal valor, Boolean ativo) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome must not be null or blank");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor must not be negative");
        }
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
        this.ativo = ativo != null ? ativo : Boolean.TRUE;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Long getVersion() {
        return version;
    }
}
