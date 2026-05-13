package com.hardsoft.sistema.entities;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "tb_clientes")
@PrimaryKeyJoinColumn(name = "id_usuario") 
public class UsuarioEntity extends ClienteEntity {
//Inverter a herança de UsuarioEntitiy extends ClienteEntity
    private String cpf;
    private String telefone;
    private String endereco;
    private Date dataCadastro;
}