# Sistema de Reservas - Documentação Completa

## Visão Geral

O sistema de reservas foi implementado para gerenciar a lista de espera de eventos que atingiram sua capacidade máxima. Quando um usuário tenta se inscrever em um evento sem vagas disponíveis, uma reserva é criada automaticamente.

## Funcionalidades Implementadas

### 1. Criação Automática de Reservas
- Quando não há vagas disponíveis no evento, o sistema cria automaticamente uma reserva
- A reserva expira em 15 minutos por padrão
- Usuário não pode ter múltiplas reservas para o mesmo evento

### 2. Conversão Automática em Inscrição
- Quando uma inscrição é cancelada, a primeira reserva (FIFO) é automaticamente convertida em inscrição
- Sistema valida se a reserva não está expirada antes de converter
- Garante que nenhuma vaga fique ociosa

### 3. Limpeza Automática de Reservas Expiradas
- Scheduler executado a cada 5 minutos
- Remove automaticamente reservas que ultrapassaram o tempo de expiração
- Logs detalhados de todas as operações

### 4. Conversão Manual de Reserva
- Endpoint permite converter manualmente uma reserva em inscrição
- Útil quando o usuário decide garantir sua vaga antes da conversão automática

## Endpoints da API

### Listar Todas as Reservas
```http
GET /api/reservas
```

**Resposta de sucesso:**
```json
[
  {
    "id": 1,
    "usuarioId": 1,
    "usuarioNome": "João Silva",
    "usuarioEmail": "joao.silva@email.com",
    "eventoId": 1,
    "eventoNome": "Workshop Spring Boot",
    "dataReserva": "2026-01-08T10:00:00",
    "dataExpiracao": "2026-01-08T10:15:00",
    "expirada": false
  }
]
```

### Listar Reservas por Usuário
```http
GET /api/reservas/usuario/{usuarioId}
```

**Exemplo:**
```http
GET /api/reservas/usuario/1
```

### Listar Reservas Ativas por Evento
```http
GET /api/reservas/evento/{eventoId}/ativas
```

**Exemplo:**
```http
GET /api/reservas/evento/1/ativas
```

**Resposta:** Retorna apenas reservas não expiradas, ordenadas por data de criação (FIFO).

### Criar Reserva Manualmente
```http
POST /api/reservas
Content-Type: application/json

{
  "usuarioId": 1,
  "eventoId": 1
}
```

**Resposta de sucesso (201 Created):**
```json
{
  "id": 1,
  "usuarioId": 1,
  "usuarioNome": "João Silva",
  "usuarioEmail": "joao.silva@email.com",
  "eventoId": 1,
  "eventoNome": "Workshop Spring Boot",
  "dataReserva": "2026-01-08T10:00:00",
  "dataExpiracao": "2026-01-08T10:15:00",
  "expirada": false
}
```

### Cancelar Reserva
```http
DELETE /api/reservas/{id}
```

**Exemplo:**
```http
DELETE /api/reservas/1
```

**Resposta de sucesso:** `204 No Content`

### Converter Reserva em Inscrição (Manual)
```http
POST /api/reservas/{id}/converter
```

**Exemplo:**
```http
POST /api/reservas/1/converter
```

**Resposta de sucesso (200 OK):**
```json
{
  "id": 5,
  "usuarioId": 1,
  "usuarioNome": "João Silva",
  "usuarioEmail": "joao.silva@email.com",
  "eventoId": 1,
  "eventoNome": "Workshop Spring Boot",
  "dataInscricao": "2026-01-08T10:05:00",
  "status": "CONFIRMADA"
}
```

## Integração com Sistema de Inscrições

### Comportamento ao Criar Inscrição

Quando um usuário tenta criar uma inscrição via `POST /api/inscricoes`:

**Se há vagas disponíveis:**
```json
// Request
{
  "usuarioId": 1,
  "eventoId": 1
}

// Response (201 Created) - InscricaoResponseDTO
{
  "id": 1,
  "usuarioId": 1,
  "usuarioNome": "João Silva",
  "usuarioEmail": "joao.silva@email.com",
  "eventoId": 1,
  "eventoNome": "Workshop Spring Boot",
  "dataInscricao": "2026-01-08T10:00:00",
  "status": "CONFIRMADA"
}
```

