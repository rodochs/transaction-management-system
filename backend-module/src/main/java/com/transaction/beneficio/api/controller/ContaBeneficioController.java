package com.transaction.beneficio.api.controller;

import com.transaction.beneficio.api.dto.ContaBeneficioRequest;
import com.transaction.beneficio.api.dto.ContaBeneficioResponse;
import com.transaction.beneficio.domain.Beneficio;
import com.transaction.beneficio.domain.Cliente;
import com.transaction.beneficio.domain.ContaBeneficio;
import com.transaction.beneficio.infra.repository.ContaBeneficioRepository;
import com.transaction.beneficio.infra.repository.BeneficioRepository;
import com.transaction.beneficio.infra.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/contas-beneficio")
public class ContaBeneficioController {

    private final ContaBeneficioRepository contaRepository;
    private final ClienteRepository clienteRepository;
    private final BeneficioRepository beneficioRepository;

    public ContaBeneficioController(ContaBeneficioRepository contaRepository,
                                    ClienteRepository clienteRepository,
                                    BeneficioRepository beneficioRepository) {
        this.contaRepository = contaRepository;
        this.clienteRepository = clienteRepository;
        this.beneficioRepository = beneficioRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaBeneficioResponse> getById(@PathVariable Long id) {
        return contaRepository.findById(id)
                .map(conta -> ResponseEntity.ok(toResponse(conta)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ContaBeneficioResponse> create(@Valid @RequestBody ContaBeneficioRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente not found: " + request.getClienteId()));
        Beneficio beneficio = beneficioRepository.findById(request.getBeneficioId())
                .orElseThrow(() -> new IllegalArgumentException("Beneficio not found: " + request.getBeneficioId()));

        ContaBeneficio conta = new ContaBeneficio(
                cliente,
                beneficio,
                request.getSaldoInicial()
        );
        ContaBeneficio saved = contaRepository.save(conta);
        return ResponseEntity.created(URI.create("/api/v1/contas-beneficio/" + saved.getId()))
                .body(toResponse(saved));
    }

    private ContaBeneficioResponse toResponse(ContaBeneficio conta) {
        return new ContaBeneficioResponse(
                conta.getId(),
                conta.getCliente().getId(),
                conta.getBeneficio().getId(),
                conta.getSaldo()
        );
    }
}
