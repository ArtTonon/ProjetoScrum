package br.edu.ifsp.scrum.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Projeto {
    private int id;
    private String nome;
    private String descricao;
    private String dataInicio;
    private String dataFim;

    private List<Usuario> membros = new ArrayList<>();
    private List<ProductBacklog> backlog = new ArrayList<>();
    private List<Sprint> sprints = new ArrayList<>();

    public Projeto(int id, String nome, String descricao, String dataInicio, String dataFim) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public void adicionarMembro(Usuario usuario) {
        membros.add(usuario);
    }

    public void removerMembro(Usuario usuario) {
        membros.remove(usuario);
    }

    public void adicionarItemBacklog(ProductBacklog item) {
        backlog.add(item);
        backlog.sort(Comparator.comparingInt(ProductBacklog::getPrioridade));
    }

    public void adicionarSprint(Sprint sprint) {
        sprints.add(sprint);
    }

    public List<Usuario> getMembros() { return membros; }
    public List<ProductBacklog> getBacklog() { return backlog; }
    public List<Sprint> getSprints() { return sprints; }
    public String getNome() { return nome; }

    public void mostrarResumo() {
        System.out.println("\n=== RESUMO DO PROJETO ===");
        System.out.println("Projeto: " + nome);
        System.out.println("Descrição: " + descricao);
        System.out.println("Período: " + dataInicio + " até " + dataFim);
        System.out.println("Membros: " + membros.size());
        System.out.println("Itens no Product Backlog: " + backlog.size());
        System.out.println("Sprints: " + sprints.size());
    }
}
