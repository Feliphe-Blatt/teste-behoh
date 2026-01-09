# API REST de Gestão de Eventos

API RESTful desenvolvida com Spring Boot para gerenciar eventos, usuários e inscrições.

## Visão Geral

Sistema completo para gerenciamento de eventos que permite:
- Cadastro e listagem de eventos com controle de vagas
- Gestão de usuários participantes
- Sistema de inscrições com validações de negócio
- Controle automático de vagas disponíveis
- Sistema de reservas quando evento está lotado

## Tecnologias Utilizadas

**Backend:**
- Java 21
- Spring Boot 3.4.1
- Spring Data JPA
- Spring Validation
- PostgreSQL 16
- Flyway (migrations)
- Lombok

**Infraestrutura:**
- Docker & Docker Compose
- Maven 3.9+

**Testes:**
- JUnit 5
- Mockito
- Spring Boot Test

## Pré-requisitos

### Para execução com Docker (Recomendado):
- Docker Desktop instalado e rodando
- Docker Compose

### Para execução local:
- Java JDK 21 ou superior
- Maven 3.9+
- PostgreSQL 16+
- Variáveis de ambiente configuradas

## Como Executar

### Opção 1: Com Docker (Recomendado)

**Passo 1:** Navegue até a pasta do projeto
```bash
cd C:\Users\aceve\OneDrive\Documentos\GitHub\teste-behoh\api
```

**Passo 2:** Inicie os containers
```bash
docker-compose up -d
```

**Passo 3:** Aguarde a inicialização (aproximadamente 40 segundos)

**Passo 4:** Crie as tabelas do banco de dados
```bash
# No Windows (CMD)
corrigir-tabelas.bat

# Ou no PowerShell
.\corrigir-tabelas.bat
```

**Passo 5:** A aplicação estará disponível em `http://localhost:8080`


### Opção 2: Execução Local

**Passo 1:** Configure o banco de dados PostgreSQL
- Crie um banco chamado `eventos_db`
- Ajuste as credenciais em `src/main/resources/application.properties` se necessário

**Passo 2:** Compile e execute
```bash
# Linux/Mac
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

**Passo 3:** A aplicação estará disponível em `http://localhost:8080`

## Endpoints da API

### Usuários

**Criar usuário**
- Método: `POST`
- URL: `/api/usuarios`
- Body:
```json
{
  "nome": "João Silva",
  "email": "joao.silva@email.com"
}
```

**Listar todos os usuários**
- Método: `GET`
- URL: `/api/usuarios`

**Buscar usuário por ID**
- Método: `GET`
- URL: `/api/usuarios/{id}`

**Deletar usuário**
- Método: `DELETE`
- URL: `/api/usuarios/{id}`

---

### Eventos

**Criar evento**
- Método: `POST`
- URL: `/api/eventos`
- Body:
```json
{
  "nome": "Workshop de Spring Boot",
  "descricao": "Aprenda Spring Boot do zero ao avançado",
  "vagas": 50,
  "dataInicio": "2026-02-15T10:00:00",
  "dataFim": "2026-02-15T18:00:00"
}
```

**Listar todos os eventos**
- Método: `GET`
- URL: `/api/eventos`

**Buscar evento por ID**
- Método: `GET`
- URL: `/api/eventos/{id}`

**Atualizar evento**
- Método: `PUT`
- URL: `/api/eventos/{id}`
- Body: (mesmo formato do POST)

**Deletar evento**
- Método: `DELETE`
- URL: `/api/eventos/{id}`

---

### Inscrições

**Criar inscrição**
- Método: `POST`
- URL: `/api/inscricoes`
- Body:
```json
{
  "usuarioId": 1,
  "eventoId": 1
}
```

**Listar todas as inscrições**
- Método: `GET`
- URL: `/api/inscricoes`

**Listar inscrições por usuário**
- Método: `GET`
- URL: `/api/inscricoes/usuario/{usuarioId}`

**Listar inscrições por evento**
- Método: `GET`
- URL: `/api/inscricoes/evento/{eventoId}`

