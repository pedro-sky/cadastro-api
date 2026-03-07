package com.estudo.cadastro.config;

import com.estudo.cadastro.model.Usuario;
import com.estudo.cadastro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DATA LOADER — popula o banco de dados com dados iniciais na inicialização.
 *
 * @Configuration → marca a classe como fonte de configurações Spring (define @Beans)
 *
 * CommandLineRunner → interface funcional executada após o contexto Spring estar pronto.
 * Perfeito para inserir dados de exemplo em ambiente de desenvolvimento/estudo.
 *
 * Alternativa: criar um arquivo src/main/resources/data.sql com INSERTs —
 * o Spring Boot executa esse arquivo automaticamente ao iniciar.
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataLoader {

    private final UsuarioRepository usuarioRepository;

    /**
     * @Bean → registra este método como um bean gerenciado pelo Spring.
     * O Spring executa o CommandLineRunner automaticamente após o startup.
     */
    @Bean
    public CommandLineRunner carregarDados() {
        return args -> {
            log.info("Carregando dados iniciais...");

            // Usa o padrão Builder do Lombok para construir objetos de forma fluente
            usuarioRepository.save(Usuario.builder()
                    .nome("Ana Souza")
                    .email("ana.souza@email.com")
                    .senha("senha123")
                    .telefone("(11) 91234-5678")
                    .status(Usuario.Status.ATIVO)
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .nome("Bruno Lima")
                    .email("bruno.lima@email.com")
                    .senha("senha456")
                    .telefone("(21) 98765-4321")
                    .status(Usuario.Status.ATIVO)
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .nome("Carla Mendes")
                    .email("carla.mendes@email.com")
                    .senha("senha789")
                    .status(Usuario.Status.INATIVO)
                    .build());

            log.info("✅ {} usuários inseridos com sucesso!", usuarioRepository.count());
        };
    }
}
