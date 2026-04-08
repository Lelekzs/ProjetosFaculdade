package com.hardsoft.sistema.controllers;

import com.hardsoft.sistema.entities.ClienteEntity;
import com.hardsoft.sistema.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    // 1. CREATE (POST) - Rota: /api/clientes
    @PostMapping
    public ResponseEntity<ClienteEntity> criar(@RequestBody ClienteEntity cliente) {
        ClienteEntity novoCliente = service.salvar(cliente);
        return new ResponseEntity<>(novoCliente, HttpStatus.CREATED);
    }

    // 2. READ ALL (GET) - Rota: /api/clientes
    @GetMapping
    public ResponseEntity<List<ClienteEntity>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // 3. READ BY ID (GET) - Rota: /api/clientes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ClienteEntity> buscarPorId(@PathVariable Long id) {
        Optional<ClienteEntity> cliente = service.buscarPorId(id);
        return cliente.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 4. UPDATE (PUT) - Rota: /api/clientes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ClienteEntity> atualizar(@PathVariable Long id, @RequestBody ClienteEntity cliente) {
        ClienteEntity clienteAtualizado = service.atualizar(id, cliente);
        if (clienteAtualizado != null) {
            return ResponseEntity.ok(clienteAtualizado);
        }
        return ResponseEntity.notFound().build();
    }

    // 5. DELETE (DELETE) - Rota: /api/clientes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (service.deletar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}