package com.hardsoft.sistema.controllers;

import com.hardsoft.sistema.entities.TipoServicoEntity;
import com.hardsoft.sistema.repositories.TipoServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-servico") // O "endereço" para acessar via navegador
public class TipoServicoController {

    @Autowired
    private TipoServicoRepository repository;

    // Listar todos os serviços do catálogo
    @GetMapping
    public List<TipoServicoEntity> listarTodos() {
        return repository.findAll();
    }

    // Cadastrar um novo serviço (ex: Formatação)
    @PostMapping
    public TipoServicoEntity salvar(@RequestBody TipoServicoEntity tipoServico) {
        return repository.save(tipoServico);
    }
}