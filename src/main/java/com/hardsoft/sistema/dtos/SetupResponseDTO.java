package com.hardsoft.sistema.dtos;

import com.hardsoft.sistema.entities.SetupEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetupResponseDTO {

    private Long idSetup;
    private Long idCliente;
    private String nomeCliente;
    private String marca;
    private String modelo;
    private String processador;
    private String memoria;
    private String placaDeVideo;
    private String armazenamento;

    public static SetupResponseDTO fromEntity(SetupEntity s) {
        return new SetupResponseDTO(
            s.getIdSetup(),
            s.getCliente() != null ? s.getCliente().getId() : null,
            s.getCliente() != null ? s.getCliente().getNome() : null,
            s.getMarca(),
            s.getModelo(),
            s.getProcessador(),
            s.getMemoria(),
            s.getPlacaDeVideo(),
            s.getArmazenamento()
        );
    }
}
