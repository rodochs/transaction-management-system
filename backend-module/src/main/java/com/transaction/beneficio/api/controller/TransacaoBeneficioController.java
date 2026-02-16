package com.transaction.beneficio.api.controller;

import com.transaction.beneficio.api.dto.TransacaoBeneficioResponse;
import com.transaction.beneficio.domain.TransacaoBeneficio;
import com.transaction.beneficio.infra.repository.TransacaoBeneficioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/transacoes")
@Tag(name = "Transações", description = "Histórico de transações de benefícios")
public class TransacaoBeneficioController {

    private final TransacaoBeneficioRepository repository;

    public TransacaoBeneficioController(TransacaoBeneficioRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Operation(summary = "Listar transações", description = "Retorna o histórico de todas as transações realizadas")
    @ApiResponse(responseCode = "200", description = "Lista de transações retornada com sucesso")
    public List<TransacaoBeneficioResponse> listAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TransacaoBeneficioResponse toResponse(TransacaoBeneficio transacao) {
        return new TransacaoBeneficioResponse(
                transacao.getId(),
                transacao.getContaOrigem() != null ? transacao.getContaOrigem().getId() : null,
                transacao.getContaDestino() != null ? transacao.getContaDestino().getId() : null,
                transacao.getValor(),
                transacao.getTipo(),
                transacao.getDataHora()
        );
    }
}
