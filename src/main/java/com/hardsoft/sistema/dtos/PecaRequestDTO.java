package com.hardsoft.sistema.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PecaRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String nome;

    @Size(max = 200)
    private String descricao;

    @Size(max = 50)
    private String marca;

    @Size(max = 20)
    private String condicao;

    @DecimalMin(value = "0.0", inclusive = true, message = "precoCusto nao pode ser negativo")
    private BigDecimal precoCusto;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true, message = "precoVenda nao pode ser negativo")
    private BigDecimal precoVenda;

    @NotNull
    @Min(value = 0, message = "qntEstoque nao pode ser negativo")
    private Integer qntEstoque;
}
