package com.hardsoft.sistema.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Peca / Produto em estoque.
 * Pode ser usada (consumida) em varias OSs.
 */
@Entity
@Table(name = "tb_pecas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PecaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPeca;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 200)
    private String descricao;

    @Column(length = 50)
    private String marca;

    /**
     * "Novo" ou "Usado"
     */
    @Column(length = 20)
    private String condicao;

    /**
     * BigDecimal e o tipo correto pra dinheiro (precisao exata).
     * double da erros de arredondamento.
     */
    @Column(name = "preco_custo", precision = 10, scale = 2)
    private BigDecimal precoCusto;

    @Column(name = "preco_venda", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;

    @Column(name = "qnt_estoque", nullable = false)
    private Integer qntEstoque = 0;

    @OneToMany(mappedBy = "peca")
    @JsonIgnore
    private List<PecaServicoEntity> usosEmServicos = new ArrayList<>();
}
