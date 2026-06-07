package com.hardsoft.sistema.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Setup = configuracao de um computador do cliente.
 * 1 cliente pode ter varios setups (ex: PC de casa + notebook).
 * 1 OS sempre se refere a 1 setup especifico (qual maquina foi consertada).
 */
@Entity
@Table(name = "tb_setups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSetup;

    @Column(length = 50)
    private String marca; // ex: Dell, HP, Acer

    @Column(length = 50)
    private String modelo;

    @Column(length = 100)
    private String processador;

    @Column(length = 50)
    private String memoria; // ex: "16GB DDR4"

    @Column(name = "placa_de_video", length = 100)
    private String placaDeVideo;

    @Column(length = 100)
    private String armazenamento; // ex: "SSD 512GB + HD 1TB"

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cliente")
    private ClienteEntity cliente;

    @OneToMany(mappedBy = "setup")
    @JsonIgnore
    private List<OrdemServicoEntity> ordensServico = new ArrayList<>();
}