**Cancelar inscrição**
- Método: `DELETE`
- URL: `/api/inscricoes/{id}`

---

### Reservas

**Listar todas as reservas**
- Método: `GET`
- URL: `/api/reservas`

**Listar reservas por usuário**
- Método: `GET`
- URL: `/api/reservas/usuario/{usuarioId}`

**Listar reservas ativas por evento**
- Método: `GET`
- URL: `/api/reservas/evento/{eventoId}/ativas`

**Criar reserva manualmente**
- Método: `POST`
- URL: `/api/reservas`
- Body:
```json
{
  "usuarioId": 1,
  "eventoId": 1
}
```

**Cancelar reserva**
- Método: `DELETE`
- URL: `/api/reservas/{id}`

**Converter reserva em inscrição**
- Método: `POST`
- URL: `/api/reservas/{id}/converter`

> **Nota:** As reservas são criadas automaticamente quando um usuário tenta se inscrever em um evento sem vagas disponíveis. Consulte o arquivo `SISTEMA-RESERVAS.md` para documentação completa do sistema de reservas.

## Executar Testes

**Executar todos os testes:**
```bash
# Linux/Mac
./mvnw test

# Windows
mvnw.cmd test
```

**Executar testes com relatório de cobertura:**
```bash
./mvnw test jacoco:report
```

## Testar a API

Para testar os endpoints, você pode usar:

1. **Insomnia** - Importe o arquivo `Insomnia_Collection.json`
2. **Postman** - Use os exemplos do arquivo `INSOMNIA-GUIDE.md`
3. **cURL** - Execute comandos diretos no terminal
4. **Scripts PowerShell** - Execute `testar-simples.ps1` ou `rodar-e-testar-clean.ps1`

Consulte o arquivo `INSOMNIA-GUIDE.md` para exemplos detalhados de requisições.

## Estrutura do Banco de Dados

O Flyway gerencia as migrations automaticamente. Tabelas criadas:

**usuarios**
- `id` (BIGSERIAL PRIMARY KEY)
- `nome` (VARCHAR(255) NOT NULL)
- `email` (VARCHAR(255) NOT NULL UNIQUE)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

**eventos**
- `id` (BIGSERIAL PRIMARY KEY)
- `nome` (VARCHAR(255) NOT NULL)
- `descricao` (TEXT)
- `vagas` (INTEGER NOT NULL)
- `vagas_disponiveis` (INTEGER NOT NULL)
- `data_inicio` (TIMESTAMP NOT NULL)
- `data_fim` (TIMESTAMP NOT NULL)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

**inscricoes**
- `id` (BIGSERIAL PRIMARY KEY)
- `usuario_id` (BIGINT NOT NULL, FK)
- `evento_id` (BIGINT NOT NULL, FK)
- `data_inscricao` (TIMESTAMP NOT NULL)
- `status` (VARCHAR(50) DEFAULT 'CONFIRMADA')
- UNIQUE constraint em (usuario_id, evento_id)

**reservas**
- `id` (BIGSERIAL PRIMARY KEY)
- `usuario_id` (BIGINT NOT NULL, FK)
- `evento_id` (BIGINT NOT NULL, FK)
- `data_reserva` (TIMESTAMP NOT NULL)
- `data_expiracao` (TIMESTAMP NOT NULL)
- UNIQUE constraint em (usuario_id, evento_id)
- Índices: usuario_id, evento_id, data_expiracao

## Estrutura do Projeto

