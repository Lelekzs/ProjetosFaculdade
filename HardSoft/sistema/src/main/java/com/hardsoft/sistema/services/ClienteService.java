package com.hardsoft.sistema.services;

import com.hardsoft.sistema.entities.ClienteEntity;
import com.hardsoft.sistema.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    // READ: Busca todos
    public List<ClienteEntity> listarTodos() {
        return repository.findAll();
    }

    // READ: Busca por ID
    public Optional<ClienteEntity> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // CREATE: Salva um novo cliente
    public ClienteEntity salvar(ClienteEntity cliente) {
        return repository.save(cliente);
    }

    // UPDATE: Atualiza um cliente existente
    public ClienteEntity atualizar(Long id, ClienteEntity clienteAtualizado) {
        if (repository.existsById(id)) {
            clienteAtualizado.setId(id); // Garante que vai atualizar o ID certo
            return repository.save(clienteAtualizado);
        }
        return null; // Aqui num projeto real lançaríamos uma exceção
    }

    // DELETE: Apaga um cliente
    public boolean deletar(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}