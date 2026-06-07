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
 * Execucao concreta de um servico dentro de uma OS.
 * Diferente de TipoServico (catalogo): aqui e o servico DE FATO realizado,
 * com observacoes especificas e podendo ter pecas associadas a essa execucao.
 */
@Entity
@Table(name = "tb_servicos_executados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idServico;

    @Column(name = "descricao_servico", length = 500)
    private String descricaoServico; // detalhes especificos desta execucao

    @Column(name = "preco_mao_de_obra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoMaoDeObra;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_ordem_servico")
    @JsonIgnore
    private OrdemServicoEntity ordemServico;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_tipo_servico")
    private TipoServicoEntity tipoServico;

    @OneToMany(mappedBy = "servico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PecaServicoEntity> pecasUtilizadas = new ArrayList<>();
}
