package com.estudo.cadastro.repository;

import com.estudo.cadastro.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY — camada de acesso a dados.
 *
 * Ao estender JpaRepository<T, ID>, ganhamos GRATUITAMENTE:
 *   - save(entity)       → INSERT ou UPDATE
 *   - findById(id)       → SELECT por ID
 *   - findAll()          → SELECT todos
 *   - deleteById(id)     → DELETE por ID
 *   - count()            → COUNT(*)
 *   - existsById(id)     → verifica existência
 *   - e muitos outros...
 *
 * O Spring Data JPA gera a implementação em tempo de execução — você
 * não precisa escrever nenhum SQL para operações básicas!
 *
 * T  = entidade gerenciada (Usuario)
 * ID = tipo da chave primária (Long)
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * QUERY METHOD (Derived Query) — o Spring Data JPA gera o SQL automaticamente
     * a partir do nome do método. Convenções:
     *
     *   findBy{Campo}        → SELECT * WHERE campo = ?
     *   existsBy{Campo}      → SELECT COUNT(*) > 0 WHERE campo = ?
     *   findBy{Campo}And{Campo2} → WHERE campo1 = ? AND campo2 = ?
     *   findBy{Campo}Containing  → WHERE campo LIKE %?%
     *
     * O retorno Optional<T> força o chamador a tratar o caso de "não encontrado".
     */

    // Gera: SELECT * FROM usuarios WHERE email = ?
    Optional<Usuario> findByEmail(String email);

    // Gera: SELECT COUNT(*) > 0 FROM usuarios WHERE email = ?
    boolean existsByEmail(String email);

    // Gera: SELECT * FROM usuarios WHERE status = ?
    List<Usuario> findByStatus(Usuario.Status status);

    // Gera: SELECT * FROM usuarios WHERE nome LIKE %nome%
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    /**
     * JPQL (Java Persistence Query Language) — similar ao SQL, mas usa nomes
     * de CLASSES e ATRIBUTOS Java em vez de tabelas e colunas do banco.
     *
     * @Query → permite escrever JPQL ou SQL nativo para queries mais complexas
     * @Param → vincula o parâmetro do método ao placeholder da query (:nome)
     */
    @Query("SELECT u FROM Usuario u WHERE u.status = :status AND LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Usuario> buscarAtivosPorNome(@Param("status") Usuario.Status status, @Param("nome") String nome);

    /**
     * SQL NATIVO — use quando precisar de funcionalidades específicas do banco
     * que o JPQL não suporta. nativeQuery = true indica que é SQL puro.
     */
    @Query(value = "SELECT COUNT(*) FROM usuarios WHERE status = 'ATIVO'", nativeQuery = true)
    long contarAtivos();
}
