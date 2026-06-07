package com.hardsoft.sistema.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hardsoft.sistema.dtos.ClienteRequestDTO;
import com.hardsoft.sistema.dtos.ClienteResponseDTO;
import com.hardsoft.sistema.entities.ClienteEntity;
import com.hardsoft.sistema.exceptions.BusinessException;
import com.hardsoft.sistema.exceptions.ResourceNotFoundException;
import com.hardsoft.sistema.repositories.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(ClienteResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        ClienteEntity c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        return ClienteResponseDTO.fromEntity(c);
    }

    @Transactional
    public ClienteResponseDTO salvar(ClienteRequestDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email ja cadastrado: " + dto.getEmail());
        }
        if (repository.existsByCpfCnpj(dto.getCpfCnpj())) {
            throw new BusinessException("CPF/CNPJ ja cadastrado: " + dto.getCpfCnpj());
        }

        ClienteEntity c = new ClienteEntity();
        aplicarDto(c, dto);
        // TODO: quando adicionar Spring Security, fazer hash BCrypt aqui
        c.setSenha(dto.getSenha());

        return ClienteResponseDTO.fromEntity(repository.save(c));
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        ClienteEntity c = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));

        // Verifica conflitos de unicidade, ignorando o proprio registro
        repository.findByEmail(dto.getEmail()).ifPresent(outro -> {
            if (!outro.getId().equals(id)) {
                throw new BusinessException("Email ja cadastrado em outro cliente.");
            }
        });
        repository.findByCpfCnpj(dto.getCpfCnpj()).ifPresent(outro -> {
            if (!outro.getId().equals(id)) {
                throw new BusinessException("CPF/CNPJ ja cadastrado em outro cliente.");
            }
        });

        aplicarDto(c, dto);
        // Senha so atualiza se foi enviada (mas DTO marca como NotBlank;
        // num cenario real teriamos um DTO de update separado)
        c.setSenha(dto.getSenha());

        return ClienteResponseDTO.fromEntity(repository.save(c));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente", id);
        }
        repository.deleteById(id);
    }

    private void aplicarDto(ClienteEntity c, ClienteRequestDTO dto) {
        c.setNome(dto.getNome());
        c.setEmail(dto.getEmail());
        c.setCpfCnpj(dto.getCpfCnpj());
        c.setRg(dto.getRg());
        c.setTelefone(dto.getTelefone());
        c.setEndereco(dto.getEndereco());
    }
}