**Se NÃO há vagas disponíveis:**
```json
// Request
{
  "usuarioId": 2,
  "eventoId": 1
}

// Response (201 Created) - ReservaResponseDTO
{
  "id": 1,
  "usuarioId": 2,
  "usuarioNome": "Maria Santos",
  "usuarioEmail": "maria.santos@email.com",
  "eventoId": 1,
  "eventoNome": "Workshop Spring Boot",
  "dataReserva": "2026-01-08T10:00:00",
  "dataExpiracao": "2026-01-08T10:15:00",
  "expirada": false
}
```

### Comportamento ao Cancelar Inscrição

Quando uma inscrição é cancelada via `DELETE /api/inscricoes/{id}`:

1. Remove a inscrição do banco de dados
2. Busca a primeira reserva ativa (não expirada) para o evento (FIFO)
3. Se encontrar:
   - Cria automaticamente uma nova inscrição para o usuário da reserva
   - Remove a reserva
   - Mantém o número de vagas disponíveis inalterado
4. Se não encontrar reservas:
   - Incrementa o número de vagas disponíveis em 1

## Regras de Negócio

### Criação de Reserva
1. Usuário deve existir no sistema
2. Evento deve existir no sistema
3. Usuário não pode ter inscrição no evento
4. Usuário não pode ter outra reserva para o mesmo evento
5. Evento não pode ter começado
6. Reserva expira em 15 minutos (configurável via `@PrePersist`)

### Conversão de Reserva em Inscrição
1. Reserva deve existir
2. Reserva não pode estar expirada
3. Usuário não pode já estar inscrito no evento
4. Deve haver vagas disponíveis no evento
5. Reserva é removida após conversão bem-sucedida
6. Vagas disponíveis são decrementadas

### Limpeza de Reservas Expiradas
- Executado automaticamente a cada 5 minutos
- Remove todas as reservas com `dataExpiracao` anterior ao momento atual
- Gera logs informativos sobre a operação

## Modelo de Dados

### Entidade Reserva

```java
@Entity
@Table(name = "reservas",
       uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "evento_id"}))
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;
    
    @Column(name = "data_reserva", nullable = false)
    private LocalDateTime dataReserva;
    
    @Column(name = "data_expiracao", nullable = false)
    private LocalDateTime dataExpiracao;
    
    public boolean isExpirada() {
        return LocalDateTime.now().isAfter(dataExpiracao);
    }
}
```

### Índices do Banco de Dados
- `idx_reserva_usuario`: Índice em `usuario_id` (para consultas por usuário)
- `idx_reserva_evento`: Índice em `evento_id` (para consultas por evento)
- `idx_reserva_expiracao`: Índice em `data_expiracao` (para limpeza de expiradas)
- Constraint única: `(usuario_id, evento_id)` (evita duplicatas)

## Scheduler

### ReservaScheduler

Configuração:
```java
@Component
public class ReservaScheduler {
    
    @Scheduled(cron = "0 */5 * * * *") // A cada 5 minutos
    public void limparReservasExpiradas() {
        int removidas = reservaService.limparReservasExpiradas();
        logger.info("Limpeza concluída: {} reserva(s) removida(s)", removidas);
    }
}
```

Para alterar a frequência, modifique a expressão cron:
- `0 */5 * * * *` = A cada 5 minutos
- `0 */1 * * * *` = A cada 1 minuto
- `0 0 * * * *` = A cada 1 hora

## Exemplos de Uso

### Cenário 1: Evento Lotado - Criar Reserva

```http
# 1. Verificar vagas do evento
GET /api/eventos/1

# Response: vagasDisponiveis = 0

# 2. Tentar criar inscrição (será criada reserva automaticamente)
POST /api/inscricoes
Content-Type: application/json

{
  "usuarioId": 5,
  "eventoId": 1
}

# Response 201 Created (ReservaResponseDTO)
{
  "id": 1,
  "usuarioId": 5,
  "usuarioNome": "Carlos Mendes",
  "usuarioEmail": "carlos@email.com",
  "eventoId": 1,
  "eventoNome": "Workshop Spring Boot",
  "dataReserva": "2026-01-08T10:00:00",
  "dataExpiracao": "2026-01-08T10:15:00",
  "expirada": false
}
```

### Cenário 2: Cancelamento com Conversão Automática

```http
# 1. Listar inscrições do evento
GET /api/inscricoes/evento/1
# Response: 50 inscrições (evento lotado)

# 2. Listar reservas ativas
GET /api/reservas/evento/1/ativas
# Response: 3 reservas na fila

# 3. Cancelar uma inscrição
DELETE /api/inscricoes/10

# Response: 204 No Content
# Sistema automaticamente:
# - Remove inscrição ID 10
# - Converte primeira reserva em inscrição
# - Remove a reserva convertida

# 4. Verificar que reserva foi convertida
GET /api/reservas/evento/1/ativas
# Response: 2 reservas (uma foi convertida)

GET /api/inscricoes/evento/1
# Response: 50 inscrições (uma nova do usuário da reserva)
```

