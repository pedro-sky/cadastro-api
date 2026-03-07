package com.estudo.cadastro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GLOBAL EXCEPTION HANDLER — centraliza o tratamento de erros da aplicação.
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 *   Intercepta exceções lançadas em qualquer Controller e retorna
 *   uma resposta HTTP padronizada em JSON.
 *
 * Sem isso, cada exceção não tratada retornaria um erro genérico do Spring
 * com stack trace exposto — péssimo para segurança e experiência do cliente.
 *
 * @ExceptionHandler → define qual tipo de exceção o método vai capturar.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata erros de validação do Bean Validation (@Valid).
     * Quando um campo não passa na validação, o Spring lança MethodArgumentNotValidException.
     * Aqui coletamos todos os erros de campo e retornamos de forma estruturada.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidacaoException(MethodArgumentNotValidException ex) {

        Map<String, String> errosCampos = new HashMap<>();

        // Itera sobre todos os erros de campo e coleta campo -> mensagem
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensagem = error.getDefaultMessage();
            errosCampos.put(campo, mensagem);
        });

        ErroResponse resposta = new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação nos campos",
                errosCampos,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    /**
     * Trata RecursoNaoEncontradoException → HTTP 404
     */
    @ExceptionHandler(Exceptions.RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleNaoEncontrado(Exceptions.RecursoNaoEncontradoException ex) {
        ErroResponse resposta = new ErroResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
    }

    /**
     * Trata RecursoJaExisteException → HTTP 409
     */
    @ExceptionHandler(Exceptions.RecursoJaExisteException.class)
    public ResponseEntity<ErroResponse> handleJaExiste(Exceptions.RecursoJaExisteException ex) {
        ErroResponse resposta = new ErroResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(resposta);
    }

    /**
     * Trata RegraDeNegocioException → HTTP 422
     */
    @ExceptionHandler(Exceptions.RegraDeNegocioException.class)
    public ResponseEntity<ErroResponse> handleRegraDeNegocio(Exceptions.RegraDeNegocioException ex) {
        ErroResponse resposta = new ErroResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage(),
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(resposta);
    }

    /**
     * Fallback — captura qualquer exceção não tratada pelas anteriores.
     * Retorna HTTP 500 sem expor detalhes internos ao cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleErroGenerico(Exception ex) {
        ErroResponse resposta = new ErroResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro interno. Tente novamente mais tarde.",
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
    }

    /**
     * DTO de resposta de erro — padroniza como os erros chegam ao cliente.
     * Usar um formato consistente facilita muito o tratamento no frontend.
     *
     * Poderia ser um record Java (Java 16+) em vez de classe:
     *   public record ErroResponse(int status, String mensagem, ...) {}
     */
    public record ErroResponse(
            int status,
            String mensagem,
            Map<String, String> errosCampos,
            LocalDateTime timestamp
    ) {}
}
