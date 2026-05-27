package com.hardsoft.sistema.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tdordens_servico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrdemServico;

    private LocalDateTime dataEntrada;

    @Column(length = 30)
    private String status; 

    @Column(columnDefinition = "TEXT")
    private String defeito;

    @Column()
    private double valorTotal;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private ClienteEntity cliente;

    @ManyToOne
    @JoinColumn(name = "id_usuario") 
    private UsuarioEntity usuario;

    @ManyToOne
    @JoinColumn(name = "id_setup")
    private SetupEntity setup;

    // Fazer lista de peças e lista de serviços
    @OneToMany(mappedBy = "ordemServico")
    public List<PecaEntity> pecas;

    @OneToMany(mappedBy = "ordemServico")
    public List<ServicoEntity> servicos;

    //Criar uma entidade N:N de serviços e peças com atributos vlrVenda, qntVendida e OS
}