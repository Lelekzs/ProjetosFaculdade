package com.hardsoft.sistema.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hardsoft.sistema.dtos.SetupRequestDTO;
import com.hardsoft.sistema.dtos.SetupResponseDTO;
import com.hardsoft.sistema.entities.ClienteEntity;
import com.hardsoft.sistema.entities.SetupEntity;
import com.hardsoft.sistema.exceptions.ResourceNotFoundException;
import com.hardsoft.sistema.repositories.ClienteRepository;
import com.hardsoft.sistema.repositories.SetupRepository;

@Service
public class SetupService {

    @Autowired
    private SetupRepository repository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<SetupResponseDTO> listarTodos() {
        return repository.findAll().stream().map(SetupResponseDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<SetupResponseDTO> listarPorCliente(Long idCliente) {
        if (!clienteRepository.existsById(idCliente)) {
            throw new ResourceNotFoundException("Cliente", idCliente);
        }
        return repository.findByClienteId(idCliente).stream()
                .map(SetupResponseDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public SetupResponseDTO buscarPorId(Long id) {
        return SetupResponseDTO.fromEntity(
            repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setup", id))
        );
    }

    @Transactional
    public SetupResponseDTO salvar(SetupRequestDTO dto) {
        ClienteEntity cliente = clienteRepository.findById(dto.getIdCliente())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente", dto.getIdCliente()));

        SetupEntity s = new SetupEntity();
        aplicarDto(s, dto);
        s.setCliente(cliente);
        return SetupResponseDTO.fromEntity(repository.save(s));
    }

    @Transactional
    public SetupResponseDTO atualizar(Long id, SetupRequestDTO dto) {
        SetupEntity s = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Setup", id));

        // Permite trocar de cliente se necessario
        if (!s.getCliente().getId().equals(dto.getIdCliente())) {
            ClienteEntity novoCliente = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", dto.getIdCliente()));
            s.setCliente(novoCliente);
        }

        aplicarDto(s, dto);
        return SetupResponseDTO.fromEntity(repository.save(s));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Setup", id);
        }
        repository.deleteById(id);
    }

    private void aplicarDto(SetupEntity s, SetupRequestDTO dto) {
        s.setMarca(dto.getMarca());
        s.setModelo(dto.getModelo());
        s.setProcessador(dto.getProcessador());
        s.setMemoria(dto.getMemoria());
        s.setPlacaDeVideo(dto.getPlacaDeVideo());
        s.setArmazenamento(dto.getArmazenamento());
    }
}
