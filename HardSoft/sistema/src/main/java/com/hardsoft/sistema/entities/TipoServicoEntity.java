package com.hardsoft.sistema.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tdtipos_servico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoServico;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false)
    private double valorBase;
}