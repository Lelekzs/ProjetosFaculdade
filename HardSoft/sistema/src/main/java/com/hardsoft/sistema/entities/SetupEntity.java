package com.hardsoft.sistema.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tdsetups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSetup;

    @Column(length = 50)
    private String device; // Ex: Notebook, Desktop, Servidor

    private String processador;
    private String memoriaRam;
    private String placaMae;
    private String fonte;
    private String disco;
    private String sistemaOperacional;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private ClienteEntity cliente;
}