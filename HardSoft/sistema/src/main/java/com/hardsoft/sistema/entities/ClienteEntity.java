package com.hardsoft.sistema.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "tb_clientes")
@PrimaryKeyJoinColumn(name = "id_usuario") // O Spring liga o Cliente ao Usuario automaticamente
public class ClienteEntity extends UsuarioEntity {

    private String cpf;
    private String telefone;
    private String endereco;
}