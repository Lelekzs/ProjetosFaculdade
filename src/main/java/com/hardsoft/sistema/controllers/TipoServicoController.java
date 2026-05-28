package com.hardsoft.sistema.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hardsoft.sistema.dtos.TipoServicoRequestDTO;
import com.hardsoft.sistema.dtos.TipoServicoResponseDTO;
import com.hardsoft.sistema.services.TipoServicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tipos-servico")
public class TipoServicoController {

    @Autowired
    private TipoServicoService service;

    @GetMapping
    public ResponseEntity<List<TipoServicoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoServicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<TipoServicoResponseDTO> criar(@Valid @RequestBody TipoServicoRequestDTO dto) {
        TipoServicoResponseDTO criado = service.salvar(dto);
        return ResponseEntity.created(URI.create("/api/tipos-servico/" + criado.getIdTipoServico()))
                             .body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoServicoResponseDTO> atualizar(@PathVariable Long id,
                                                            @Valid @RequestBody TipoServicoRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
