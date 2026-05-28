package com.hardsoft.sistema.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ordem de Servico — entidade central do sistema.
 * Liga um cliente, um setup (a maquina), um admin responsavel,
 * e contem a lista de servicos executados (que por sua vez podem ter pecas).
 */
@Entity
@Table(name = "tb_ordens_servico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrdemServico;

    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada;

    @Column(name = "data_saida")
    private LocalDateTime dataSaida;

    /**
     * Status livre: "ABERTA", "EM_ANDAMENTO", "AGUARDANDO_PECA",
     * "CONCLUIDA", "ENTREGUE", "CANCELADA".
     * Idealmente seria um enum — deixei String por compatibilidade.
     */
    @Column(nullable = false, length = 30)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String defeito;

    @Column(columnDefinition = "TEXT")
    private String solucao;

    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cliente")
    private ClienteEntity cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_setup")
    private SetupEntity setup;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_admin")
    private AdminEntity admin;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServicoEntity> servicos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.dataEntrada == null) {
            this.dataEntrada = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "ABERTA";
        }
    }
}