```
api/
├── src/
│   ├── main/
│   │   ├── java/com/behoh/
│   │   │   ├── controller/      # Controladores REST
│   │   │   │   ├── EventoController.java
│   │   │   │   ├── InscricaoController.java
│   │   │   │   ├── ReservaController.java
│   │   │   │   └── UsuarioController.java
│   │   │   ├── service/         # Lógica de negócio
│   │   │   │   ├── EventoService.java
│   │   │   │   ├── InscricaoService.java
│   │   │   │   ├── ReservaService.java
│   │   │   │   ├── ReservaScheduler.java
│   │   │   │   └── UsuarioService.java
│   │   │   ├── repository/      # Repositórios JPA
│   │   │   │   ├── EventoRepository.java
│   │   │   │   ├── InscricaoRepository.java
│   │   │   │   ├── ReservaRepository.java
│   │   │   │   └── UsuarioRepository.java
│   │   │   ├── model/           # Entidades JPA
│   │   │   │   ├── Evento.java
│   │   │   │   ├── Inscricao.java
│   │   │   │   ├── Reserva.java
│   │   │   │   └── Usuario.java
│   │   │   ├── dto/             # DTOs (Request/Response)
│   │   │   │   ├── EventoRequestDTO.java
│   │   │   │   ├── EventoResponseDTO.java
│   │   │   │   ├── InscricaoResponseDTO.java
│   │   │   │   ├── ReservaRequestDTO.java
│   │   │   │   ├── ReservaResponseDTO.java
│   │   │   │   ├── UsuarioRequestDTO.java
│   │   │   │   └── UsuarioResponseDTO.java
│   │   │   └── exception/       # Tratamento de exceções
│   │   │       ├── BusinessException.java
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       └── ResourceNotFoundException.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/    # Scripts Flyway
│   └── test/                    # Testes unitários
│       └── java/com/behoh/service/
│           ├── EventoServiceTest.java
│           ├── InscricaoServiceTest.java
│           ├── ReservaServiceTest.java
│           └── UsuarioServiceTest.java
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## Regras de Negócio

### Gestão de Eventos
- Eventos devem ter data de início anterior à data de fim
- Número de vagas deve ser positivo
- Vagas disponíveis não podem exceder o total de vagas
- Vagas disponíveis são atualizadas automaticamente com inscrições/cancelamentos

### Inscrições
1. **Criação de inscrição:**
   - Usuário não pode se inscrever duas vezes no mesmo evento
   - Inscrição só é permitida antes do início do evento
   - Verifica disponibilidade de vagas antes de confirmar
   - Se não houver vagas, cria automaticamente uma reserva

2. **Cancelamento de inscrição:**
   - Permitido apenas antes do evento iniciar
   - Ao cancelar, libera uma vaga
   - Se houver reservas, converte automaticamente a primeira em inscrição (FIFO)

3. **Status da inscrição:**
   - CONFIRMADA: Inscrição ativa com vaga garantida
   - Pode ser expandido para incluir outros status (CANCELADA, AGUARDANDO, etc)

### Sistema de Reservas
- Criada automaticamente quando evento está sem vagas
- Usuário é notificado que está na lista de espera
- Primeira reserva (FIFO) é convertida em inscrição quando vaga é liberada
- Reservas expiram em 15 minutos (configurável)
- Scheduler automático limpa reservas expiradas a cada 5 minutos
- Usuário não pode ter múltiplas reservas para o mesmo evento
- Conversão manual de reserva em inscrição disponível via endpoint

### Limpeza Automática
- `ReservaScheduler` executa a cada 5 minutos
- Remove todas as reservas expiradas
- Gera logs informativos de operações realizadas

## Códigos de Status HTTP

A API utiliza os códigos de status HTTP padrão:

- `200 OK` - Requisição bem-sucedida (GET)
- `201 Created` - Recurso criado com sucesso (POST)
- `204 No Content` - Requisição bem-sucedida sem conteúdo (DELETE)
- `400 Bad Request` - Dados inválidos ou malformados
- `404 Not Found` - Recurso não encontrado
- `409 Conflict` - Conflito (ex: inscrição duplicada, email já existe)
- `500 Internal Server Error` - Erro interno do servidor

## Solução de Problemas

### Erro 500 ao criar usuário/evento

Se você receber erro 500, provavelmente as tabelas não foram criadas. Execute:

```bash
# Windows
cd api
corrigir-tabelas.bat
```

## Documentação Adicional

- `SISTEMA-RESERVAS.md` - Documentação completa do sistema de reservas
- `INSOMNIA-GUIDE.md` - Guia completo para testar no Insomnia
- `Insomnia_Collection.json` - Collection pronta para importar
- `exemplos-requisicoes-reservas.http` - Exemplos de requisições HTTP para reservas
