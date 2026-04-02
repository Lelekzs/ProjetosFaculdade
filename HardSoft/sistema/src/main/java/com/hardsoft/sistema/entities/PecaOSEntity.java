package com.hardsoft.sistema.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tdpecas_os")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PecaOSEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedidoPeca;

    private int quantidade;
    private double valorUnitarioPago;

    @ManyToOne
    @JoinColumn(name = "id_ordem_servico")
    private OrdemServicoEntity ordemServico;

    @ManyToOne
    @JoinColumn(name = "id_peca")
    private PecaEntity peca;
}