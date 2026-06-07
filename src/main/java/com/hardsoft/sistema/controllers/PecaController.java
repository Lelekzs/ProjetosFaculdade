package com.hardsoft.sistema.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hardsoft.sistema.dtos.PecaRequestDTO;
import com.hardsoft.sistema.dtos.PecaResponseDTO;
import com.hardsoft.sistema.services.PecaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pecas")
public class PecaController {

    @Autowired
    private PecaService service;

    // GET /api/pecas               -> lista todas
    // GET /api/pecas?nome=memoria  -> busca por nome (parcial, case insensitive)
    @GetMapping
    public ResponseEntity<List<PecaResponseDTO>> listar(
            @RequestParam(required = false) String nome) {
        if (nome != null && !nome.isBlank()) {
            return ResponseEntity.ok(service.buscarPorNome(nome));
        }
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PecaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // GET /api/pecas/estoque-baixo?limite=5
    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<PecaResponseDTO>> estoqueBaixo(
            @RequestParam(defaultValue = "5") Integer limite) {
        return ResponseEntity.ok(service.estoqueBaixo(limite));
    }

    @PostMapping
    public ResponseEntity<PecaResponseDTO> criar(@Valid @RequestBody PecaRequestDTO dto) {
        PecaResponseDTO criado = service.salvar(dto);
        return ResponseEntity.created(URI.create("/api/pecas/" + criado.getIdPeca())).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PecaResponseDTO> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody PecaRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
