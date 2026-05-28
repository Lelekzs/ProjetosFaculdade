package com.hardsoft.sistema.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoRequestDTO {

    @NotNull(message = "idCliente e obrigatorio")
    private Long idCliente;

    @NotNull(message = "idSetup e obrigatorio")
    private Long idSetup;

    @NotNull(message = "idAdmin e obrigatorio")
    private Long idAdmin;

    private String defeito;

    private String solucao;

    /**
     * Opcional na criacao — default e "ABERTA" (definido na entidade via @PrePersist).
     */
    private String status;
}
