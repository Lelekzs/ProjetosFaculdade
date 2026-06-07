package com.hardsoft.sistema.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRequestDTO {

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 100)
    private String nome;

    @NotBlank @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(min = 6, max = 60)
    private String senha;

    @Size(max = 20)
    private String telefone;

    @Size(max = 200)
    private String endereco;

    @Size(max = 50)
    private String cargo;
}
