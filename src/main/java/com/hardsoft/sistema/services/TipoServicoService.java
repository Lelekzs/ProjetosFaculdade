package com.hardsoft.sistema.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hardsoft.sistema.dtos.TipoServicoRequestDTO;
import com.hardsoft.sistema.dtos.TipoServicoResponseDTO;
import com.hardsoft.sistema.entities.TipoServicoEntity;
import com.hardsoft.sistema.exceptions.BusinessException;
import com.hardsoft.sistema.exceptions.ResourceNotFoundException;
import com.hardsoft.sistema.repositories.TipoServicoRepository;

@Service
public class TipoServicoService {

    @Autowired
    private TipoServicoRepository repository;

    @Transactional(readOnly = true)
    public List<TipoServicoResponseDTO> listarTodos() {
        return repository.findAll().stream().map(TipoServicoResponseDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public TipoServicoResponseDTO buscarPorId(Long id) {
        return TipoServicoResponseDTO.fromEntity(
            repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoServico", id))
        );
    }

    @Transactional
    public TipoServicoResponseDTO salvar(TipoServicoRequestDTO dto) {
        if (repository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new BusinessException("Tipo de servico ja cadastrado: " + dto.getNome());
        }
        TipoServicoEntity t = new TipoServicoEntity();
        aplicarDto(t, dto);
        return TipoServicoResponseDTO.fromEntity(repository.save(t));
    }

    @Transactional
    public TipoServicoResponseDTO atualizar(Long id, TipoServicoRequestDTO dto) {
        TipoServicoEntity t = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("TipoServico", id));

        repository.findByNomeIgnoreCase(dto.getNome()).ifPresent(outro -> {
            if (!outro.getIdTipoServico().equals(id)) {
                throw new BusinessException("Tipo de servico ja cadastrado com esse nome.");
            }
        });

        aplicarDto(t, dto);
        return TipoServicoResponseDTO.fromEntity(repository.save(t));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("TipoServico", id);
        }
        repository.deleteById(id);
    }

    private void aplicarDto(TipoServicoEntity t, TipoServicoRequestDTO dto) {
        t.setNome(dto.getNome());
        t.setDescricao(dto.getDescricao());
        t.setValorBase(dto.getValorBase());
    }
}
