# Guia de Requisições para Insomnia - API REST de Eventos

## Configuração Inicial

**Base URL**: `http://localhost:8080`

**Requisitos:**
- API rodando no Docker ou localmente
- Insomnia ou Postman instalado
- Tabelas do banco de dados criadas

---

## ENDPOINTS DE USUÁRIOS

### 1. Criar Usuário

**Método**: `POST`  
**URL**: `http://localhost:8080/api/usuarios`  
**Headers**:
```
Content-Type: application/json
```

**Body (JSON)**:
```json
{
  "nome": "João Silva",
  "email": "joao.silva@email.com"
}
```

**Exemplo 2**:
```json
{
  "nome": "Maria Santos",
  "email": "maria.santos@email.com"
}
```

**Exemplo 3**:
```json
{
  "nome": "Pedro Costa",
  "email": "pedro.costa@email.com"
}
```

---

### 2. Listar Todos os Usuários

**Método**: `GET`  
**URL**: `http://localhost:8080/api/usuarios`

**Response esperado**:
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "email": "joao.silva@email.com"
  },
  {
    "id": 2,
    "nome": "Maria Santos",
    "email": "maria.santos@email.com"
  }
]
```

---

### 3. Buscar Usuário por ID

**Método**: `GET`  
**URL**: `http://localhost:8080/api/usuarios/1`

**Response esperado**:
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao.silva@email.com"
}
```

---

### 4. Deletar Usuário

**Método**: `DELETE`  
**URL**: `http://localhost:8080/api/usuarios/1`

**Response**: Status `204 No Content`

---

## ENDPOINTS DE EVENTOS

### 5. Criar Evento

**Método**: `POST`  
**URL**: `http://localhost:8080/api/eventos`  
**Headers**:
```
Content-Type: application/json
```

**Body (JSON)**:
```json
{
  "nome": "Workshop de Spring Boot",
  "descricao": "Aprenda Spring Boot do zero ao avançado",
  "vagas": 50,
  "dataInicio": "2026-02-15T10:00:00",
  "dataFim": "2026-02-15T18:00:00"
}
```

**Exemplo 2 - Evento com poucas vagas (para testar sistema de reservas)**:
```json
{
  "nome": "Meetup de DevOps",
  "descricao": "Discussão sobre práticas de DevOps modernas",
  "vagas": 2,
  "dataInicio": "2026-03-10T19:00:00",
  "dataFim": "2026-03-10T21:00:00"
}
```

**Exemplo 3**:
```json
{
  "nome": "Palestra sobre Microservices",
  "descricao": "Arquitetura de microservices na prática",
  "vagas": 100,
  "dataInicio": "2026-04-20T14:00:00",
  "dataFim": "2026-04-20T17:00:00"
}
```

**Exemplo 4 - Evento no passado (para testar restrições)**:
```json
{
  "nome": "Evento Passado",
  "descricao": "Este evento já ocorreu",
  "vagas": 30,
  "dataInicio": "2025-01-01T10:00:00",
  "dataFim": "2025-01-01T12:00:00"
}
```

---

### 6. Listar Todos os Eventos

**Método**: `GET`  
**URL**: `http://localhost:8080/api/eventos`

**Response esperado**:
```json
[
  {
    "id": 1,
    "nome": "Workshop de Spring Boot",
    "descricao": "Aprenda Spring Boot do zero ao avançado",
    "vagas": 50,
    "dataInicio": "2026-02-15T10:00:00",
    "dataFim": "2026-02-15T18:00:00"
  }
]
```

---

### 7. Buscar Evento por ID

**Método**: `GET`  
**URL**: `http://localhost:8080/api/eventos/1`

**Response esperado**:
```json
{
  "id": 1,
  "nome": "Workshop de Spring Boot",
  "descricao": "Aprenda Spring Boot do zero ao avançado",
  "vagas": 50,
  "dataInicio": "2026-02-15T10:00:00",
  "dataFim": "2026-02-15T18:00:00"
}
```

