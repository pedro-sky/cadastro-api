package com.estudo.cadastro.exception;

/**
 * EXCEÇÕES CUSTOMIZADAS — permitem identificar erros de negócio com precisão.
 *
 * Boas práticas:
 *   - Extendem RuntimeException (unchecked) → não obrigam try/catch no chamador
 *   - Têm nomes descritivos que indicam o que aconteceu
 *   - São tratadas centralizadamente no GlobalExceptionHandler
 *
 * Sem exceções customizadas, você acabaria fazendo validações manuais em cada
 * método e retornando erros inconsistentes. Com elas, o fluxo fica limpo:
 *   throw new RecursoNaoEncontradoException("Usuário não encontrado");
 */

public class Exceptions {

    /**
     * Lançada quando um recurso requisitado não existe no banco.
     * Mapeada para HTTP 404 Not Found.
     */
    public static class RecursoNaoEncontradoException extends RuntimeException {
        public RecursoNaoEncontradoException(String mensagem) {
            super(mensagem);
        }
    }

    /**
     * Lançada quando há tentativa de criar um recurso que já existe
     * (ex: email duplicado). Mapeada para HTTP 409 Conflict.
     */
    public static class RecursoJaExisteException extends RuntimeException {
        public RecursoJaExisteException(String mensagem) {
            super(mensagem);
        }
    }

    /**
     * Lançada quando uma regra de negócio é violada.
     * Mapeada para HTTP 422 Unprocessable Entity.
     */
    public static class RegraDeNegocioException extends RuntimeException {
        public RegraDeNegocioException(String mensagem) {
            super(mensagem);
        }
    }
}
