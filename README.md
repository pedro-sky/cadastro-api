# 📚 Cadastro API — Spring Boot para Estudo

API REST de cadastro de usuários construída com Java 17 + Spring Boot 3.

---

## 🏗️ Arquitetura em Camadas

```
Controller → Service → Repository → Banco de Dados
    ↑            ↑          ↑
   HTTP       Negócio    Persistência
```

| Camada | Responsabilidade |
|--------|-----------------|
| **Controller** | Recebe e responde requisições HTTP |
| **Service** | Contém a lógica de negócio |
| **Repository** | Acessa o banco de dados |
| **Model** | Representa as entidades/tabelas |
| **DTO** | Transfere dados entre camadas |
| **Exception** | Tratamento centralizado de erros |

---

## 🚀 Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+

### Rodar a aplicação
```bash
./mvnw spring-boot:run
# ou
mvn spring-boot:run
```

### Rodar os testes
```bash
mvn test
```

A API ficará disponível em: `http://localhost:8080`  
Console H2 (banco em memória): `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:cadastrodb`
- User: `sa` | Senha: (vazio)

---

## 📡 Endpoints

### Criar usuário
```http
POST /api/usuarios
Content-Type: application/json

{
  "nome": "Maria Oliveira",
  "email": "maria@email.com",
  "senha": "minhasenha",
  "telefone": "(11) 91234-5678"
}
```

**Resposta 201 Created:**
```json
{
  "id": 1,
  "nome": "Maria Oliveira",
  "email": "maria@email.com",
  "telefone": "(11) 91234-5678",
  "status": "ATIVO",
  "criadoEm": "2025-01-15T10:30:00",
  "atualizadoEm": "2025-01-15T10:30:00"
}
```

---

### Listar todos os usuários
```http
GET /api/usuarios
```

### Buscar por nome
```http
GET /api/usuarios?nome=maria
```

### Listar apenas ativos
```http
GET /api/usuarios?status=ATIVO
```

### Buscar por ID
```http
GET /api/usuarios/1
```

### Atualizar usuário
```http
PUT /api/usuarios/1
Content-Type: application/json

{
  "nome": "Maria Silva",
  "telefone": "(21) 99999-8888"
}
```

### Inativar usuário (soft delete)
```http
PATCH /api/usuarios/1/inativar
```

### Deletar usuário (hard delete)
```http
DELETE /api/usuarios/1
```

---

## ❌ Tratamento de Erros

Todos os erros seguem o formato padronizado:

```json
{
  "status": 404,
  "mensagem": "Usuário não encontrado com ID: 99",
  "errosCampos": null,
  "timestamp": "2025-01-15T10:30:00"
}
```

Erros de validação:
```json
{
  "status": 400,
  "mensagem": "Erro de validação nos campos",
  "errosCampos": {
    "email": "Email deve ter um formato válido",
    "nome": "Nome deve ter entre 2 e 100 caracteres"
  },
  "timestamp": "2025-01-15T10:30:00"
}
```

| Situação | HTTP Status |
|----------|-------------|
| Criado com sucesso | 201 Created |
| Encontrado | 200 OK |
| Atualizado/Inativado | 200 / 204 No Content |
| Deletado | 204 No Content |
| Validação falhou | 400 Bad Request |
| Não encontrado | 404 Not Found |
| Email duplicado | 409 Conflict |
| Regra de negócio | 422 Unprocessable Entity |
| Erro interno | 500 Internal Server Error |

---

## 🔑 Conceitos-Chave para Revisão

### Injeção de Dependência
```java
// ✅ Via construtor (melhor prática)
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repository; // injetado pelo Spring
}
```

### Query Methods do Spring Data
```java
// O Spring gera o SQL a partir do nome do método!
Optional<Usuario> findByEmail(String email);
List<Usuario> findByNomeContainingIgnoreCase(String nome);
```

### Bean Validation
```java
@NotBlank(message = "Nome é obrigatório")
@Email(message = "Email inválido")
@Size(min = 6, message = "Mínimo 6 caracteres")
private String campo;
```

### Transações
```java
@Transactional              // leitura + escrita
@Transactional(readOnly = true)  // otimizado para leitura
```

---

## 📦 Estrutura do Projeto

```
src/
├── main/java/com/estudo/cadastro/
│   ├── CadastroApiApplication.java  ← Entry point
│   ├── controller/
│   │   └── UsuarioController.java   ← Endpoints REST
│   ├── service/
│   │   └── UsuarioService.java      ← Lógica de negócio
│   ├── repository/
│   │   └── UsuarioRepository.java   ← Acesso a dados
│   ├── model/
│   │   └── Usuario.java             ← Entidade JPA
│   ├── dto/
│   │   └── UsuarioDTO.java          ← Request/Response DTOs
│   ├── exception/
│   │   ├── Exceptions.java          ← Exceções customizadas
│   │   └── GlobalExceptionHandler.java ← Tratamento centralizado
│   └── config/
│       └── DataLoader.java          ← Dados iniciais
└── test/java/com/estudo/cadastro/
    └── UsuarioServiceTest.java      ← Testes unitários
```