---

### 8. Atualizar Evento

**Método**: `PUT`  
**URL**: `http://localhost:8080/api/eventos/1`  
**Headers**:
```
Content-Type: application/json
```

**Body (JSON)**:
```json
{
  "nome": "Workshop de Spring Boot - Edição Especial",
  "descricao": "Aprenda Spring Boot do zero ao avançado - Com certificado",
  "vagas": 60,
  "dataInicio": "2026-02-15T10:00:00",
  "dataFim": "2026-02-15T18:00:00"
}
```

---

### 9. Deletar Evento

**Método**: `DELETE`  
**URL**: `http://localhost:8080/api/eventos/1`

**Response**: Status `204 No Content`

---

## ENDPOINTS DE INSCRIÇÕES

### 10. Criar Inscrição (Inscrever usuário em evento)

**Método**: `POST`  
**URL**: `http://localhost:8080/api/inscricoes`  
**Headers**:
```
Content-Type: application/json
```

**Body (JSON)**:
```json
{
  "usuarioId": 1,
  "eventoId": 1
}
```

**Exemplo 2**:
```json
{
  "usuarioId": 2,
  "eventoId": 1
}
```

**Response esperado**:
```json
{
  "id": 1,
  "usuarioId": 1,
  "usuarioNome": "João Silva",
  "eventoId": 1,
  "eventoNome": "Workshop de Spring Boot",
  "dataInscricao": "2026-01-08T19:30:00",
  "entradaRegistrada": false
}
```

---

### 11. Listar Todas as Inscrições

**Método**: `GET`  
**URL**: `http://localhost:8080/api/inscricoes`

**Response esperado**:
```json
[
  {
    "id": 1,
    "usuarioId": 1,
    "usuarioNome": "João Silva",
    "eventoId": 1,
    "eventoNome": "Workshop de Spring Boot",
    "dataInscricao": "2026-01-08T19:30:00",
    "entradaRegistrada": false
  }
]
```

---

### 12. Listar Inscrições por Usuário

**Método**: `GET`  
**URL**: `http://localhost:8080/api/inscricoes/usuario/1`

**Response esperado**:
```json
[
  {
    "id": 1,
    "usuarioId": 1,
    "usuarioNome": "João Silva",
    "eventoId": 1,
    "eventoNome": "Workshop de Spring Boot",
    "dataInscricao": "2026-01-08T19:30:00",
    "entradaRegistrada": false
  }
]
```

---

### 13. Listar Inscrições por Evento

**Método**: `GET`  
**URL**: `http://localhost:8080/api/inscricoes/evento/1`

**Response esperado**:
```json
[
  {
    "id": 1,
    "usuarioId": 1,
    "usuarioNome": "João Silva",
    "eventoId": 1,
    "eventoNome": "Workshop de Spring Boot",
    "dataInscricao": "2026-01-08T19:30:00",
    "entradaRegistrada": false
  }
]
```

---

### 14. Cancelar Inscrição

**Método**: `DELETE`  
**URL**: `http://localhost:8080/api/inscricoes/1`

**Response**: Status `204 No Content`

**Nota**: Não é possível cancelar após registrar entrada no evento!

---

## ENDPOINTS DE RESERVAS

### 15. Criar Reserva

**Método**: `POST`  
**URL**: `http://localhost:8080/api/reservas`  
**Headers**:
```
Content-Type: application/json
```

**Body (JSON)**:
```json
{
  "usuarioId": 3,
  "eventoId": 2
}
```

**Response esperado** (quando não há vagas):
```json
{
  "id": 1,
  "usuarioId": 3,
  "usuarioNome": "Pedro Costa",
  "eventoId": 2,
  "eventoNome": "Meetup de DevOps",
  "dataReserva": "2026-01-08T20:00:00",
  "posicao": 1,
  "status": "ATIVA"
}
```

