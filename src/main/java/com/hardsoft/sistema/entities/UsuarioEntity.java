package com.hardsoft.sistema.entities;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe base de qualquer usuario do sistema.
 * Admin e Cliente herdam desta classe (estrategia JOINED:
 * gera uma tabela tb_usuarios + uma tabela por subclasse).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    // 60 chars pra caber um hash BCrypt (quando adicionar Spring Security)
    @Column(nullable = false, length = 60)
    private String senha;

    @Column(length = 20)
    private String telefone;

    @Column(length = 200)
    private String endereco;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;

    @PrePersist
    protected void onCreate() {
        if (this.dataCadastro == null) {
            this.dataCadastro = LocalDate.now();
        }
    }
}
