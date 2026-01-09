# Fluxo do Sistema de Reservas

## Diagrama de Fluxo

```
┌─────────────────────────────────────────────────────────────────┐
│                    TENTATIVA DE INSCRIÇÃO                        │
│                  POST /api/inscricoes                            │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
                    ┌────────────────┐
                    │ Validações     │
                    │ Básicas        │
                    └────────┬───────┘
                             │
                ┌────────────┴────────────┐
                │                         │
                ▼                         ▼
         ┌──────────┐              ┌──────────┐
         │ Usuário  │              │ Evento   │
         │ Existe?  │              │ Existe?  │
         └────┬─────┘              └────┬─────┘
              │ Sim                     │ Sim
              └──────────┬──────────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │ Já está inscrito?    │
              └──────────┬───────────┘
                         │ Não
                         ▼
              ┌──────────────────────┐
              │ Já tem reserva?      │
              └──────────┬───────────┘
                         │ Não
                         ▼
              ┌──────────────────────┐
              │ Evento já começou?   │
              └──────────┬───────────┘
                         │ Não
                         ▼
              ┌──────────────────────┐
              │  HÁ VAGAS?           │
              └──────────┬───────────┘
                         │
           ┌─────────────┴─────────────┐
           │                           │
           ▼ SIM                       ▼ NÃO
    ┌──────────────┐          ┌───────────────┐
    │ CRIAR        │          │ CRIAR         │
    │ INSCRIÇÃO    │          │ RESERVA       │
    └──────┬───────┘          └───────┬───────┘
           │                           │
           ▼                           ▼
    ┌──────────────┐          ┌───────────────┐
    │ Decrementar  │          │ Definir       │
    │ Vagas (-1)   │          │ Expiração     │
    └──────┬───────┘          │ (+15min)      │
           │                  └───────┬───────┘
           │                           │
           ▼                           ▼
    ┌──────────────┐          ┌───────────────┐
    │ Retornar     │          │ Retornar      │
    │ Inscrição    │          │ Reserva       │
    │ (201)        │          │ (201)         │
    └──────────────┘          └───────────────┘
```

## Fluxo de Cancelamento com Conversão Automática

```
┌─────────────────────────────────────────────────────────────────┐
│              CANCELAMENTO DE INSCRIÇÃO                           │
│            DELETE /api/inscricoes/{id}                           │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
                    ┌────────────────┐
                    │ Inscrição      │
                    │ Existe?        │
                    └────────┬───────┘
                             │ Sim
                             ▼
                    ┌────────────────┐
                    │ Evento já      │
                    │ começou?       │
                    └────────┬───────┘
                             │ Não
                             ▼
                    ┌────────────────┐
                    │ Deletar        │
                    │ Inscrição      │
                    └────────┬───────┘
                             │
                             ▼
                    ┌────────────────┐
                    │ Buscar         │
                    │ Reservas       │
                    │ Ativas (FIFO)  │
                    └────────┬───────┘
                             │
                  ┌──────────┴──────────┐
                  │                     │
                  ▼ Existe              ▼ Não existe
         ┌─────────────────┐    ┌──────────────┐
         │ Converter       │    │ Incrementar  │
         │ Primeira        │    │ Vagas (+1)   │
         │ Reserva em      │    └──────┬───────┘
         │ Inscrição       │           │
         └────────┬────────┘           │
                  │                    │
                  ▼                    │
         ┌─────────────────┐           │
         │ Deletar         │           │
         │ Reserva         │           │
         └────────┬────────┘           │
                  │                    │
                  └──────────┬─────────┘
                             │
                             ▼
                    ┌────────────────┐
                    │ Retornar       │
                    │ 204 No Content │
                    └────────────────┘
```

## Fluxo de Limpeza Automática (Scheduler)

```
┌─────────────────────────────────────────────────────────────────┐
│                  SCHEDULER (A cada 5 minutos)                    │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
                    ┌────────────────┐
                    │ Buscar         │
                    │ Reservas com   │
                    │ dataExpiracao  │
                    │ < NOW()        │
                    └────────┬───────┘
                             │
                  ┌──────────┴──────────┐
                  │                     │
                  ▼ Encontrou           ▼ Não encontrou
         ┌─────────────────┐    ┌──────────────┐
         │ Deletar Todas   │    │ Log: Nenhuma │
         │ Reservas        │    │ reserva      │
         │ Expiradas       │    │ expirada     │
         └────────┬────────┘    └──────────────┘
                  │
                  ▼
         ┌─────────────────┐
         │ Log: X reservas │
         │ removidas       │
         └─────────────────┘
```

## Fluxo de Conversão Manual

