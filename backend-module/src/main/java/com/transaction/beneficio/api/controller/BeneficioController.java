package com.transaction.beneficio.api.controller;

import com.transaction.beneficio.api.dto.BeneficioRequest;
import com.transaction.beneficio.api.dto.BeneficioResponse;
import com.transaction.beneficio.domain.Beneficio;
import com.transaction.beneficio.infra.repository.BeneficioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/beneficios")
@Tag(name = "Benefícios", description = "Gerenciamento de tipos de benefícios corporativos")
public class BeneficioController {

    private final BeneficioRepository repository;

    public BeneficioController(BeneficioRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Operation(summary = "Listar benefícios", description = "Retorna todos os tipos de benefícios cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de benefícios retornada com sucesso")
    public List<BeneficioResponse> listAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar benefício por ID", description = "Retorna um benefício específico pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Benefício encontrado"),
            @ApiResponse(responseCode = "404", description = "Benefício não encontrado")
    })
    public ResponseEntity<BeneficioResponse> getById(
            @Parameter(description = "ID do benefício") @PathVariable Long id) {
        return repository.findById(id)
                .map(beneficio -> ResponseEntity.ok(toResponse(beneficio)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar benefício", description = "Cadastra um novo tipo de benefício")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Benefício criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<BeneficioResponse> create(@Valid @RequestBody BeneficioRequest request) {
        Beneficio entity = new Beneficio(
                request.getNome(),
                request.getDescricao(),
                request.getValor(),
                request.getAtivo()
        );
        Beneficio saved = repository.save(entity);
        return ResponseEntity.created(URI.create("/api/v1/beneficios/" + saved.getId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar benefício", description = "Atualiza os dados de um benefício existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Benefício atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Benefício não encontrado")
    })
    public ResponseEntity<BeneficioResponse> update(
            @Parameter(description = "ID do benefício") @PathVariable Long id,
            @Valid @RequestBody BeneficioRequest request) {
        return repository.findById(id)
                .map(existing -> {
                    if (request.getNome() != null) {
                        existing.setNome(request.getNome());
                    }
                    existing.setDescricao(request.getDescricao());
                    existing.setValor(request.getValor());
                    existing.setAtivo(request.getAtivo());
                    Beneficio saved = repository.save(existing);
                    return ResponseEntity.ok(toResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir benefício", description = "Remove um benefício pelo ID")
    @ApiResponse(responseCode = "204", description = "Benefício excluído com sucesso")
    public void delete(@Parameter(description = "ID do benefício") @PathVariable Long id) {
        repository.deleteById(id);
    }

    private BeneficioResponse toResponse(Beneficio beneficio) {
        return new BeneficioResponse(
                beneficio.getId(),
                beneficio.getNome(),
                beneficio.getDescricao(),
                beneficio.getValor(),
                beneficio.getAtivo()
        );
    }
}
