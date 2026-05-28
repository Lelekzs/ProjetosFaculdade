package com.hardsoft.sistema.entities;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Catalogo de tipos de servico oferecidos.
 * Exemplos: "Formatacao", "Limpeza Interna", "Troca de Pasta Termica".
 */
@Entity
@Table(name = "tb_tipos_servico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoServico;

    @Column(nullable = false, length = 100, unique = true)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Column(name = "valor_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorBase;
}
