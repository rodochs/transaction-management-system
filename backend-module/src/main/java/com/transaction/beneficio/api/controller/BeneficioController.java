package com.transaction.beneficio.api.controller;

import com.transaction.beneficio.api.dto.BeneficioRequest;
import com.transaction.beneficio.api.dto.BeneficioResponse;
import com.transaction.beneficio.domain.Beneficio;
import com.transaction.beneficio.infra.repository.BeneficioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/beneficios")
public class BeneficioController {

    private final BeneficioRepository repository;

    public BeneficioController(BeneficioRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<BeneficioResponse> listAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficioResponse> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(beneficio -> ResponseEntity.ok(toResponse(beneficio)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
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
    public ResponseEntity<BeneficioResponse> update(@PathVariable Long id,
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
    public void delete(@PathVariable Long id) {
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