```
┌─────────────────────────────────────────────────────────────────┐
│           CONVERSÃO MANUAL DE RESERVA                            │
│         POST /api/reservas/{id}/converter                        │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
                    ┌────────────────┐
                    │ Reserva        │
                    │ Existe?        │
                    └────────┬───────┘
                             │ Sim
                             ▼
                    ┌────────────────┐
                    │ Reserva        │
                    │ Expirada?      │
                    └────────┬───────┘
                             │ Não
                             ▼
                    ┌────────────────┐
                    │ Usuário já     │
                    │ inscrito?      │
                    └────────┬───────┘
                             │ Não
                             ▼
                    ┌────────────────┐
                    │ Evento tem     │
                    │ vagas?         │
                    └────────┬───────┘
                             │ Sim
                             ▼
                    ┌────────────────┐
                    │ Criar          │
                    │ Inscrição      │
                    └────────┬───────┘
                             │
                             ▼
                    ┌────────────────┐
                    │ Decrementar    │
                    │ Vagas (-1)     │
                    └────────┬───────┘
                             │
                             ▼
                    ┌────────────────┐
                    │ Deletar        │
                    │ Reserva        │
                    └────────┬───────┘
                             │
                             ▼
                    ┌────────────────┐
                    │ Retornar       │
                    │ Inscrição (200)│
                    └────────────────┘
```

## Estados de uma Reserva

```
┌──────────────┐
│   CRIADA     │ ← Reserva criada quando não há vagas
└──────┬───────┘
       │
       ├──────────────────────────────┐
       │                              │
       ▼                              ▼
┌──────────────┐              ┌─────────────┐
│  CONVERTIDA  │              │  EXPIRADA   │
│ (em inscrição)│              │ (> 15 min)  │
└──────────────┘              └──────┬──────┘
       │                              │
       ▼                              ▼
┌──────────────┐              ┌─────────────┐
│   REMOVIDA   │              │  REMOVIDA   │
│ (com sucesso)│              │(por scheduler)
└──────────────┘              └─────────────┘
```

## Cenário Completo: Evento com 2 Vagas

```
┌─────────────────────────────────────────────────────────────────┐
│ Estado Inicial: Evento com 2 vagas                              │
│ vagasDisponiveis = 2                                            │
└─────────────────────────────────────────────────────────────────┘

▼ Usuário 1 se inscreve

┌─────────────────────────────────────────────────────────────────┐
│ vagasDisponiveis = 1                                            │
│ Inscrições: [Usuário 1]                                         │
└─────────────────────────────────────────────────────────────────┘

▼ Usuário 2 se inscreve

┌─────────────────────────────────────────────────────────────────┐
│ vagasDisponiveis = 0  ← Evento LOTADO                           │
│ Inscrições: [Usuário 1, Usuário 2]                             │
└─────────────────────────────────────────────────────────────────┘

▼ Usuário 3 tenta se inscrever

┌─────────────────────────────────────────────────────────────────┐
│ vagasDisponiveis = 0                                            │
│ Inscrições: [Usuário 1, Usuário 2]                             │
│ Reservas: [Usuário 3 - expira em 15min]  ← LISTA DE ESPERA     │
└─────────────────────────────────────────────────────────────────┘

▼ Usuário 4 tenta se inscrever

┌─────────────────────────────────────────────────────────────────┐
│ vagasDisponiveis = 0                                            │
│ Inscrições: [Usuário 1, Usuário 2]                             │
│ Reservas: [Usuário 3, Usuário 4]  ← FILA FIFO                  │
└─────────────────────────────────────────────────────────────────┘

▼ Usuário 1 cancela inscrição

┌─────────────────────────────────────────────────────────────────┐
│ vagasDisponiveis = 0  ← Mantém 0 pois reserva foi convertida   │
│ Inscrições: [Usuário 2, Usuário 3]  ← Usuário 3 entrou!        │
│ Reservas: [Usuário 4]  ← Usuário 3 saiu da fila                │
└─────────────────────────────────────────────────────────────────┘

▼ Usuário 2 cancela inscrição

┌─────────────────────────────────────────────────────────────────┐
│ vagasDisponiveis = 0  ← Mantém 0 pois reserva foi convertida   │
│ Inscrições: [Usuário 3, Usuário 4]  ← Usuário 4 entrou!        │
│ Reservas: []  ← Fila vazia                                      │
└─────────────────────────────────────────────────────────────────┘

▼ Usuário 3 cancela inscrição

┌─────────────────────────────────────────────────────────────────┐
│ vagasDisponiveis = 1  ← Incrementa pois não há mais reservas   │
│ Inscrições: [Usuário 4]                                         │
│ Reservas: []                                                    │
└─────────────────────────────────────────────────────────────────┘
```

## Validações em Cada Endpoint

### POST /api/reservas (Criar)
```
✓ Usuário existe
✓ Evento existe
✓ Usuário não está inscrito
✓ Usuário não tem reserva
✓ Evento não começou
```

### DELETE /api/reservas/{id} (Cancelar)
```
✓ Reserva existe
```

### POST /api/reservas/{id}/converter (Converter)
```
✓ Reserva existe
✓ Reserva não está expirada
✓ Usuário não está inscrito
✓ Evento tem vagas disponíveis
```

### POST /api/inscricoes (Criar - modo reserva)
```
✓ Usuário existe
✓ Evento existe
✓ Usuário não está inscrito
✓ Usuário não tem reserva
✓ Evento não começou
✗ Não há vagas → Cria RESERVA
```

## Códigos de Retorno

```
200 OK ..................... GET bem-sucedido, Conversão bem-sucedida
201 Created ................ Reserva/Inscrição criada
204 No Content ............. Reserva/Inscrição cancelada
400 Bad Request ............ Evento começou, dados inválidos
404 Not Found .............. Recurso não encontrado
409 Conflict ............... Já inscrito, já tem reserva
500 Internal Server Error .. Erro do servidor
```
