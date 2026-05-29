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
 * Cliente do sistema — quem traz o computador pra manutencao.
 * Herda nome, email, senha, telefone, endereco, dataCadastro de UsuarioEntity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_clientes")
@PrimaryKeyJoinColumn(name = "id")
public class ClienteEntity extends UsuarioEntity {

    /**
     * CPF (pessoa fisica, 11 digitos) ou CNPJ (pessoa juridica, 14 digitos).
     * Unico no sistema. Armazenar so digitos, sem mascara.
     */
    @Column(name = "cpf_cnpj", nullable = false, length = 14, unique = true)
    private String cpfCnpj;

    /**
     * RG — opcional, so faz sentido pra pessoa fisica.
     */
    @Column(length = 20)
    private String rg;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SetupEntity> setups = new ArrayList<>();

    @OneToMany(mappedBy = "cliente")
    @JsonIgnore
    private List<OrdemServicoEntity> ordensServico = new ArrayList<>();
}
