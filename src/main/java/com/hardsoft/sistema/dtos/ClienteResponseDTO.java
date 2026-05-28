package com.hardsoft.sistema.dtos;

import java.time.LocalDate;

import com.hardsoft.sistema.entities.ClienteEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String cpfCnpj;
    private String rg;
    private String telefone;
    private String endereco;
    private LocalDate dataCadastro;

    public static ClienteResponseDTO fromEntity(ClienteEntity c) {
        return new ClienteResponseDTO(
            c.getId(),
            c.getNome(),
            c.getEmail(),
            c.getCpfCnpj(),
            c.getRg(),
            c.getTelefone(),
            c.getEndereco(),
            c.getDataCadastro()
        );
    }
}
