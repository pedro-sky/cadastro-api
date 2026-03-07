package com.estudo.cadastro.service;

import com.estudo.cadastro.dto.UsuarioDTO;
import com.estudo.cadastro.exception.Exceptions;
import com.estudo.cadastro.model.Usuario;
import com.estudo.cadastro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SERVICE — camada de lógica de negócio.
 *
 * Responsabilidades do Service:
 *   - Orquestrar operações entre Repository, outros Services, etc.
 *   - Aplicar regras de negócio (validações, transformações)
 *   - Gerenciar transações com @Transactional
 *   - Converter entre entidades e DTOs
 *
 * O Service NÃO deve saber nada sobre HTTP (sem Request/Response/HttpStatus).
 * O Controller NÃO deve conter lógica de negócio.
 * Essa separação facilita testes e manutenção.
 *
 * @Service   → marca como componente de serviço (registrado no Spring Context)
 * @Slf4j     → injeta o logger "log" automaticamente via Lombok
 * @RequiredArgsConstructor → gera construtor com todos os campos final (injeção de dependência)
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioService {

    /**
     * Injeção de dependência via construtor (melhor prática).
     * O @RequiredArgsConstructor do Lombok gera o construtor automaticamente.
     *
     * Por que via construtor em vez de @Autowired no campo?
     *   - Imutabilidade: campo final não pode ser reatribuído
     *   - Testabilidade: facilita mockar dependências nos testes
     *   - Expliciteza: deixa claro quais dependências a classe precisa
     */
    private final UsuarioRepository usuarioRepository;

    // ================================================================
    // CREATE
    // ================================================================

    /**
     * @Transactional → garante que toda a operação ocorre em uma única transação.
     * Se qualquer passo falhar, o Spring faz rollback automaticamente.
     *
     * Regra de negócio: email deve ser único.
     */
    @Transactional
    public UsuarioDTO.Response criar(UsuarioDTO.CriarRequest request) {
        log.info("Criando usuário com email: {}", request.getEmail());

        // Valida unicidade do email antes de tentar persistir
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new Exceptions.RecursoJaExisteException(
                    "Já existe um usuário com o email: " + request.getEmail()
            );
        }

        // ATENÇÃO: em produção real, a senha DEVE ser criptografada!
        // Exemplo com BCrypt: passwordEncoder.encode(request.getSenha())
        // Para simplificar o estudo, estamos salvando em texto puro aqui.
        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(request.getSenha()) // ⚠️ em produção: use BCryptPasswordEncoder
                .telefone(request.getTelefone())
                .status(Usuario.Status.ATIVO)
                .build();

        Usuario salvo = usuarioRepository.save(usuario);
        log.info("Usuário criado com ID: {}", salvo.getId());

        return UsuarioDTO.Response.fromEntity(salvo);
    }

    // ================================================================
    // READ
    // ================================================================

    /**
     * @Transactional(readOnly = true) → otimização para operações de leitura.
     * O Hibernate desabilita o "dirty checking" (verificação de mudanças),
     * tornando a operação mais performática.
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO.Response> listarTodos() {
        log.info("Listando todos os usuários");
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioDTO.Response::fromEntity) // method reference em vez de lambda
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO.Response buscarPorId(Long id) {
        log.info("Buscando usuário por ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new Exceptions.RecursoNaoEncontradoException(
                        "Usuário não encontrado com ID: " + id
                ));
        return UsuarioDTO.Response.fromEntity(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO.Response> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(UsuarioDTO.Response::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO.Response> listarAtivos() {
        return usuarioRepository.findByStatus(Usuario.Status.ATIVO)
                .stream()
                .map(UsuarioDTO.Response::fromEntity)
                .toList();
    }

    // ================================================================
    // UPDATE
    // ================================================================

    @Transactional
    public UsuarioDTO.Response atualizar(Long id, UsuarioDTO.AtualizarRequest request) {
        log.info("Atualizando usuário ID: {}", id);

        // Busca o usuário — lança exceção se não encontrado
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new Exceptions.RecursoNaoEncontradoException(
                        "Usuário não encontrado com ID: " + id
                ));

        // Atualiza apenas os campos permitidos (email não pode ser alterado aqui)
        usuario.setNome(request.getNome());
        usuario.setTelefone(request.getTelefone());

        // O save() do JPA: se a entidade tem ID → UPDATE; se não tem → INSERT
        Usuario atualizado = usuarioRepository.save(usuario);

        return UsuarioDTO.Response.fromEntity(atualizado);
    }

    // ================================================================
    // DELETE / INATIVAR
    // ================================================================

    /**
     * Soft Delete — em vez de excluir o registro do banco, apenas muda o status.
     *
     * Por que preferir soft delete?
     *   - Mantém histórico de dados
     *   - Permite "desfazer" a exclusão
     *   - Evita problemas de integridade referencial
     *   - Auditoria e compliance
     */
    @Transactional
    public void inativar(Long id) {
        log.info("Inativando usuário ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new Exceptions.RecursoNaoEncontradoException(
                        "Usuário não encontrado com ID: " + id
                ));

        if (usuario.getStatus() == Usuario.Status.INATIVO) {
            throw new Exceptions.RegraDeNegocioException("Usuário já está inativo");
        }

        usuario.setStatus(Usuario.Status.INATIVO);
        usuarioRepository.save(usuario);
    }

    /**
     * Hard Delete — remove permanentemente do banco.
     * Use com cautela em sistemas reais.
     */
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando usuário ID: {}", id);

        if (!usuarioRepository.existsById(id)) {
            throw new Exceptions.RecursoNaoEncontradoException(
                    "Usuário não encontrado com ID: " + id
            );
        }

        usuarioRepository.deleteById(id);
    }
}
