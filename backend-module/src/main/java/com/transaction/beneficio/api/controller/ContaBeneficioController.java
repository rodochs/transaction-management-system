package com.transaction.beneficio.api.controller;

import com.transaction.beneficio.api.dto.ContaBeneficioRequest;
import com.transaction.beneficio.api.dto.ContaBeneficioResponse;
import com.transaction.beneficio.domain.Beneficio;
import com.transaction.beneficio.domain.Cliente;
import com.transaction.beneficio.domain.ContaBeneficio;
import com.transaction.beneficio.infra.repository.ContaBeneficioRepository;
import com.transaction.beneficio.infra.repository.BeneficioRepository;
import com.transaction.beneficio.infra.repository.ClienteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/contas-beneficio")
@Tag(name = "Contas de Benefício", description = "Gerenciamento de contas de benefício dos colaboradores")
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

    @GetMapping
    @Operation(summary = "Listar contas", description = "Retorna todas as contas de benefício com saldos atuais")
    @ApiResponse(responseCode = "200", description = "Lista de contas retornada com sucesso")
    public List<ContaBeneficioResponse> listAll() {
        return contaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta por ID", description = "Retorna uma conta de benefício específica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta encontrada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    public ResponseEntity<ContaBeneficioResponse> getById(
            @Parameter(description = "ID da conta") @PathVariable Long id) {
        return contaRepository.findById(id)
                .map(conta -> ResponseEntity.ok(toResponse(conta)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar conta", description = "Cria uma nova conta de benefício para um colaborador")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Conta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
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
                conta.getCliente().getNome(),
                conta.getBeneficio().getId(),
                conta.getSaldo()
        );
    }
}
