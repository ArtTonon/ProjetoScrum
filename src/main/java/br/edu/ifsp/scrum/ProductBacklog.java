package br.edu.ifsp.scrum;

import java.util.ArrayList;
import java.util.List;

public class ProductBacklog {
    private int id;
    private String titulo;
    private String descricao;
    private int prioridade;
    private String status;
    private List<Tarefa> tarefas = new ArrayList<>();

    public ProductBacklog(int id, String titulo, String descricao, int prioridade) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.status = "Pendente";
    }

    public void adicionarTarefa(Tarefa tarefa) {
        tarefas.add(tarefa);
    }

    public List<Tarefa> getTarefas() { return tarefas; }
    public int getId() { return id; }
    public int getPrioridade() { return prioridade; }
    public String getTitulo() { return titulo; }

    @Override
    public String toString() {
        return id + " - " + titulo + " | Prioridade: " + prioridade
                + " | Status: " + status;
    }
}
