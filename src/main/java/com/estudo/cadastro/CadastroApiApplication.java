package com.estudo.cadastro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação Spring Boot.
 *
 * @SpringBootApplication é uma anotação composta que engloba:
 *   - @Configuration     → marca a classe como fonte de beans Spring
 *   - @EnableAutoConfiguration → ativa a configuração automática do Spring Boot
 *   - @ComponentScan     → escaneia os pacotes em busca de @Component, @Service, etc.
 */
@SpringBootApplication
public class CadastroApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CadastroApiApplication.class, args);
    }
}
