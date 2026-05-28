package com.hardsoft.sistema.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Admin = operador/tecnico do sistema. Quem cadastra clientes,
 * cria ordens de servico, registra pecas etc.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_admins")
@PrimaryKeyJoinColumn(name = "id")
public class AdminEntity extends UsuarioEntity {

    @Column(length = 50)
    private String cargo; // ex: "Tecnico", "Gerente", "Atendente"

    @OneToMany(mappedBy = "admin")
    @JsonIgnore
    private List<OrdemServicoEntity> ordensServico = new ArrayList<>();
}
