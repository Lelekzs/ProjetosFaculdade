package com.hardsoft.sistema.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hardsoft.sistema.entities.OrdemServicoEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoResponseDTO {

    private Long idOrdemServico;
    private LocalDateTime dataEntrada;
    private LocalDateTime dataSaida;
    private String status;
    private String defeito;
    private String solucao;
    private BigDecimal valorTotal;

    private Long idCliente;
    private String nomeCliente;

    private Long idSetup;
    private String descricaoSetup;

    private Long idAdmin;
    private String nomeAdmin;

    public static OrdemServicoResponseDTO fromEntity(OrdemServicoEntity os) {
        String descSetup = null;
        if (os.getSetup() != null) {
            descSetup = (os.getSetup().getMarca() != null ? os.getSetup().getMarca() : "") +
                        " " + (os.getSetup().getModelo() != null ? os.getSetup().getModelo() : "");
            descSetup = descSetup.trim();
        }

        return new OrdemServicoResponseDTO(
            os.getIdOrdemServico(),
            os.getDataEntrada(),
            os.getDataSaida(),
            os.getStatus(),
            os.getDefeito(),
            os.getSolucao(),
            os.getValorTotal(),
            os.getCliente() != null ? os.getCliente().getId() : null,
            os.getCliente() != null ? os.getCliente().getNome() : null,
            os.getSetup() != null ? os.getSetup().getIdSetup() : null,
            descSetup,
            os.getAdmin() != null ? os.getAdmin().getId() : null,
            os.getAdmin() != null ? os.getAdmin().getNome() : null
        );
    }
}
