package com.transaction.beneficio.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "CLIENTE")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 150)
    private String nome;

    @Column(name = "EMAIL", nullable = false, length = 150, unique = true)
    private String email;

    protected Cliente() {
    }

    public Cliente(String nome, String email) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome must not be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be null or blank");
        }
        this.nome = nome;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}
