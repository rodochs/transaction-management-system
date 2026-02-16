package com.transaction.beneficio.api.controller;

import com.transaction.beneficio.api.dto.ClienteRequest;
import com.transaction.beneficio.api.dto.ClienteResponse;
import com.transaction.beneficio.domain.Cliente;
import com.transaction.beneficio.infra.repository.ClienteRepository;
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
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "Gerenciamento de colaboradores/clientes")
public class ClienteController {

    private final ClienteRepository repository;

    public ClienteController(ClienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Retorna todos os clientes cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    public List<ClienteResponse> listAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna um cliente específico pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ClienteResponse> getById(
            @Parameter(description = "ID do cliente") @PathVariable Long id) {
        return repository.findById(id)
                .map(cliente -> ResponseEntity.ok(toResponse(cliente)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar cliente", description = "Cadastra um novo cliente/colaborador")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ClienteResponse> create(@Valid @RequestBody ClienteRequest request) {
        Cliente entity = new Cliente(request.getNome(), request.getEmail());
        Cliente saved = repository.save(entity);
        return ResponseEntity.created(URI.create("/api/v1/clientes/" + saved.getId()))
                .body(toResponse(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ClienteResponse> update(
            @Parameter(description = "ID do cliente") @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setNome(request.getNome());
                    existing.setEmail(request.getEmail());
                    Cliente saved = repository.save(existing);
                    return ResponseEntity.ok(toResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir cliente", description = "Remove um cliente pelo ID")
    @ApiResponse(responseCode = "204", description = "Cliente excluído com sucesso")
    public void delete(@Parameter(description = "ID do cliente") @PathVariable Long id) {
        repository.deleteById(id);
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail()
        );
    }
}
