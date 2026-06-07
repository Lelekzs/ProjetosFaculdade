package com.hardsoft.sistema.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hardsoft.sistema.dtos.PecaRequestDTO;
import com.hardsoft.sistema.dtos.PecaResponseDTO;
import com.hardsoft.sistema.entities.PecaEntity;
import com.hardsoft.sistema.exceptions.ResourceNotFoundException;
import com.hardsoft.sistema.repositories.PecaRepository;

@Service
public class PecaService {

    @Autowired
    private PecaRepository repository;

    @Transactional(readOnly = true)
    public List<PecaResponseDTO> listarTodos() {
        return repository.findAll().stream().map(PecaResponseDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public PecaResponseDTO buscarPorId(Long id) {
        return PecaResponseDTO.fromEntity(
            repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Peca", id))
        );
    }

    @Transactional(readOnly = true)
    public List<PecaResponseDTO> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome).stream()
                .map(PecaResponseDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<PecaResponseDTO> estoqueBaixo(Integer limite) {
        return repository.findByQntEstoqueLessThan(limite).stream()
                .map(PecaResponseDTO::fromEntity).toList();
    }

    @Transactional
    public PecaResponseDTO salvar(PecaRequestDTO dto) {
        PecaEntity p = new PecaEntity();
        aplicarDto(p, dto);
        return PecaResponseDTO.fromEntity(repository.save(p));
    }

    @Transactional
    public PecaResponseDTO atualizar(Long id, PecaRequestDTO dto) {
        PecaEntity p = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Peca", id));
        aplicarDto(p, dto);
        return PecaResponseDTO.fromEntity(repository.save(p));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Peca", id);
        }
        repository.deleteById(id);
    }

    private void aplicarDto(PecaEntity p, PecaRequestDTO dto) {
        p.setNome(dto.getNome());
        p.setDescricao(dto.getDescricao());
        p.setMarca(dto.getMarca());
        p.setCondicao(dto.getCondicao());
        p.setPrecoCusto(dto.getPrecoCusto());
        p.setPrecoVenda(dto.getPrecoVenda());
        p.setQntEstoque(dto.getQntEstoque());
    }
}
