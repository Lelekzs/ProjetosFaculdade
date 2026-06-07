package com.hardsoft.sistema.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestDTO {

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "Email e obrigatorio")
    @Email(message = "Email invalido")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Senha e obrigatoria")
    @Size(min = 6, max = 60, message = "Senha deve ter entre 6 e 60 caracteres")
    private String senha;

    @NotBlank(message = "CPF/CNPJ e obrigatorio")
    @Pattern(regexp = "\\d{11}|\\d{14}",
             message = "CPF deve ter 11 digitos ou CNPJ deve ter 14 (apenas numeros)")
    private String cpfCnpj;

    @Size(max = 20)
    private String rg;

    @Size(max = 20)
    private String telefone;

    @Size(max = 200)
    private String endereco;
}
