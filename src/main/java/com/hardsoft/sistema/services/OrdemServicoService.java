package com.hardsoft.sistema.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hardsoft.sistema.dtos.OrdemServicoRequestDTO;
import com.hardsoft.sistema.dtos.OrdemServicoResponseDTO;
import com.hardsoft.sistema.entities.AdminEntity;
import com.hardsoft.sistema.entities.ClienteEntity;
import com.hardsoft.sistema.entities.OrdemServicoEntity;
import com.hardsoft.sistema.entities.PecaServicoEntity;
import com.hardsoft.sistema.entities.ServicoEntity;
import com.hardsoft.sistema.entities.SetupEntity;
import com.hardsoft.sistema.exceptions.BusinessException;
import com.hardsoft.sistema.exceptions.ResourceNotFoundException;
import com.hardsoft.sistema.repositories.AdminRepository;
import com.hardsoft.sistema.repositories.ClienteRepository;
import com.hardsoft.sistema.repositories.OrdemServicoRepository;
import com.hardsoft.sistema.repositories.SetupRepository;

@Service
public class OrdemServicoService {

    @Autowired
    private OrdemServicoRepository repository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private SetupRepository setupRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> listarTodos() {
        return repository.findAll().stream().map(OrdemServicoResponseDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public OrdemServicoResponseDTO buscarPorId(Long id) {
        return OrdemServicoResponseDTO.fromEntity(
            repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrdemServico", id))
        );
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> listarPorCliente(Long idCliente) {
        if (!clienteRepository.existsById(idCliente)) {
            throw new ResourceNotFoundException("Cliente", idCliente);
        }
        return repository.findByClienteId(idCliente).stream()
                .map(OrdemServicoResponseDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> listarPorStatus(String status) {
        return repository.findByStatusIgnoreCase(status).stream()
                .map(OrdemServicoResponseDTO::fromEntity).toList();
    }

    @Transactional
    public OrdemServicoResponseDTO salvar(OrdemServicoRequestDTO dto) {
        ClienteEntity cliente = clienteRepository.findById(dto.getIdCliente())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente", dto.getIdCliente()));

        SetupEntity setup = setupRepository.findById(dto.getIdSetup())
            .orElseThrow(() -> new ResourceNotFoundException("Setup", dto.getIdSetup()));

        // Regra: o setup tem que pertencer ao cliente da OS
        if (!setup.getCliente().getId().equals(cliente.getId())) {
            throw new BusinessException(
                "O setup informado nao pertence ao cliente. Setup id=" + setup.getIdSetup() +
                " pertence ao cliente id=" + setup.getCliente().getId()
            );
        }

        AdminEntity admin = adminRepository.findById(dto.getIdAdmin())
            .orElseThrow(() -> new ResourceNotFoundException("Admin", dto.getIdAdmin()));

        OrdemServicoEntity os = new OrdemServicoEntity();
        os.setCliente(cliente);
        os.setSetup(setup);
        os.setAdmin(admin);
        os.setDefeito(dto.getDefeito());
        os.setSolucao(dto.getSolucao());
        if (dto.getStatus() != null) {
            os.setStatus(dto.getStatus());
        }

        return OrdemServicoResponseDTO.fromEntity(repository.save(os));
    }

    @Transactional
    public OrdemServicoResponseDTO atualizar(Long id, OrdemServicoRequestDTO dto) {
        OrdemServicoEntity os = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("OrdemServico", id));

        ClienteEntity cliente = clienteRepository.findById(dto.getIdCliente())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente", dto.getIdCliente()));
        SetupEntity setup = setupRepository.findById(dto.getIdSetup())
            .orElseThrow(() -> new ResourceNotFoundException("Setup", dto.getIdSetup()));
        AdminEntity admin = adminRepository.findById(dto.getIdAdmin())
            .orElseThrow(() -> new ResourceNotFoundException("Admin", dto.getIdAdmin()));

        if (!setup.getCliente().getId().equals(cliente.getId())) {
            throw new BusinessException("O setup nao pertence ao cliente informado.");
        }

        os.setCliente(cliente);
        os.setSetup(setup);
        os.setAdmin(admin);
        os.setDefeito(dto.getDefeito());
        os.setSolucao(dto.getSolucao());
        if (dto.getStatus() != null) {
            os.setStatus(dto.getStatus());
        }

        return OrdemServicoResponseDTO.fromEntity(repository.save(os));
    }

    @Transactional
    public OrdemServicoResponseDTO atualizarStatus(Long id, String novoStatus) {
        OrdemServicoEntity os = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("OrdemServico", id));
        os.setStatus(novoStatus);

        if ("CONCLUIDA".equalsIgnoreCase(novoStatus) || "ENTREGUE".equalsIgnoreCase(novoStatus)) {
            if (os.getDataSaida() == null) {
                os.setDataSaida(LocalDateTime.now());
            }
        }

        return OrdemServicoResponseDTO.fromEntity(repository.save(os));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("OrdemServico", id);
        }
        repository.deleteById(id);
    }

    /**
     * Recalcula o valor total da OS: soma da mao de obra dos servicos
     * + soma das pecas usadas (qnt * vlrUnitario).
     * Chamado pelo ServicoService e PecaServicoService apos mudancas.
     */
    @Transactional
    public void recalcularValorTotal(Long idOrdemServico) {
        OrdemServicoEntity os = repository.findById(idOrdemServico)
            .orElseThrow(() -> new ResourceNotFoundException("OrdemServico", idOrdemServico));

        BigDecimal total = BigDecimal.ZERO;
        for (ServicoEntity servico : os.getServicos()) {
            if (servico.getPrecoMaoDeObra() != null) {
                total = total.add(servico.getPrecoMaoDeObra());
            }
            for (PecaServicoEntity ps : servico.getPecasUtilizadas()) {
                if (ps.getVlrUnitarioVenda() != null && ps.getQntVendida() != null) {
                    total = total.add(
                        ps.getVlrUnitarioVenda().multiply(BigDecimal.valueOf(ps.getQntVendida()))
                    );
                }
            }
        }
        os.setValorTotal(total);
        repository.save(os);

        System.out.println("teste");
    }
}