**Nota**: A reserva é criada automaticamente quando não há vagas disponíveis. Este endpoint permite criar uma reserva manualmente.

---

### 16. Listar Todas as Reservas

**Método**: `GET`  
**URL**: `http://localhost:8080/api/reservas`

**Response esperado**:
```json
[
  {
    "id": 1,
    "usuarioId": 3,
    "usuarioNome": "Pedro Costa",
    "eventoId": 2,
    "eventoNome": "Meetup de DevOps",
    "dataReserva": "2026-01-08T20:00:00",
    "posicao": 1,
    "status": "ATIVA"
  }
]
```

---

### 17. Listar Reservas por Usuário

**Método**: `GET`  
**URL**: `http://localhost:8080/api/reservas/usuario/3`

**Response esperado**:
```json
[
  {
    "id": 1,
    "usuarioId": 3,
    "usuarioNome": "Pedro Costa",
    "eventoId": 2,
    "eventoNome": "Meetup de DevOps",
    "dataReserva": "2026-01-08T20:00:00",
    "posicao": 1,
    "status": "ATIVA"
  }
]
```

---

### 18. Listar Reservas Ativas por Evento

**Método**: `GET`  
**URL**: `http://localhost:8080/api/reservas/evento/2/ativas`

**Response esperado**:
```json
[
  {
    "id": 1,
    "usuarioId": 3,
    "usuarioNome": "Pedro Costa",
    "eventoId": 2,
    "eventoNome": "Meetup de DevOps",
    "dataReserva": "2026-01-08T20:00:00",
    "posicao": 1,
    "status": "ATIVA"
  },
  {
    "id": 2,
    "usuarioId": 4,
    "usuarioNome": "Ana Paula",
    "eventoId": 2,
    "eventoNome": "Meetup de DevOps",
    "dataReserva": "2026-01-08T20:05:00",
    "posicao": 2,
    "status": "ATIVA"
  }
]
```

**Nota**: As reservas são ordenadas por ordem de chegada (posição na fila).

---

### 19. Cancelar Reserva

**Método**: `DELETE`  
**URL**: `http://localhost:8080/api/reservas/1`

**Response**: Status `204 No Content`

**Nota**: Ao cancelar uma reserva, as posições são recalculadas automaticamente.

---

### 20. Converter Reserva em Inscrição

**Método**: `POST`  
**URL**: `http://localhost:8080/api/reservas/1/converter`

**Response esperado**:
```json
{
  "id": 3,
  "usuarioId": 3,
  "usuarioNome": "Pedro Costa",
  "eventoId": 2,
  "eventoNome": "Meetup de DevOps",
  "dataInscricao": "2026-01-08T20:10:00",
  "entradaRegistrada": false
}
```

**Notas**:
- Este endpoint converte manualmente uma reserva em inscrição
- Só funciona se houver vagas disponíveis no evento
- A conversão automática acontece quando alguém cancela uma inscrição
- Após a conversão, a reserva é removida da lista de espera

---

## CENÁRIOS DE TESTE COMPLETOS

### Cenário 1: Fluxo Básico Completo

Execute na ordem:

1. **Criar 3 usuários** (requisições 1.1, 1.2, 1.3)
2. **Criar 2 eventos** (requisições 5.1, 5.2)
3. **Inscrever usuário 1 no evento 1**
4. **Inscrever usuário 2 no evento 1**
5. **Listar inscrições do evento 1**
6. **Listar inscrições do usuário 1**

---

### Cenário 2: Testar Sistema de Reservas

**Objetivo**: Testar a criação automática de reservas e conversão em inscrição

1. **Criar um evento com 2 vagas** (usar evento "Meetup de DevOps" que já tem 2 vagas)

2. **Criar 3 usuários** (João, Maria e Pedro)

3. **Inscrever usuário 1 no evento**:
   - POST `/api/inscricoes` com `usuarioId: 1, eventoId: 2`
   - Status esperado: `201 Created`
   - Vagas restantes: 1

