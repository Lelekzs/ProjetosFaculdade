package com.hardsoft.sistema.dtos;

import java.time.LocalDate;

import com.hardsoft.sistema.entities.AdminEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String endereco;
    private String cargo;
    private LocalDate dataCadastro;

    public static AdminResponseDTO fromEntity(AdminEntity a) {
        return new AdminResponseDTO(
            a.getId(),
            a.getNome(),
            a.getEmail(),
            a.getTelefone(),
            a.getEndereco(),
            a.getCargo(),
            a.getDataCadastro()
        );
    }
}
