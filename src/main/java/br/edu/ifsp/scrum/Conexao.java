
package br.edu.ifsp.scrum;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexao {

    private static final String URL = "jdbc:sqlite:scrum.db";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void criarTabelaUsuarios() {

        String sql = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                senha TEXT NOT NULL,
                cargo TEXT NOT NULL
            )
            """;

        try (Connection conexao = conectar();
             Statement comando = conexao.createStatement()) {

            comando.execute(sql);
            System.out.println("Tabela de usuários pronta!");

        } catch (SQLException e) {
            System.out.println(
                "Erro ao criar tabela: " + e.getMessage()
            );
        }
    }
}