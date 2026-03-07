package com.estudo.cadastro.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ENTIDADE — representa a tabela "usuarios" no banco de dados.
 *
 * Anotações JPA importantes:
 *   @Entity    → indica que esta classe é mapeada para uma tabela do banco
 *   @Table     → permite customizar o nome da tabela
 *   @Id        → marca o campo como chave primária
 *   @GeneratedValue → define a estratégia de geração do ID
 *   @Column    → customiza detalhes da coluna (nome, tamanho, nullable, unique)
 *
 * Anotações Lombok:
 *   @Getter / @Setter  → gera getters e setters automaticamente
 *   @Builder           → permite criar objetos com o padrão builder
 *   @NoArgsConstructor → gera construtor sem argumentos (exigido pelo JPA)
 *   @AllArgsConstructor→ gera construtor com todos os argumentos (exigido pelo @Builder)
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    /**
     * Chave primária com geração automática de ID.
     * IDENTITY → usa o auto-increment do banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column com nullable=false → NOT NULL no banco
     * length → tamanho máximo do VARCHAR
     */
    @Column(nullable = false, length = 100)
    private String nome;

    /**
     * unique=true → garante que não haverá dois usuários com o mesmo email
     */
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String senha;

    @Column(length = 20)
    private String telefone;

    /**
     * @Enumerated(EnumType.STRING) → salva o enum como texto no banco ("ATIVO", "INATIVO")
     * em vez de número (0, 1) — muito mais legível e seguro
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    /**
     * @Column(updatable = false) → esse campo é definido na criação e nunca mais alterado
     */
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    /**
     * @PrePersist → executado automaticamente pelo JPA ANTES de inserir no banco
     * @PreUpdate  → executado automaticamente pelo JPA ANTES de atualizar no banco
     *
     * Isso elimina a necessidade de setar as datas manualmente no código.
     */
    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
        if (this.status == null) {
            this.status = Status.ATIVO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Enum interno — define os possíveis estados de um usuário.
     * Usar enum é melhor que strings soltas ("ativo", "inativo") pois garante type-safety.
     */
    public enum Status {
        ATIVO, INATIVO
    }
}
