package com.hardsoft.sistema.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetupRequestDTO {

    @NotNull(message = "idCliente e obrigatorio")
    private Long idCliente;

    @Size(max = 50)
    private String marca;

    @Size(max = 50)
    private String modelo;

    @Size(max = 100)
    private String processador;

    @Size(max = 50)
    private String memoria;

    @Size(max = 100)
    private String placaDeVideo;

    @Size(max = 100)
    private String armazenamento;
}
