package com.hardsoft.sistema.controllers;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hardsoft.sistema.dtos.OrdemServicoRequestDTO;
import com.hardsoft.sistema.dtos.OrdemServicoResponseDTO;
import com.hardsoft.sistema.exceptions.BusinessException;
import com.hardsoft.sistema.services.OrdemServicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ordens-servico")
public class OrdemServicoController {

    @Autowired
    private OrdemServicoService service;

    // GET /api/ordens-servico
    // GET /api/ordens-servico?status=ABERTA
    @GetMapping
    public ResponseEntity<List<OrdemServicoResponseDTO>> listar(
            @RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(service.listarPorStatus(status));
        }
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // GET /api/ordens-servico/cliente/3 — historico de OS do cliente 3
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<OrdemServicoResponseDTO>> listarPorCliente(
            @PathVariable Long idCliente) {
        return ResponseEntity.ok(service.listarPorCliente(idCliente));
    }

    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> criar(
            @Valid @RequestBody OrdemServicoRequestDTO dto) {
        OrdemServicoResponseDTO criada = service.salvar(dto);
        return ResponseEntity.created(
                URI.create("/api/ordens-servico/" + criada.getIdOrdemServico())
        ).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> atualizar(@PathVariable Long id,
                                                             @Valid @RequestBody OrdemServicoRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    // PATCH /api/ordens-servico/5/status
    // Body: { "status": "CONCLUIDA" }
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrdemServicoResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String novoStatus = body.get("status");
        if (novoStatus == null || novoStatus.isBlank()) {
            throw new BusinessException("Campo 'status' e obrigatorio no corpo.");
        }
        return ResponseEntity.ok(service.atualizarStatus(id, novoStatus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
