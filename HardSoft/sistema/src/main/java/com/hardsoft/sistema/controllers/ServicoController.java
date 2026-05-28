package com.hardsoft.sistema.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hardsoft.sistema.dtos.ServicoRequestDTO;
import com.hardsoft.sistema.dtos.ServicoResponseDTO;
import com.hardsoft.sistema.services.ServicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    @Autowired
    private ServicoService service;

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // GET /api/servicos/ordem-servico/5 — lista servicos da OS 5
    @GetMapping("/ordem-servico/{idOrdemServico}")
    public ResponseEntity<List<ServicoResponseDTO>> listarPorOrdemServico(
            @PathVariable Long idOrdemServico) {
        return ResponseEntity.ok(service.listarPorOrdemServico(idOrdemServico));
    }

    @PostMapping
    public ResponseEntity<ServicoResponseDTO> criar(@Valid @RequestBody ServicoRequestDTO dto) {
        ServicoResponseDTO criado = service.salvar(dto);
        return ResponseEntity.created(URI.create("/api/servicos/" + criado.getIdServico()))
                             .body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody ServicoRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
