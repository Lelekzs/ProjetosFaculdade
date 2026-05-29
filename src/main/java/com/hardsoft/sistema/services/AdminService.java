package com.hardsoft.sistema.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hardsoft.sistema.dtos.AdminRequestDTO;
import com.hardsoft.sistema.dtos.AdminResponseDTO;
import com.hardsoft.sistema.entities.AdminEntity;
import com.hardsoft.sistema.exceptions.BusinessException;
import com.hardsoft.sistema.exceptions.ResourceNotFoundException;
import com.hardsoft.sistema.repositories.AdminRepository;

@Service
public class AdminService {

    @Autowired
    private AdminRepository repository;

    @Transactional(readOnly = true)
    public List<AdminResponseDTO> listarTodos() {
        return repository.findAll().stream().map(AdminResponseDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public AdminResponseDTO buscarPorId(Long id) {
        return AdminResponseDTO.fromEntity(
            repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", id))
        );
    }

    @Transactional
    public AdminResponseDTO salvar(AdminRequestDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email ja cadastrado: " + dto.getEmail());
        }
        AdminEntity a = new AdminEntity();
        aplicarDto(a, dto);
        a.setSenha(dto.getSenha());
        return AdminResponseDTO.fromEntity(repository.save(a));
    }

    @Transactional
    public AdminResponseDTO atualizar(Long id, AdminRequestDTO dto) {
        AdminEntity a = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Admin", id));

        repository.findByEmail(dto.getEmail()).ifPresent(outro -> {
            if (!outro.getId().equals(id)) {
                throw new BusinessException("Email ja cadastrado em outro admin.");
            }
        });

        aplicarDto(a, dto);
        a.setSenha(dto.getSenha());
        return AdminResponseDTO.fromEntity(repository.save(a));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Admin", id);
        }
        repository.deleteById(id);
    }

    private void aplicarDto(AdminEntity a, AdminRequestDTO dto) {
        a.setNome(dto.getNome());
        a.setEmail(dto.getEmail());
        a.setTelefone(dto.getTelefone());
        a.setEndereco(dto.getEndereco());
        a.setCargo(dto.getCargo());
    }
}
