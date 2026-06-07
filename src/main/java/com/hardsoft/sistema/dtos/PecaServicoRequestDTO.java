package com.hardsoft.sistema.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PecaServicoRequestDTO {

    @NotNull
    private Long idServico;

    @NotNull
    private Long idPeca;

    @NotNull
    @Min(1)
    private Integer qntVendida;
}
