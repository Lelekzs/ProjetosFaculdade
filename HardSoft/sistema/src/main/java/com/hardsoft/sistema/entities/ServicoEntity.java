package com.hardsoft.sistema.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tdservicos_executados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idServico;

    private String descricaoServico; // Detalhes específicos dessa execução
    private double precoMaoDeObra;

    @ManyToOne
    @JoinColumn(name = "id_ordem_servico")
    private OrdemServicoEntity ordemServico;

    @ManyToOne
    @JoinColumn(name = "id_tipo_servico")
    private TipoServicoEntity tipoServico;
}