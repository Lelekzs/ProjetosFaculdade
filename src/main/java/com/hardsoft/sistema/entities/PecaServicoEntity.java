package com.hardsoft.sistema.entities;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tabela de juncao: registra quais pecas foram usadas em qual servico
 * (e, indiretamente, em qual OS — atraves do servico).
 *
 * NOTA: substitui a antiga PecaOSEntity (que era duplicada).
 * Como uma peca SEMPRE pertence a um servico de uma OS,
 * ligar direto a OS era redundante.
 */
@Entity
@Table(name = "tb_pecas_servicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PecaServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPecaServico;

    @Column(name = "qnt_vendida", nullable = false)
    private Integer qntVendida;

    /**
     * Valor unitario congelado no momento da venda
     * (preco da peca pode mudar depois, mas o historico fica preservado).
     */
    @Column(name = "vlr_unitario_venda", nullable = false, precision = 10, scale = 2)
    private BigDecimal vlrUnitarioVenda;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_servico")
    @JsonIgnore
    private ServicoEntity servico;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_peca")
    private PecaEntity peca;
}
