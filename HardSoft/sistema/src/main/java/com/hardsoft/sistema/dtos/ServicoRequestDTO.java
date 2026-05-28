package com.hardsoft.sistema.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicoRequestDTO {

    @NotNull
    private Long idOrdemServico;

    @NotNull
    private Long idTipoServico;

    private String descricaoServico;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal precoMaoDeObra;
}