### Cenário 3: Conversão Manual

```http
# 1. Listar minhas reservas
GET /api/reservas/usuario/5

# Response:
[
  {
    "id": 1,
    "usuarioId": 5,
    "eventoId": 1,
    "dataReserva": "2026-01-08T10:00:00",
    "dataExpiracao": "2026-01-08T10:15:00",
    "expirada": false
  }
]

# 2. Verificar se há vagas (alguém pode ter cancelado)
GET /api/eventos/1
# Response: vagasDisponiveis = 2

# 3. Converter minha reserva em inscrição
POST /api/reservas/1/converter

# Response 200 OK (InscricaoResponseDTO)
{
  "id": 51,
  "usuarioId": 5,
  "eventoId": 1,
  "dataInscricao": "2026-01-08T10:05:00",
  "status": "CONFIRMADA"
}
```

## Códigos de Status HTTP

### Reservas

**GET /api/reservas**
- `200 OK` - Lista retornada com sucesso (pode ser vazia)

**POST /api/reservas**
- `201 Created` - Reserva criada com sucesso
- `400 Bad Request` - Dados inválidos
- `404 Not Found` - Usuário ou evento não encontrado
- `409 Conflict` - Usuário já inscrito ou já possui reserva

**DELETE /api/reservas/{id}**
- `204 No Content` - Reserva cancelada com sucesso
- `404 Not Found` - Reserva não encontrada

**POST /api/reservas/{id}/converter**
- `200 OK` - Reserva convertida em inscrição
- `400 Bad Request` - Reserva expirada ou sem vagas
- `404 Not Found` - Reserva não encontrada
- `409 Conflict` - Usuário já inscrito

## Testes

Execute os testes com:

```bash
# Windows
mvnw.cmd test

# Linux/Mac
./mvnw test
```

### Cobertura de Testes

O `ReservaServiceTest` cobre:
- ✅ Listar todas as reservas
- ✅ Listar reservas por usuário
- ✅ Listar reservas ativas por evento
- ✅ Criar reserva com sucesso
- ✅ Validações de criação (usuário inexistente, evento inexistente, etc.)
- ✅ Cancelar reserva
- ✅ Converter reserva em inscrição
- ✅ Validações de conversão (reserva expirada, sem vagas, etc.)
- ✅ Limpeza de reservas expiradas
- ✅ Conversão automática de reservas (FIFO)

## Logs

O sistema gera logs detalhados das operações:

```
2026-01-08 10:00:00 INFO  ReservaScheduler - Iniciando limpeza de reservas expiradas...
2026-01-08 10:00:00 INFO  ReservaScheduler - Limpeza concluída: 3 reserva(s) expirada(s) removida(s)
2026-01-08 10:05:00 INFO  ReservaScheduler - Iniciando limpeza de reservas expiradas...
2026-01-08 10:05:00 DEBUG ReservaScheduler - Nenhuma reserva expirada encontrada
```

## Configurações

### Tempo de Expiração

Para alterar o tempo de expiração padrão (15 minutos), edite o método `onCreate()` na entidade `Reserva`:

```java
@PrePersist
protected void onCreate() {
    dataReserva = LocalDateTime.now();
    if (dataExpiracao == null) {
        dataExpiracao = dataReserva.plusMinutes(30); // Alterado para 30 minutos
    }
}
```

### Frequência de Limpeza

Para alterar a frequência do scheduler, edite a anotação `@Scheduled` em `ReservaScheduler`:

```java
@Scheduled(cron = "0 */1 * * * *") // A cada 1 minuto (mais agressivo)
```

## Considerações de Performance

1. **Índices**: Todos os campos usados em queries possuem índices
2. **Fetch Lazy**: Relacionamentos são carregados sob demanda
3. **Transações**: Todas as operações críticas são transacionais
4. **Batch Delete**: Limpeza de expiradas usa `deleteAll()` para eficiência

## Melhorias Futuras

Possíveis melhorias que podem ser implementadas:

1. **Notificações**: Enviar email/SMS quando reserva for convertida
2. **Priorização**: Sistema de prioridade além de FIFO
3. **Dashboard**: Interface para visualizar fila de reservas
4. **Métricas**: Estatísticas de conversão e tempo médio de espera
5. **Configuração Dinâmica**: Tempo de expiração por tipo de evento

