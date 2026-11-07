# Projeto Scrum Java (refatorado)

Sistema de gerenciamento Scrum em Java 17, Maven e SQLite. Menu com 12 opcoes.

## Executar

No terminal, dentro da pasta que contem `pom.xml`:

```powershell
mvn clean compile
mvn exec:java
```

O SQLite cria o arquivo `scrum.db` no diretorio de execucao. **Antes de substituir o projeto, copie o `scrum.db` original para um local seguro.** Depois, coloque uma copia no diretorio do novo projeto, ao lado do `pom.xml`.

## Organizacao

- `model/`: classes do dominio preservadas para a futura interface grafica.
- `dao/`: consultas SQL e persistencia.
- `service/`: operacoes transacionais do planejamento.
- `security/`: hash de senhas PBKDF2-HMAC-SHA256.
- `Main.java`: menu de terminal e leitura de dados.

## Compatibilidade

A estrutura das tabelas e os dados anteriores sao preservados. Ao iniciar, senhas antigas em texto simples sao migradas para hashes; a exclusao continua exigindo a senha original. **Faca backup antes da primeira execucao**, pois a migracao altera os valores da coluna `senha`.

Se o banco antigo tiver dois e-mails que so diferem em maiusculas/minusculas, a criacao do indice unico falhara; corrija os cadastros manualmente apos backup. Nenhum registro e excluido automaticamente.

## Observacoes

- O planejamento associa uma tarefa a uma Sprint e a um item do backlog; pode associar varias tarefas ao mesmo item.
- A mudanca de status da tarefa e persistida. O status do item do backlog nao e automaticamente concluido quando uma tarefa termina.
- Nao inclui ainda login, permissoes, interface JavaFX nem testes automatizados de integracao.
- `scrum.db` e `.git/` nao fazem parte deste pacote por privacidade e seguranca.


## Login e permissoes

No primeiro acesso com banco vazio, o sistema pede o cadastro inicial do Product Owner.
Depois, todos os acessos exigem e-mail e senha. Tres tentativas por sessao.

- Product Owner: cadastra/exclui usuarios, gerencia backlog, cria/planeja Sprints, atualiza tarefas.
- Scrum Master: cria/planeja Sprints e atualiza tarefas.
- Desenvolvedor: consulta os dados e atualiza status de tarefas.
- Todos: visualizam membros, backlog, Sprints, tarefas e resumo.
- Opcao 13: trocar usuario. Opcao 0: encerrar.

A exclusao de usuario continua exigindo a senha da conta excluida.
Por seguranca, o usuario logado nao pode excluir sua propria conta nesta versao.

**Atenção:** nao use senhas reais de outros servicos. O sistema e didatico, sem
recuperacao de senha, bloqueio persistente ou auditoria. Os cargos antigos
sao preservados conforme registrados no SQLite.
