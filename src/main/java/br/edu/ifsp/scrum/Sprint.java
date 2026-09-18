package br.edu.ifsp.scrum;

import java.util.ArrayList;
import java.util.List;

public class Sprint {
    private int id;
    private String nome;
    private String objetivo;
    private String dataInicio;
    private String dataFim;
    private String status;
    private List<Tarefa> tarefas = new ArrayList<>();

    public Sprint(int id, String nome, String objetivo, String dataInicio, String dataFim) {
        this.id = id;
        this.nome = nome;
        this.objetivo = objetivo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = "Planejada";
    }

    public void adicionarTarefa(Tarefa tarefa) {
        tarefas.add(tarefa);
    }

    public void iniciarSprint() {
        status = "Em andamento";
    }

    public void finalizarSprint() {
        status = "Finalizada";
    }

    public List<Tarefa> getTarefas() { return tarefas; }
    public String getNome() { return nome; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return id + " - " + nome + " | " + objetivo
                + " | " + dataInicio + " até " + dataFim
                + " | " + status + " | Tarefas: " + tarefas.size();
    }
}
