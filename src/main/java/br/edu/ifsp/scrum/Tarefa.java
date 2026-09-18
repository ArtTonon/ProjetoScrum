package br.edu.ifsp.scrum;

public class Tarefa {

    private int id;
    private String titulo;
    private String descricao;
    private String status;
    private double estimativaHoras;
    private Usuario responsavel;

    public Tarefa(int id, String titulo, String descricao, double estimativaHoras) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.estimativaHoras = estimativaHoras;
        this.status = "A FAZER";
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getStatus() {
        return status;
    }

    public double getEstimativaHoras() {
        return estimativaHoras;
    }

    public Usuario getResponsavel() {
        return responsavel;
    }

    public void definirResponsavel(Usuario responsavel) {
        this.responsavel = responsavel;
    }

    public boolean alterarStatus(String novoStatus) {

        if (novoStatus == null) {
            return false;
        }

        novoStatus = novoStatus.toUpperCase();

        if (!novoStatus.equals("A FAZER")
                && !novoStatus.equals("EM ANDAMENTO")
                && !novoStatus.equals("CONCLUÍDA")) {

            return false;
        }

        this.status = novoStatus;
        return true;
    }

    @Override
    public String toString() {

        String nomeResponsavel;

        if (responsavel == null) {
            nomeResponsavel = "Não definido";
        } else {
            nomeResponsavel = responsavel.getNome();
        }

        return "Tarefa #" + id
                + " | " + titulo
                + " | Status: " + status
                + " | Estimativa: " + estimativaHoras + "h"
                + " | Responsável: " + nomeResponsavel;
    }
}