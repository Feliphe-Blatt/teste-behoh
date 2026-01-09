# API REST de Gestão de Eventos - Documentação Atualizada

## Arquivos Revisados e Atualizados

Os seguintes arquivos foram revisados e atualizados, com remoção de emojis e melhor organização:

### 1. README.md (Principal)
**Localização:** `C:\Users\aceve\OneDrive\Documentos\GitHub\teste-behoh\README.md`

**Conteúdo atualizado:**
- Visão geral do projeto
- Tecnologias utilizadas
- Instruções claras de instalação (Docker e local)
- Lista completa de endpoints organizados por recurso
- Estrutura do banco de dados detalhada
- Regras de negócio explicadas
- Estrutura do projeto com árvore de diretórios
- Códigos de status HTTP
- Seção de solução de problemas
- Referências para documentação adicional

**Melhorias aplicadas:**
- Removidos todos os emojis
- Adicionadas instruções para executar o script `corrigir-tabelas.bat`
- Formatação consistente e profissional
- Seções bem organizadas e hierarquizadas
- Exemplos de código com syntax highlighting

### 2. INSOMNIA-GUIDE.md
**Localização:** `C:\Users\aceve\OneDrive\Documentos\GitHub\teste-behoh\INSOMNIA-GUIDE.md`

**Conteúdo atualizado:**
- Guia completo de requisições HTTP
- Exemplos de body JSON para cada endpoint
- Cenários de teste completos
- Códigos de status HTTP explicados
- Dicas para configurar o Insomnia
- Checklist de testes
- Seção de problemas comuns e soluções

**Melhorias aplicadas:**
- Removidos todos os emojis
- Mantida estrutura clara e navegável
- Exemplos práticos prontos para copiar e colar
- Organização profissional

## Estrutura de Documentação

### Documentos Principais
1. **README.md** - Documentação geral do projeto
2. **INSOMNIA-GUIDE.md** - Guia de testes e requisições
3. **Insomnia_Collection.json** - Collection pronta para importar

### Documentos de Suporte
4. **SOLUCAO-ERRO-500.md** - Solução para problema de tabelas
5. **ANALISE-CONTROLLERS.md** - Análise técnica dos controllers
6. **README-SOLUCAO.md** - Guia rápido de solução

### Scripts Auxiliares
7. **corrigir-tabelas.bat** - Script para criar tabelas manualmente
8. **testar-simples.ps1** - Script de teste básico
9. **rodar-e-testar-clean.ps1** - Script completo de teste

## Próximos Passos

### Para Começar a Usar:

1. **Leia o README.md** para entender o projeto
2. **Execute a API** seguindo as instruções no README
3. **Execute o script** `corrigir-tabelas.bat` para criar as tabelas
4. **Use o INSOMNIA-GUIDE.md** para testar os endpoints
5. **Importe a collection** `Insomnia_Collection.json` no Insomnia

### Para Desenvolvimento:

1. Clone o repositório
2. Configure o ambiente (Docker recomendado)
3. Execute os testes com `mvnw test`
4. Consulte a estrutura do projeto no README
5. Siga as regras de negócio documentadas

## Padrões Adotados

### Documentação:
- Sem emojis (profissional)
- Markdown com formatação consistente
- Código com syntax highlighting
- Seções bem definidas e hierarquizadas
- Exemplos práticos e funcionais

### Código:
- RESTful best practices
- DTOs para Request/Response
- Tratamento global de exceções
- Códigos HTTP apropriados
- Validações de negócio na camada de serviço

### Estrutura:
- Separação clara de responsabilidades (Controller → Service → Repository)
- Uso de records para DTOs
- Lombok para redução de boilerplate
- Migrations com Flyway

## Informações Técnicas

### Base URL
```
http://localhost:8080
```

### Endpoints Principais

**Usuários:**
- GET /api/usuarios
- POST /api/usuarios
- GET /api/usuarios/{id}
- DELETE /api/usuarios/{id}

**Eventos:**
- GET /api/eventos
- POST /api/eventos
- GET /api/eventos/{id}
- PUT /api/eventos/{id}
- DELETE /api/eventos/{id}

**Inscrições:**
- GET /api/inscricoes
- POST /api/inscricoes
- GET /api/inscricoes/usuario/{usuarioId}
- GET /api/inscricoes/evento/{eventoId}
- DELETE /api/inscricoes/{id}

### Tecnologias

**Backend:**
- Java 21
- Spring Boot 3.4.1
- Spring Data JPA
- PostgreSQL 16
- Flyway

**Ferramentas:**
- Docker & Docker Compose
- Maven
- Insomnia/Postman

## Comandos Úteis

### Docker
```bash
# Iniciar
docker-compose up -d

# Ver logs
docker-compose logs -f app

# Parar
docker-compose down

# Limpar e reiniciar
docker-compose down -v
docker-compose up -d --build
```

### Maven
```bash
# Compilar
./mvnw clean install

# Executar
./mvnw spring-boot:run

# Testes
./mvnw test
```

### Banco de Dados
```bash
# Criar tabelas manualmente
corrigir-tabelas.bat

# Conectar ao PostgreSQL
docker exec -it eventos-postgres psql -U postgres -d eventos_db
```