4. **Inscrever usuário 2 no evento**:
   - POST `/api/inscricoes` com `usuarioId: 2, eventoId: 2`
   - Status esperado: `201 Created`
   - Vagas restantes: 0

5. **Tentar inscrever usuário 3 no evento**:
   - POST `/api/inscricoes` com `usuarioId: 3, eventoId: 2`
   - Status esperado: `201 Created` (mas cria RESERVA, não inscrição)
   - Verificar resposta: deve indicar criação de reserva

6. **Listar reservas do evento**:
   - GET `/api/reservas/evento/2/ativas`
   - Deve mostrar usuário 3 na posição 1

7. **Cancelar inscrição do usuário 1**:
   - DELETE `/api/inscricoes/1`
   - Status esperado: `204 No Content`
   - **Ação automática**: Reserva do usuário 3 deve ser convertida em inscrição

8. **Verificar que a reserva foi convertida**:
   - GET `/api/reservas/evento/2/ativas`
   - Deve retornar lista vazia (reserva foi convertida)
   - GET `/api/inscricoes/evento/2`
   - Deve mostrar usuário 3 inscrito

**Resultado esperado**: Sistema gerencia automaticamente a fila de espera!

---

### Cenário 3: Testar Conversão Manual de Reserva

1. **Criar evento com 1 vaga**

2. **Inscrever usuário 1** (vaga ocupada)

3. **Criar reserva para usuário 2** (lista de espera)

4. **Aumentar vagas do evento**:
   - PUT `/api/eventos/1`
   - Aumentar número de vagas

5. **Converter reserva manualmente**:
   - POST `/api/reservas/1/converter`
   - Reserva deve virar inscrição

6. **Verificar**:
   - Listar inscrições do evento
   - Listar reservas (deve estar vazia)

---

### Cenário 4: Testar Restrições

1. **Tentar inscrever o mesmo usuário duas vezes no mesmo evento**:
   - Deve retornar erro: "Usuário já inscrito neste evento"

2. **Tentar inscrever em evento que já começou**:
   - Criar evento no passado
   - Tentar inscrever
   - Deve retornar erro

3. **Tentar cancelar inscrição após registrar entrada**:
   - Inscrever usuário
   - Registrar entrada (se implementado)
   - Tentar cancelar
   - Deve retornar erro

---

## CÓDIGOS DE STATUS HTTP

- `200 OK` - Sucesso (GET)
- `201 Created` - Criado com sucesso (POST)
- `204 No Content` - Sucesso sem retorno (DELETE)
- `400 Bad Request` - Erro de validação
- `404 Not Found` - Recurso não encontrado
- `409 Conflict` - Conflito (ex: inscrição duplicada)
- `500 Internal Server Error` - Erro no servidor

---


## CHECKLIST DE TESTES

### Usuários
- [ ] Criar usuário
- [ ] Listar todos os usuários
- [ ] Buscar usuário por ID
- [ ] Deletar usuário

### Eventos
- [ ] Criar evento
- [ ] Listar todos os eventos
- [ ] Buscar evento por ID
- [ ] Atualizar evento
- [ ] Deletar evento

### Inscrições
- [ ] Inscrever usuário em evento
- [ ] Listar todas as inscrições
- [ ] Listar inscrições por usuário
- [ ] Listar inscrições por evento
- [ ] Cancelar inscrição
- [ ] Testar inscrição duplicada (deve falhar)

### Reservas (Lista de Espera)
- [ ] Criar reserva manualmente
- [ ] Listar todas as reservas
- [ ] Listar reservas por usuário
- [ ] Listar reservas ativas por evento
- [ ] Cancelar reserva
- [ ] Converter reserva em inscrição manualmente
- [ ] Testar criação automática de reserva (evento cheio)
- [ ] Testar conversão automática (cancelamento de inscrição)
- [ ] Verificar ordenação por posição na fila

---
