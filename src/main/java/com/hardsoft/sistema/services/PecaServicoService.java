package com.hardsoft.sistema.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hardsoft.sistema.dtos.PecaServicoRequestDTO;
import com.hardsoft.sistema.dtos.PecaServicoResponseDTO;
import com.hardsoft.sistema.entities.PecaEntity;
import com.hardsoft.sistema.entities.PecaServicoEntity;
import com.hardsoft.sistema.entities.ServicoEntity;
import com.hardsoft.sistema.exceptions.BusinessException;
import com.hardsoft.sistema.exceptions.ResourceNotFoundException;
import com.hardsoft.sistema.repositories.PecaRepository;
import com.hardsoft.sistema.repositories.PecaServicoRepository;
import com.hardsoft.sistema.repositories.ServicoRepository;

@Service
public class PecaServicoService {

    @Autowired
    private PecaServicoRepository repository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private PecaRepository pecaRepository;

    @Autowired
    private OrdemServicoService ordemServicoService;

    @Transactional(readOnly = true)
    public List<PecaServicoResponseDTO> listarPorServico(Long idServico) {
        if (!servicoRepository.existsById(idServico)) {
            throw new ResourceNotFoundException("Servico", idServico);
        }
        return repository.findByServicoIdServico(idServico).stream()
                .map(PecaServicoResponseDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public PecaServicoResponseDTO buscarPorId(Long id) {
        return PecaServicoResponseDTO.fromEntity(
            repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PecaServico", id))
        );
    }

    @Transactional
    public PecaServicoResponseDTO salvar(PecaServicoRequestDTO dto) {
        ServicoEntity servico = servicoRepository.findById(dto.getIdServico())
            .orElseThrow(() -> new ResourceNotFoundException("Servico", dto.getIdServico()));

        PecaEntity peca = pecaRepository.findById(dto.getIdPeca())
            .orElseThrow(() -> new ResourceNotFoundException("Peca", dto.getIdPeca()));

        // Regra: nao deixa vender peca sem estoque
        if (peca.getQntEstoque() < dto.getQntVendida()) {
            throw new BusinessException(
                "Estoque insuficiente para a peca '" + peca.getNome() + "'. " +
                "Disponivel: " + peca.getQntEstoque() + ", solicitado: " + dto.getQntVendida()
            );
        }

        // Da baixa no estoque
        peca.setQntEstoque(peca.getQntEstoque() - dto.getQntVendida());
        pecaRepository.save(peca);

        PecaServicoEntity ps = new PecaServicoEntity();
        ps.setServico(servico);
        ps.setPeca(peca);
        ps.setQntVendida(dto.getQntVendida());
        // Congela o preco no momento da venda
        ps.setVlrUnitarioVenda(peca.getPrecoVenda());

        PecaServicoEntity salvo = repository.save(ps);
        ordemServicoService.recalcularValorTotal(servico.getOrdemServico().getIdOrdemServico());
        return PecaServicoResponseDTO.fromEntity(salvo);
    }

    @Transactional
    public void deletar(Long id) {
        PecaServicoEntity ps = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("PecaServico", id));

        // Devolve as pecas pro estoque
        PecaEntity peca = ps.getPeca();
        peca.setQntEstoque(peca.getQntEstoque() + ps.getQntVendida());
        pecaRepository.save(peca);

        Long idOS = ps.getServico().getOrdemServico().getIdOrdemServico();
        repository.delete(ps);
        ordemServicoService.recalcularValorTotal(idOS);
    }
}
