package com.estudo.cadastro;

import com.estudo.cadastro.dto.UsuarioDTO;
import com.estudo.cadastro.exception.Exceptions;
import com.estudo.cadastro.model.Usuario;
import com.estudo.cadastro.repository.UsuarioRepository;
import com.estudo.cadastro.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TESTES UNITÁRIOS do UsuarioService.
 *
 * Conceitos importantes:
 *
 * @ExtendWith(MockitoExtension.class)
 *   → Integra o Mockito com o JUnit 5. Inicializa os mocks automaticamente.
 *
 * @Mock → cria um "dublê" (mock) do Repository.
 *   O mock simula o comportamento da dependência sem tocar o banco de dados.
 *   Você define explicitamente o que o mock deve retornar (given/when).
 *
 * @InjectMocks → cria uma instância REAL do Service e injeta os mocks.
 *
 * Padrão AAA (Arrange, Act, Assert):
 *   - Arrange: configurar o cenário (dados, mocks)
 *   - Act: executar a ação que está sendo testada
 *   - Assert: verificar o resultado
 *
 * AssertJ (assertThat) → biblioteca de asserções fluente e expressiva.
 * Mockito (when/verify) → configurar e verificar interações com mocks.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioMock;
    private UsuarioDTO.CriarRequest criarRequest;

    /**
     * @BeforeEach → executado antes de cada teste.
     * Use para configurar dados comuns que serão reutilizados.
     */
    @BeforeEach
    void setUp() {
        usuarioMock = Usuario.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@email.com")
                .senha("senha123")
                .status(Usuario.Status.ATIVO)
                .build();

        criarRequest = new UsuarioDTO.CriarRequest(
                "João Silva", "joao@email.com", "senha123", "(11) 91234-5678"
        );
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso quando email não existe")
    void deveCriarUsuarioComSucesso() {
        // ARRANGE — configurar o cenário
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        // ACT — executar a ação
        UsuarioDTO.Response resultado = usuarioService.criar(criarRequest);

        // ASSERT — verificar o resultado
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getEmail()).isEqualTo("joao@email.com");

        // Verifica que o repository foi chamado corretamente
        verify(usuarioRepository, times(1)).existsByEmail("joao@email.com");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        // ARRANGE
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        // ACT & ASSERT — verifica que a exceção é lançada
        assertThatThrownBy(() -> usuarioService.criar(criarRequest))
                .isInstanceOf(Exceptions.RecursoJaExisteException.class)
                .hasMessageContaining("joao@email.com");

        // Garante que o save NUNCA foi chamado
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorIdComSucesso() {
        // ARRANGE
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        // ACT
        UsuarioDTO.Response resultado = usuarioService.buscarPorId(1L);

        // ASSERT
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado por ID")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // ARRANGE
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> usuarioService.buscarPorId(99L))
                .isInstanceOf(Exceptions.RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve inativar usuário ativo com sucesso")
    void deveInativarUsuarioAtivoComSucesso() {
        // ARRANGE
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        // ACT — não deve lançar exceção
        assertThatCode(() -> usuarioService.inativar(1L))
                .doesNotThrowAnyException();

        // ASSERT — verifica que o save foi chamado com o usuário inativo
        verify(usuarioRepository).save(argThat(u -> u.getStatus() == Usuario.Status.INATIVO));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar inativar usuário já inativo")
    void deveLancarExcecaoAoInativarUsuarioJaInativo() {
        // ARRANGE
        usuarioMock.setStatus(Usuario.Status.INATIVO);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        // ACT & ASSERT
        assertThatThrownBy(() -> usuarioService.inativar(1L))
                .isInstanceOf(Exceptions.RegraDeNegocioException.class)
                .hasMessageContaining("já está inativo");
    }
}
