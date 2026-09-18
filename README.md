# Projeto Scrum em Java

Projeto acadêmico simples, baseado no relatório científico **"Implementação da Metodologia Scrum no Gerenciamento de Projetos de Desenvolvimento de Software"**.

## O que foi implementado

- Projeto
- Usuários e papéis:
  - Product Owner
  - Scrum Master
  - Desenvolvedor
- Product Backlog
- Sprint
- Tarefas
- Prioridade do backlog
- Planejamento de Sprint
- Alteração de status das tarefas
- Resumo do projeto

## Tecnologias

- Java 17
- Maven
- Aplicação de console
- Sem banco de dados e sem bibliotecas externas

## Como executar

### Pelo terminal

Na pasta do projeto:

```bash
mvn clean compile
mvn exec:java
```

### No NetBeans

1. Abra o NetBeans.
2. Vá em **File > Open Project**.
3. Selecione a pasta `ProjetoScrumJava`.
4. Execute o projeto.

## Observação

O relatório apresenta um modelo conceitual com as classes Projeto, Sprint, Usuario, ProductBacklog e Tarefa. Este projeto transforma esse modelo em uma versão simples e funcional de console. Os dados ficam apenas em memória enquanto o programa está aberto.
