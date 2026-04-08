package com.hardsoft.sistema.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tdpecas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PecaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPeca;

    @Column(nullable = false, length = 100)
    private String descricao;

    @Column(length = 50)
    private String marca;

    @Column(length = 20)
    private String condicao; // Novo ou Usado
    
    private double precoVenda;

    private int qtdEstoque;
}