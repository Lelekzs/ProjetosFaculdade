package com.hardsoft.sistema.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hardsoft.sistema.dtos.PecaServicoRequestDTO;
import com.hardsoft.sistema.dtos.PecaServicoResponseDTO;
import com.hardsoft.sistema.services.PecaServicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pecas-servico")
public class PecaServicoController {

    @Autowired
    private PecaServicoService service;

    @GetMapping("/{id}")
    public ResponseEntity<PecaServicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // GET /api/pecas-servico/servico/8 — lista pecas usadas no servico 8
    @GetMapping("/servico/{idServico}")
    public ResponseEntity<List<PecaServicoResponseDTO>> listarPorServico(
            @PathVariable Long idServico) {
        return ResponseEntity.ok(service.listarPorServico(idServico));
    }

    @PostMapping
    public ResponseEntity<PecaServicoResponseDTO> criar(
            @Valid @RequestBody PecaServicoRequestDTO dto) {
        PecaServicoResponseDTO criado = service.salvar(dto);
        return ResponseEntity.created(URI.create("/api/pecas-servico/" + criado.getIdPecaServico()))
                             .body(criado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
