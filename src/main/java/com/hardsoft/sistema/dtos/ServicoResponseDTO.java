package com.hardsoft.sistema.dtos;

import java.math.BigDecimal;

import com.hardsoft.sistema.entities.ServicoEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicoResponseDTO {

    private Long idServico;
    private Long idOrdemServico;
    private Long idTipoServico;
    private String nomeTipoServico;
    private String descricaoServico;
    private BigDecimal precoMaoDeObra;

    public static ServicoResponseDTO fromEntity(ServicoEntity s) {
        return new ServicoResponseDTO(
            s.getIdServico(),
            s.getOrdemServico() != null ? s.getOrdemServico().getIdOrdemServico() : null,
            s.getTipoServico() != null ? s.getTipoServico().getIdTipoServico() : null,
            s.getTipoServico() != null ? s.getTipoServico().getNome() : null,
            s.getDescricaoServico(),
            s.getPrecoMaoDeObra()
        );
    }
}
