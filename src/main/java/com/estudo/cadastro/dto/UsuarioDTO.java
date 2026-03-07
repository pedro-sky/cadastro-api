package com.estudo.cadastro.dto;

import com.estudo.cadastro.model.Usuario;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * DTOs — Data Transfer Objects
 *
 * Por que usar DTOs em vez de expor a entidade diretamente?
 *   1. Segurança: evita expor campos sensíveis (como senha) nas respostas
 *   2. Controle: você define exatamente o que entra e o que sai da API
 *   3. Validação: as regras de validação ficam no DTO, não na entidade
 *   4. Evolução: você pode mudar a entidade sem quebrar o contrato da API
 *
 * Esta classe usa o padrão "classe aninhada" para manter os DTOs agrupados.
 */
public class UsuarioDTO {

    // ================================================================
    // REQUEST DTO — Usado para CRIAR um novo usuário (POST)
    // ================================================================

    /**
     * Anotações de validação do Bean Validation (jakarta.validation):
     *   @NotBlank  → campo obrigatório, não pode ser nulo nem apenas espaços
     *   @NotNull   → campo obrigatório, não pode ser nulo (permite string vazia)
     *   @Email     → valida formato de email
     *   @Size      → valida comprimento mínimo e máximo
     *   @Pattern   → valida com expressão regular
     *
     * A mensagem "message" aparece quando a validação falha.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CriarRequest {

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        private String nome;

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ter um formato válido")
        private String email;

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 100, message = "Senha deve ter pelo menos 6 caracteres")
        private String senha;

        @Pattern(regexp = "^\\(\\d{2}\\)\\s?\\d{4,5}-\\d{4}$",
                 message = "Telefone deve estar no formato (11) 91234-5678")
        private String telefone; // campo opcional — sem @NotBlank
    }

    // ================================================================
    // REQUEST DTO — Usado para ATUALIZAR um usuário existente (PUT)
    // ================================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AtualizarRequest {

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        private String nome;

        @Pattern(regexp = "^\\(\\d{2}\\)\\s?\\d{4,5}-\\d{4}$",
                 message = "Telefone deve estar no formato (11) 91234-5678")
        private String telefone;
    }

    // ================================================================
    // RESPONSE DTO — Retornado nas respostas da API
    // Note que a SENHA não está aqui — nunca retorne senhas!
    // ================================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long id;
        private String nome;
        private String email;
        private String telefone;
        private String status;
        private LocalDateTime criadoEm;
        private LocalDateTime atualizadoEm;

        /**
         * Método de fábrica estático — converte a entidade para o DTO de resposta.
         * Centralizar a conversão aqui evita repetir código nos Services/Controllers.
         */
        public static Response fromEntity(Usuario usuario) {
            return Response.builder()
                    .id(usuario.getId())
                    .nome(usuario.getNome())
                    .email(usuario.getEmail())
                    .telefone(usuario.getTelefone())
                    .status(usuario.getStatus().name())
                    .criadoEm(usuario.getCriadoEm())
                    .atualizadoEm(usuario.getAtualizadoEm())
                    .build();
        }
    }
}
