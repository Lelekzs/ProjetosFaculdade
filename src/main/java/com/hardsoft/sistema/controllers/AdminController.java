package com.hardsoft.sistema.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hardsoft.sistema.dtos.AdminRequestDTO;
import com.hardsoft.sistema.dtos.AdminResponseDTO;
import com.hardsoft.sistema.services.AdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    private AdminService service;

    @GetMapping
    public ResponseEntity<List<AdminResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AdminResponseDTO> criar(@Valid @RequestBody AdminRequestDTO dto) {
        AdminResponseDTO criado = service.salvar(dto);
        return ResponseEntity.created(URI.create("/api/admins/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminResponseDTO> atualizar(@PathVariable Long id,
                                                      @Valid @RequestBody AdminRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
