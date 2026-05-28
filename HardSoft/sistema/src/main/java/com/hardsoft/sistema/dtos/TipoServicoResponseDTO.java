package com.hardsoft.sistema.dtos;

import java.math.BigDecimal;

import com.hardsoft.sistema.entities.TipoServicoEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoServicoResponseDTO {

    private Long idTipoServico;
    private String nome;
    private String descricao;
    private BigDecimal valorBase;

    public static TipoServicoResponseDTO fromEntity(TipoServicoEntity t) {
        return new TipoServicoResponseDTO(
            t.getIdTipoServico(),
            t.getNome(),
            t.getDescricao(),
            t.getValorBase()
        );
    }
}
