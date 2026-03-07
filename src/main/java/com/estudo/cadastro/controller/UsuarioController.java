package com.estudo.cadastro.controller;

import com.estudo.cadastro.dto.UsuarioDTO;
import com.estudo.cadastro.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLLER — camada de apresentação/interface HTTP.
 *
 * Responsabilidades do Controller:
 *   - Receber requisições HTTP e extrair dados (path, query params, body)
 *   - Delegar o processamento para o Service
 *   - Montar e retornar a resposta HTTP adequada
 *
 * O Controller NÃO deve ter lógica de negócio — apenas orquestra entrada/saída.
 *
 * @RestController = @Controller + @ResponseBody
 *   Indica que os métodos retornam dados diretamente no body da resposta (JSON)
 *   em vez de nomes de views (como no MVC tradicional).
 *
 * @RequestMapping → define o prefixo de URL para todos os endpoints desta classe.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // ================================================================
    // POST /api/usuarios — Criar novo usuário
    // ================================================================

    /**
     * @PostMapping → mapeia requisições HTTP POST
     * @RequestBody  → deserializa o JSON do corpo da requisição para o DTO
     * @Valid        → dispara as validações do Bean Validation no DTO
     *                 (sem @Valid, as anotações @NotBlank, @Email etc. são ignoradas!)
     *
     * ResponseEntity<T> → permite controlar o status HTTP, headers e body da resposta.
     * HttpStatus.CREATED (201) → código correto para criação bem-sucedida.
     */
    @PostMapping
    public ResponseEntity<UsuarioDTO.Response> criar(@Valid @RequestBody UsuarioDTO.CriarRequest request) {
        UsuarioDTO.Response resposta = usuarioService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    // ================================================================
    // GET /api/usuarios — Listar todos
    // GET /api/usuarios?nome=joao — Filtrar por nome
    // GET /api/usuarios?status=ATIVO — Listar ativos
    // ================================================================

    /**
     * @GetMapping → mapeia requisições HTTP GET
     * @RequestParam(required = false) → parâmetro de query opcional (?nome=...)
     *
     * Exemplo de URLs:
     *   GET /api/usuarios              → lista todos
     *   GET /api/usuarios?nome=Maria   → filtra por nome
     *   GET /api/usuarios?status=ATIVO → lista apenas ativos
     */
    @GetMapping
    public ResponseEntity<List<UsuarioDTO.Response>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String status) {

        List<UsuarioDTO.Response> usuarios;

        if (nome != null && !nome.isBlank()) {
            usuarios = usuarioService.buscarPorNome(nome);
        } else if ("ATIVO".equalsIgnoreCase(status)) {
            usuarios = usuarioService.listarAtivos();
        } else {
            usuarios = usuarioService.listarTodos();
        }

        return ResponseEntity.ok(usuarios); // HTTP 200 OK
    }

    // ================================================================
    // GET /api/usuarios/{id} — Buscar por ID
    // ================================================================

    /**
     * @PathVariable → extrai o valor de {id} da URL
     *
     * Exemplo: GET /api/usuarios/42 → id = 42
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO.Response> buscarPorId(@PathVariable Long id) {
        UsuarioDTO.Response usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    // ================================================================
    // PUT /api/usuarios/{id} — Atualizar usuário
    // ================================================================

    /**
     * PUT → substitui o recurso inteiro (todos os campos obrigatórios devem vir)
     * PATCH → atualiza apenas os campos enviados (parcial)
     *
     * Para simplificar o estudo, usamos PUT aqui.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO.Response> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO.AtualizarRequest request) {

        UsuarioDTO.Response atualizado = usuarioService.atualizar(id, request);
        return ResponseEntity.ok(atualizado);
    }

    // ================================================================
    // PATCH /api/usuarios/{id}/inativar — Soft delete
    // ================================================================

    /**
     * @PatchMapping → para atualizações parciais de recurso.
     * Retorna 204 No Content → ação executada com sucesso, sem body na resposta.
     */
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        usuarioService.inativar(id);
        return ResponseEntity.noContent().build(); // HTTP 204
    }

    // ================================================================
    // DELETE /api/usuarios/{id} — Hard delete
    // ================================================================

    /**
     * @DeleteMapping → mapeia requisições HTTP DELETE
     * Retorna 204 No Content após excluir com sucesso.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build(); // HTTP 204
    }
}
