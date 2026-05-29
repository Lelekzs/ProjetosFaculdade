package com.hardsoft.sistema.dtos;

import java.math.BigDecimal;

import com.hardsoft.sistema.entities.PecaEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PecaResponseDTO {

    private Long idPeca;
    private String nome;
    private String descricao;
    private String marca;
    private String condicao;
    private BigDecimal precoCusto;
    private BigDecimal precoVenda;
    private Integer qntEstoque;

    public static PecaResponseDTO fromEntity(PecaEntity p) {
        return new PecaResponseDTO(
            p.getIdPeca(),
            p.getNome(),
            p.getDescricao(),
            p.getMarca(),
            p.getCondicao(),
            p.getPrecoCusto(),
            p.getPrecoVenda(),
            p.getQntEstoque()
        );
    }
}
