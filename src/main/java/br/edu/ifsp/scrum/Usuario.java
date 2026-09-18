package br.edu.ifsp.scrum;

public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private String cargo;

    public Usuario(int id, String nome, String email, String senha, String cargo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cargo = cargo;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCargo() { return cargo; }

    @Override
    public String toString() {
        return id + " - " + nome + " | " + cargo + " | " + email;
    }
}
