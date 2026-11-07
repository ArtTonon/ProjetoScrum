
package br.edu.ifsp.scrum;

import java.sql.SQLException;
import br.edu.ifsp.scrum.dao.*;
import br.edu.ifsp.scrum.model.*;
import br.edu.ifsp.scrum.service.PlanejamentoService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static Usuario sessao;

    private static final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private static final ProductBacklogDAO backlogDAO = new ProductBacklogDAO();
    private static final SprintDAO sprintDAO = new SprintDAO();
    private static final TarefaDAO tarefaDAO = new TarefaDAO();
    private static final ResumoDAO resumoDAO = new ResumoDAO();
    private static final PlanejamentoService planejamento = new PlanejamentoService();

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {

        try {
            Conexao.inicializar();
            usuarioDAO.migrarSenhasAntigas();
        } catch (SQLException e) {
            System.out.println("Falha ao iniciar banco de dados: " + e.getMessage());
            System.out.println("Confira se ha e-mails duplicados ignorando maiusculas/minusculas.");
            return;
        }

        // O primeiro usuario do banco sera cadastrado como Product Owner.
        int totalUsuarios = usuarioDAO.contarUsuarios();
        if (totalUsuarios < 0) return;
        if (totalUsuarios == 0) {
            System.out.println("Primeiro acesso: cadastre o Product Owner inicial.");
            cadastrarUsuarioInicial();
        }
        if (!entrar()) return;

        int opcao;

        do {
            exibirMenu();
            opcao = lerInt("Escolha: ");

            if (!podeAcessarOpcao(opcao)) {
                System.out.println("Opcao invalida ou nao permitida para seu cargo.");
                continue;
            }

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
                case 10 -> mostrarResumo();
                case 11 -> listarTarefasDaSprint();
                case 12 -> excluirUsuario();
                case 13 -> { sessao = null; if (!entrar()) return; }
                case 14 -> alterarCargoUsuario();
                case 0 -> System.out.println("Programa encerrado.");
                default -> System.out.println("Opcao invalida.");
            }

        } while (opcao != 0);
    }

    // ========================================
    // MENU PRINCIPAL
    // ========================================

    private static void exibirMenu() {
        System.out.println("\n======================================");
        System.out.println(" SISTEMA DE GERENCIAMENTO SCRUM");
        System.out.println("======================================");
        System.out.println("\nConectado: " + sessao.getNome() + " (" + sessao.getCargo() + ")");

        mostrarOpcao(1, "Cadastrar usuario");
        mostrarOpcao(2, "Listar membros");
        mostrarOpcao(3, "Adicionar item ao Product Backlog");
        mostrarOpcao(4, "Listar Product Backlog");
        mostrarOpcao(5, "Criar Sprint");
        mostrarOpcao(6, "Planejar Sprint (selecionar itens)");
        mostrarOpcao(7, "Listar Sprints");
        mostrarOpcao(8, "Alterar status de tarefa");
        mostrarOpcao(9, "Listar tarefas");
        mostrarOpcao(10, "Mostrar resumo do projeto");
        mostrarOpcao(11, "Listar tarefas de uma Sprint");
        mostrarOpcao(12, "Excluir usuario");
        mostrarOpcao(13, "Trocar usuario / sair da sessao");
        mostrarOpcao(14, "Alterar cargo de membro");
        mostrarOpcao(0, "Sair");
    }

    private static void mostrarOpcao(int numero, String descricao) {
        if (podeAcessarOpcao(numero)) {
            System.out.println(numero + " - " + descricao);
        }
    }

    // Regra unica usada tanto para exibir o menu quanto para autorizar a execucao.
    private static boolean podeAcessarOpcao(int opcao) {
        if (sessao == null) return false;
        String cargo = sessao.getCargo();
        boolean productOwner = "Product Owner".equals(cargo);
        boolean scrumMaster = "Scrum Master".equals(cargo);
        boolean desenvolvedor = "Desenvolvedor".equals(cargo);

        if (!productOwner && !scrumMaster && !desenvolvedor) return false;

        return switch (opcao) {
            case 1, 3, 12, 14 -> productOwner;
            case 2, 5, 6 -> productOwner || scrumMaster;
            case 4, 7, 8, 9, 10, 11, 13, 0 -> true;
            default -> false;
        };
    }

    // ========================================
    // USUÁRIOS
    // ========================================

    private static void cadastrarUsuario() {

        System.out.println("\n--- Novo usuário ---");

        String nome = lerTexto("Nome: ");
        String email = lerTexto("E-mail: ");
        String senha = lerTexto("Senha: ");

        System.out.println("\n--- Selecione o cargo ---");
        System.out.println("1 - Product Owner");
        System.out.println("2 - Scrum Master");
        System.out.println("3 - Desenvolvedor");

        String cargo;

        while (true) {

            int opcaoCargo = lerInt("Escolha: ");

            switch (opcaoCargo) {
                case 1 -> cargo = "Product Owner";
                case 2 -> cargo = "Scrum Master";
                case 3 -> cargo = "Desenvolvedor";
                default -> {
                    System.out.println("Cargo inválido.");
                    continue;
                }
            }

            break;
        }

        Usuario usuario = new Usuario(
                0, nome, email, senha, cargo
        );

        boolean salvo = usuarioDAO.salvar(usuario);

        if (salvo) {
            System.out.println("Usuário cadastrado com sucesso!");
        }
    }

    private static void listarMembros() {

        System.out.println("\n--- Membros do projeto ---");

        List<Usuario> usuarios = usuarioDAO.listar();

        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }

        for (Usuario usuario : usuarios) {
            System.out.println(usuario);
        }
    }

    private static void alterarCargoUsuario() {
        System.out.println("\n--- Alterar cargo de membro ---");
        listarMembros();
        int id = lerInt("ID do membro: ");
        if (id == sessao.getId()) {
            System.out.println("Nao e permitido alterar seu proprio cargo durante a sessao.");
            return;
        }
        System.out.println("1 - Product Owner");
        System.out.println("2 - Scrum Master");
        System.out.println("3 - Desenvolvedor");
        int escolha = lerInt("Novo cargo: ");
        String cargo = switch (escolha) {
            case 1 -> "Product Owner";
            case 2 -> "Scrum Master";
            case 3 -> "Desenvolvedor";
            default -> null;
        };
        if (cargo == null) {
            System.out.println("Cargo invalido.");
            return;
        }
        if (usuarioDAO.alterarCargo(id, cargo)) {
            System.out.println("Cargo atualizado com sucesso!");
        } else {
            System.out.println("Nao foi possivel alterar o cargo.");
        }
    }

    private static void excluirUsuario() {

        System.out.println("\n--- Excluir usuário ---");

        listarMembros();

        int id = lerInt("Digite o ID do usuário: ");
        String senha = lerTexto("Digite a senha do usuário: ");

        if (id == sessao.getId()) {
            System.out.println("Nao e permitido excluir a propria conta durante a sessao.");
            return;
        }
        boolean excluido = usuarioDAO.excluir(id, senha);

        if (excluido) {
            System.out.println("Usuário excluído com sucesso!");
        } else {
            System.out.println(
                "Usuário não encontrado ou senha incorreta."
            );
        }
    }

    // ========================================
    // PRODUCT BACKLOG
    // ========================================

    private static void cadastrarBacklog() {

        System.out.println("\n--- Novo item do Product Backlog ---");

        String titulo = lerTexto("Título: ");
        String descricao = lerTexto("Descrição: ");

        int prioridade = lerInt("Prioridade (1 = mais importante): ");

        if (titulo.isBlank() || prioridade < 1) {
            System.out.println("Título ou prioridade inválidos.");
            return;
        }

        int id = backlogDAO.salvar(
                titulo, descricao, prioridade
        );

        if (id > 0) {
            System.out.println(
                "Item cadastrado com sucesso! ID: " + id
            );
        } else {
            System.out.println("Não foi possível cadastrar o item.");
        }
    }

    private static void listarBacklog() {

        System.out.println("\n--- PRODUCT BACKLOG ---");
        backlogDAO.listar();
    }

    // ========================================
    // SPRINTS
    // ========================================

    private static void criarSprint() {

        System.out.println("\n--- Nova Sprint ---");

        String nome = lerTexto("Nome: ");
        String objetivo = lerTexto("Objetivo: ");

        if (nome.isBlank()) {
            System.out.println("O nome da Sprint é obrigatório.");
            return;
        }

        LocalDate inicio = lerData("Data de início (dd/MM/yyyy): ");
        LocalDate fim = lerData("Data de fim (dd/MM/yyyy): ");

        if (fim.isBefore(inicio)) {
            System.out.println(
                "A data final não pode ser anterior à inicial."
            );
            return;
        }

        int id = sprintDAO.salvar(nome, objetivo, inicio, fim);

        if (id > 0) {
            System.out.println(
                "Sprint cadastrada com sucesso! ID: " + id
            );
        } else {
            System.out.println("Não foi possível cadastrar a Sprint.");
        }
    }

    private static void listarSprints() {

        System.out.println("\n--- SPRINTS ---");
        sprintDAO.listar();
    }

    // ========================================
    // PLANEJAMENTO DA SPRINT
    // ========================================

    private static void planejarSprint() {
        System.out.println("\n--- Planejar Sprint ---");
        listarSprints();
        int sprintId=lerInt("Digite o ID da Sprint (0 para cancelar): ");
        if(sprintId==0)return;
        if(!sprintDAO.existe(sprintId)) {System.out.println("Sprint nao encontrada.");return;}
        listarBacklog();
        while(true) {
            int backlogId=lerInt("ID do item do backlog (0 para terminar): ");
            if(backlogId==0)return;
            if(!backlogDAO.existe(backlogId)) {System.out.println("Item nao encontrado.");continue;}
            String titulo=lerTexto("Titulo da tarefa: ");
            String descricao=lerTexto("Descricao: ");
            double horas=lerDouble("Estimativa em horas: ");
            if(titulo.isBlank() || horas<=0 || !Double.isFinite(horas)) {
                System.out.println("Dados invalidos.");continue;
            }
            listarMembros();
            int responsavel=lerInt("ID do responsavel (0 = sem responsavel): ");
            Integer usuarioId=responsavel==0?null:responsavel;
            try {
                int id=planejamento.adicionarTarefa(titulo,descricao,horas,sprintId,backlogId,usuarioId);
                System.out.println("Tarefa salva! ID: "+id);
            } catch(SQLException e) {
                System.out.println("Nao foi possivel salvar tarefa. Verifique os IDs informados: "+e.getMessage());
            } catch(IllegalArgumentException e) {System.out.println(e.getMessage());}
        }
    }

    // ========================================
    // TAREFAS
    // ========================================

    private static void listarTarefas() {

        System.out.println("\n--- TAREFAS ---");
        tarefaDAO.listar();
    }

    private static void listarTarefasDaSprint() {
        System.out.println("\n--- TAREFAS DE UMA SPRINT ---");
        listarSprints();
        int sprintId=lerInt("Digite o ID da Sprint: ");
        if (!sprintDAO.existe(sprintId)) {
            System.out.println("Sprint nao encontrada."); return;
        }
        tarefaDAO.listarPorSprint(sprintId);
    }

    private static void alterarStatusTarefa() {
        System.out.println("\n--- Alterar status da tarefa ---");
        listarTarefas();
        int id=lerInt("ID da tarefa: ");
        System.out.println("1 - A Fazer");
        System.out.println("2 - Em andamento");
        System.out.println("3 - Concluida");
        String status=switch(lerInt("Novo status: ")) {
            case 1 -> "A FAZER";
            case 2 -> "EM ANDAMENTO";
            case 3 -> "CONCLUÍDA";
            default -> null;
        };
        if(status==null) {System.out.println("Status invalido.");return;}
        if(tarefaDAO.alterarStatus(id,status)) System.out.println("Status atualizado!");
        else System.out.println("Tarefa nao encontrada ou falha ao atualizar.");
    }

    // ========================================
    // RESUMO DO PROJETO
    // ========================================

    private static void mostrarResumo() {
        System.out.println("\n--- RESUMO DO PROJETO ---");
        System.out.println("Projeto: Sistema de Gerenciamento Scrum");
        System.out.println("Usuarios: "+resumoDAO.contar("usuarios"));
        System.out.println("Itens no backlog: "+resumoDAO.contar("itens_backlog"));
        System.out.println("Sprints: "+resumoDAO.contar("sprints"));
        System.out.println("Tarefas: "+resumoDAO.contar("tarefas"));
    }

    // ========================================
    // MÉTODOS DE LEITURA
    // ========================================

    private static boolean entrar() {
        for (int tentativa = 1; tentativa <= 3; tentativa++) {
            System.out.println("\n--- LOGIN (" + tentativa + "/3) ---");
            String email = lerTexto("E-mail: ");
            String senha = lerTexto("Senha: ");
            sessao = usuarioDAO.autenticar(email, senha);
            if (sessao != null) {
                System.out.println("Bem-vindo(a), " + sessao.getNome() + "!");
                return true;
            }
            System.out.println("E-mail ou senha incorretos.");
        }
        System.out.println("Limite de tentativas atingido. Programa encerrado.");
        return false;
    }

    private static void cadastrarUsuarioInicial() {
        String nome = lerTexto("Nome: ");
        String email = lerTexto("E-mail: ");
        String senha = lerTexto("Senha: ");
        if (!usuarioDAO.salvar(new Usuario(0, nome, email, senha, "Product Owner"))) {
            System.out.println("Nao foi possivel criar o primeiro usuario.");
        }
    }

    private static String lerTexto(String mensagem) {

        System.out.print(mensagem);
        return sc.nextLine().trim();
    }

    private static int lerInt(String mensagem) {

        while (true) {

            try {
                return Integer.parseInt(lerTexto(mensagem));

            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private static double lerDouble(String mensagem) {

        while (true) {

            try {
                return Double.parseDouble(
                    lerTexto(mensagem).replace(",", ".")
                );

            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }

    private static LocalDate lerData(String mensagem) {

        while (true) {

            try {
                String texto = lerTexto(mensagem);
                return LocalDate.parse(texto, FORMATO_DATA);

            } catch (DateTimeParseException e) {
                System.out.println(
                    "Data inválida. Utilize o formato dd/MM/yyyy."
                );
            }
        }
    }
}
