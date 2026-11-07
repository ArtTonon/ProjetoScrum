package br.edu.ifsp.scrum.dao;

import java.sql.*;

public final class Conexao {
    private static final String URL = "jdbc:sqlite:scrum.db";
    private Conexao() {}

    public static Connection conectar() throws SQLException {
        Connection conexao = DriverManager.getConnection(URL);
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            conexao.close();
            throw e;
        }
        return conexao;
    }

    public static void inicializar() throws SQLException {
        try (Connection c = conectar(); Statement s = c.createStatement()) {
            s.execute("""
                CREATE TABLE IF NOT EXISTS usuarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    senha TEXT NOT NULL,
                    cargo TEXT NOT NULL
                )
                """);
            // Em bancos antigos com emails duplicados ignorando maiusculas, a criacao falha.
            // Nao removemos nenhum registro automaticamente.
            s.execute("CREATE UNIQUE INDEX IF NOT EXISTS idx_email_unico ON usuarios(email COLLATE NOCASE)");
            s.execute("""
                CREATE TABLE IF NOT EXISTS itens_backlog (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    titulo TEXT NOT NULL,
                    descricao TEXT,
                    prioridade INTEGER NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDENTE'
                )
                """);
            s.execute("""
                CREATE TABLE IF NOT EXISTS sprints (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    objetivo TEXT,
                    data_inicio TEXT NOT NULL,
                    data_fim TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PLANEJADA'
                )
                """);
            s.execute("""
                CREATE TABLE IF NOT EXISTS tarefas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    titulo TEXT NOT NULL,
                    descricao TEXT,
                    status TEXT NOT NULL DEFAULT 'A FAZER',
                    estimativa_horas REAL NOT NULL,
                    usuario_id INTEGER,
                    sprint_id INTEGER NOT NULL,
                    backlog_id INTEGER,
                    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
                    FOREIGN KEY (sprint_id) REFERENCES sprints(id),
                    FOREIGN KEY (backlog_id) REFERENCES itens_backlog(id)
                )
                """);
        }
    }
}
