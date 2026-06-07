package com.hardsoft.sistema.dtos;

import java.math.BigDecimal;

import com.hardsoft.sistema.entities.PecaServicoEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PecaServicoResponseDTO {

    private Long idPecaServico;
    private Long idServico;
    private Long idPeca;
    private String nomePeca;
    private Integer qntVendida;
    private BigDecimal vlrUnitarioVenda;
    private BigDecimal subtotal;

    public static PecaServicoResponseDTO fromEntity(PecaServicoEntity ps) {
        BigDecimal subtotal = ps.getVlrUnitarioVenda() != null && ps.getQntVendida() != null
            ? ps.getVlrUnitarioVenda().multiply(BigDecimal.valueOf(ps.getQntVendida()))
            : BigDecimal.ZERO;

        return new PecaServicoResponseDTO(
            ps.getIdPecaServico(),
            ps.getServico() != null ? ps.getServico().getIdServico() : null,
            ps.getPeca() != null ? ps.getPeca().getIdPeca() : null,
            ps.getPeca() != null ? ps.getPeca().getNome() : null,
            ps.getQntVendida(),
            ps.getVlrUnitarioVenda(),
            subtotal
        );
    }
}
