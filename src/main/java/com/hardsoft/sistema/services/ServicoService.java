package com.hardsoft.sistema.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hardsoft.sistema.dtos.ServicoRequestDTO;
import com.hardsoft.sistema.dtos.ServicoResponseDTO;
import com.hardsoft.sistema.entities.OrdemServicoEntity;
import com.hardsoft.sistema.entities.ServicoEntity;
import com.hardsoft.sistema.entities.TipoServicoEntity;
import com.hardsoft.sistema.exceptions.ResourceNotFoundException;
import com.hardsoft.sistema.repositories.OrdemServicoRepository;
import com.hardsoft.sistema.repositories.ServicoRepository;
import com.hardsoft.sistema.repositories.TipoServicoRepository;

@Service
public class ServicoService {

    @Autowired
    private ServicoRepository repository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private TipoServicoRepository tipoServicoRepository;

    @Autowired
    private OrdemServicoService ordemServicoService;

    @Transactional(readOnly = true)
    public List<ServicoResponseDTO> listarPorOrdemServico(Long idOrdemServico) {
        if (!ordemServicoRepository.existsById(idOrdemServico)) {
            throw new ResourceNotFoundException("OrdemServico", idOrdemServico);
        }
        return repository.findByOrdemServicoIdOrdemServico(idOrdemServico).stream()
                .map(ServicoResponseDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public ServicoResponseDTO buscarPorId(Long id) {
        return ServicoResponseDTO.fromEntity(
            repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servico", id))
        );
    }

    @Transactional
    public ServicoResponseDTO salvar(ServicoRequestDTO dto) {
        OrdemServicoEntity os = ordemServicoRepository.findById(dto.getIdOrdemServico())
            .orElseThrow(() -> new ResourceNotFoundException("OrdemServico", dto.getIdOrdemServico()));

        TipoServicoEntity tipo = tipoServicoRepository.findById(dto.getIdTipoServico())
            .orElseThrow(() -> new ResourceNotFoundException("TipoServico", dto.getIdTipoServico()));

        ServicoEntity s = new ServicoEntity();
        s.setOrdemServico(os);
        s.setTipoServico(tipo);
        s.setDescricaoServico(dto.getDescricaoServico());
        s.setPrecoMaoDeObra(dto.getPrecoMaoDeObra());

        ServicoEntity salvo = repository.save(s);
        ordemServicoService.recalcularValorTotal(os.getIdOrdemServico());
        return ServicoResponseDTO.fromEntity(salvo);
    }

    @Transactional
    public ServicoResponseDTO atualizar(Long id, ServicoRequestDTO dto) {
        ServicoEntity s = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Servico", id));

        TipoServicoEntity tipo = tipoServicoRepository.findById(dto.getIdTipoServico())
            .orElseThrow(() -> new ResourceNotFoundException("TipoServico", dto.getIdTipoServico()));

        s.setTipoServico(tipo);
        s.setDescricaoServico(dto.getDescricaoServico());
        s.setPrecoMaoDeObra(dto.getPrecoMaoDeObra());

        ServicoEntity salvo = repository.save(s);
        ordemServicoService.recalcularValorTotal(s.getOrdemServico().getIdOrdemServico());
        return ServicoResponseDTO.fromEntity(salvo);
    }

    @Transactional
    public void deletar(Long id) {
        ServicoEntity s = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Servico", id));
        Long idOS = s.getOrdemServico().getIdOrdemServico();
        repository.delete(s);
        ordemServicoService.recalcularValorTotal(idOS);
    }
}
