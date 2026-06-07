package com.hardsoft.sistema.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hardsoft.sistema.dtos.SetupRequestDTO;
import com.hardsoft.sistema.dtos.SetupResponseDTO;
import com.hardsoft.sistema.services.SetupService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/setups")
public class SetupController {

    @Autowired
    private SetupService service;

    @GetMapping
    public ResponseEntity<List<SetupResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SetupResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // GET /api/setups/cliente/5 — lista todos os setups do cliente 5
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<SetupResponseDTO>> listarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(service.listarPorCliente(idCliente));
    }

    @PostMapping
    public ResponseEntity<SetupResponseDTO> criar(@Valid @RequestBody SetupRequestDTO dto) {
        SetupResponseDTO criado = service.salvar(dto);
        return ResponseEntity.created(URI.create("/api/setups/" + criado.getIdSetup())).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SetupResponseDTO> atualizar(@PathVariable Long id,
                                                      @Valid @RequestBody SetupRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
