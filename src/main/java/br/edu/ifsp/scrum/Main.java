package br.edu.ifsp.scrum;

import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final Projeto projeto =
            new Projeto(1, "Sistema de Gerenciamento Scrum",
                    "Sistema acadêmico para organizar projetos, backlog, sprints e tarefas.",
                    "01/02/2026", "30/06/2026");

    private static int proximoUsuario = 1;
    private static int proximoBacklog = 1;
    private static int proximaTarefa = 1;
    private static int proximaSprint = 1;

    public static void main(String[] args) {
        carregarExemplo();

        int opcao;
        do {
            exibirMenu();
            opcao = lerInt("Escolha: ");

            switch (opcao) {
                case 1 -> cadastrarUsuario();
                case 2 -> listarMembros();
                case 3 -> cadastrarBacklog();
                case 4 -> listarBacklog();
                case 5 -> criarSprint();
                case 6 -> planejarSprint();
                case 7 -> listarSprints();
                case 8 -> alterarStatusTarefa();
                case 9 -> listarTarefas();
                case 10 -> projeto.mostrarResumo();
                case 0 -> System.out.println("Programa encerrado.");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private static void exibirMenu() {
        System.out.println("\n======================================");
        System.out.println(" SISTEMA DE GERENCIAMENTO SCRUM");
        System.out.println("======================================");
        System.out.println("1 - Cadastrar usuário");
        System.out.println("2 - Listar membros");
        System.out.println("3 - Adicionar item ao Product Backlog");
        System.out.println("4 - Listar Product Backlog");
        System.out.println("5 - Criar Sprint");
        System.out.println("6 - Planejar Sprint (selecionar itens)");
        System.out.println("7 - Listar Sprints");
        System.out.println("8 - Alterar status de tarefa");
        System.out.println("9 - Listar tarefas");
        System.out.println("10 - Mostrar resumo do projeto");
        System.out.println("0 - Sair");
    }

    private static void carregarExemplo() {
        Usuario po = new Usuario(proximoUsuario++, "Ana", "ana@email.com", "123", "Product Owner");
        Usuario sm = new Usuario(proximoUsuario++, "Carlos", "carlos@email.com", "123", "Scrum Master");
        Usuario dev = new Usuario(proximoUsuario++, "João", "joao@email.com", "123", "Desenvolvedor");

        projeto.adicionarMembro(po);
        projeto.adicionarMembro(sm);
        projeto.adicionarMembro(dev);

        ProductBacklog b1 = new ProductBacklog(proximoBacklog++, "Cadastro de usuários",
                "Permitir cadastrar usuários no sistema.", 1);
        ProductBacklog b2 = new ProductBacklog(proximoBacklog++, "Cadastro de projetos",
                "Permitir criar e visualizar projetos.", 2);
        ProductBacklog b3 = new ProductBacklog(proximoBacklog++, "Gerenciar tarefas",
                "Criar e alterar tarefas das sprints.", 3);

        b1.adicionarTarefa(new Tarefa(proximaTarefa++, "Criar tela de cadastro",
                "Implementar o cadastro básico.", 4));
        b2.adicionarTarefa(new Tarefa(proximaTarefa++, "Criar classe Projeto",
                "Implementar a entidade Projeto.", 3));

        projeto.adicionarItemBacklog(b1);
        projeto.adicionarItemBacklog(b2);
        projeto.adicionarItemBacklog(b3);
    }

    private static void cadastrarUsuario() {
        System.out.println("\n--- Novo usuário ---");
        String nome = lerTexto("Nome: ");
        String email = lerTexto("E-mail: ");
        String senha = lerTexto("Senha: ");
        String cargo = lerTexto("Cargo (Product Owner/Scrum Master/Desenvolvedor): ");

        Usuario usuario = new Usuario(proximoUsuario++, nome, email, senha, cargo);
        projeto.adicionarMembro(usuario);
        System.out.println("Usuário cadastrado!");
    }

    private static void listarMembros() {
        System.out.println("\n--- Membros do projeto ---");
        if (projeto.getMembros().isEmpty()) {
            System.out.println("Nenhum membro.");
            return;
        }
        projeto.getMembros().forEach(System.out::println);
    }

    private static void cadastrarBacklog() {
        System.out.println("\n--- Novo item do Product Backlog ---");
        String titulo = lerTexto("Título: ");
        String descricao = lerTexto("Descrição: ");
        int prioridade = lerInt("Prioridade (1 = mais importante): ");

        ProductBacklog item = new ProductBacklog(
                proximoBacklog++, titulo, descricao, prioridade);

        projeto.adicionarItemBacklog(item);
        System.out.println("Item adicionado ao Product Backlog.");
    }

    private static void listarBacklog() {
        System.out.println("\n--- PRODUCT BACKLOG ---");
        if (projeto.getBacklog().isEmpty()) {
            System.out.println("Backlog vazio.");
            return;
        }
        projeto.getBacklog().forEach(System.out::println);
    }

    private static void criarSprint() {
        System.out.println("\n--- Nova Sprint ---");
        String nome = lerTexto("Nome: ");
        String objetivo = lerTexto("Objetivo: ");
        String inicio = lerTexto("Data de início: ");
        String fim = lerTexto("Data de fim: ");

        Sprint sprint = new Sprint(proximaSprint++, nome, objetivo, inicio, fim);
        projeto.adicionarSprint(sprint);
        System.out.println("Sprint criada.");
    }

    private static void planejarSprint() {
        if (projeto.getSprints().isEmpty()) {
            System.out.println("Crie uma Sprint primeiro.");
            return;
        }

        Sprint sprint = projeto.getSprints().get(projeto.getSprints().size() - 1);
        System.out.println("\nPlanejando a Sprint: " + sprint.getNome());
        listarBacklog();

        int idBacklog = lerInt("Digite o ID do item do backlog para adicionar (0 para terminar): ");

        while (idBacklog != 0) {
            ProductBacklog item = encontrarBacklog(idBacklog);

            if (item == null) {
                System.out.println("Item não encontrado.");
            } else {
                if (item.getTarefas().isEmpty()) {
                    String titulo = lerTexto("Título da tarefa: ");
                    String descricao = lerTexto("Descrição da tarefa: ");
                    double horas = lerDouble("Estimativa em horas: ");

                    Tarefa tarefa = new Tarefa(proximaTarefa++, titulo, descricao, horas);
                    sprint.adicionarTarefa(tarefa);
                    item.adicionarTarefa(tarefa);
                    System.out.println("Tarefa criada e adicionada à Sprint.");
                } else {
                    sprint.adicionarTarefa(item.getTarefas().get(0));
                    System.out.println("Tarefa existente adicionada à Sprint.");
                }
            }

            idBacklog = lerInt("Outro ID (0 para terminar): ");
        }

        sprint.iniciarSprint();
        System.out.println("Sprint iniciada!");
    }

    private static void listarSprints() {
        System.out.println("\n--- SPRINTS ---");
        if (projeto.getSprints().isEmpty()) {
            System.out.println("Nenhuma Sprint.");
            return;
        }

        for (Sprint sprint : projeto.getSprints()) {
            System.out.println(sprint);
            for (Tarefa tarefa : sprint.getTarefas()) {
                System.out.println("   -> " + tarefa);
            }
        }
    }

    private static void alterarStatusTarefa() {
        if (projeto.getSprints().isEmpty()) {
            System.out.println("Nenhuma Sprint criada.");
            return;
        }

        listarTarefas();
        int id = lerInt("ID da tarefa: ");
        Tarefa tarefa = encontrarTarefa(id);

        if (tarefa == null) {
            System.out.println("Tarefa não encontrada.");
            return;
        }

        System.out.println("1 - A Fazer");
        System.out.println("2 - Em andamento");
        System.out.println("3 - Concluída");
        int status = lerInt("Novo status: ");

        switch (status) {
            case 1 -> tarefa.alterarStatus("A Fazer");
            case 2 -> tarefa.alterarStatus("Em andamento");
            case 3 -> tarefa.alterarStatus("Concluída");
            default -> {
                System.out.println("Status inválido.");
                return;
            }
        }

        System.out.println("Status atualizado!");
    }

    private static void listarTarefas() {
        System.out.println("\n--- TAREFAS ---");
        boolean encontrou = false;

        for (Sprint sprint : projeto.getSprints()) {
            for (Tarefa tarefa : sprint.getTarefas()) {
                System.out.println("Sprint: " + sprint.getNome() + " | " + tarefa);
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Nenhuma tarefa em Sprint.");
        }
    }

    private static ProductBacklog encontrarBacklog(int id) {
        for (ProductBacklog item : projeto.getBacklog()) {
            if (item.getId() == id) return item;
        }
        return null;
    }

    private static Tarefa encontrarTarefa(int id) {
        for (Sprint sprint : projeto.getSprints()) {
            for (Tarefa tarefa : sprint.getTarefas()) {
                if (tarefa.getId() == id) return tarefa;
            }
        }
        return null;
    }

    private static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return sc.nextLine();
    }

    private static int lerInt(String mensagem) {
        while (true) {
            try {
                return Integer.parseInt(lerTexto(mensagem));
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }

    private static double lerDouble(String mensagem) {
        while (true) {
            try {
                return Double.parseDouble(lerTexto(mensagem).replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }
}
