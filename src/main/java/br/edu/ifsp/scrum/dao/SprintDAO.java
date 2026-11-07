
package br.edu.ifsp.scrum.dao;

import br.edu.ifsp.scrum.model.*;

import java.sql.*;
import java.time.LocalDate;

public class SprintDAO {

    public int salvar(String nome, String objetivo,
                      LocalDate inicio, LocalDate fim) {

        if (inicio == null || fim == null || fim.isBefore(inicio)) {
            System.out.println("Período da Sprint inválido.");
            return -1;
        }

        String sql = """
            INSERT INTO sprints
            (nome, objetivo, data_inicio, data_fim)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                 sql, Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, nome);
            comando.setString(2, objetivo);
            comando.setString(3, inicio.toString());
            comando.setString(4, fim.toString());

            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao salvar Sprint: " + e.getMessage());
        }

        return -1;
    }

    public void listar() {

        String sql = "SELECT * FROM sprints ORDER BY id";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            boolean encontrou = false;

            while (resultado.next()) {
                encontrou = true;

                System.out.println(
                    resultado.getInt("id") + " - " +
                    resultado.getString("nome") +
                    " | Início: " + resultado.getString("data_inicio") +
                    " | Fim: " + resultado.getString("data_fim") +
                    " | Status: " + resultado.getString("status")
                );
            }

            if (!encontrou) {
                System.out.println("Nenhuma Sprint cadastrada.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar Sprints: " + e.getMessage());
        }
    }

    public boolean existe(int id) {
        try(Connection c=Conexao.conectar(); PreparedStatement p=c.prepareStatement(
                "SELECT 1 FROM sprints WHERE id=?")) {
            p.setInt(1,id);
            try(ResultSet r=p.executeQuery()) { return r.next(); }
        } catch(SQLException e) { System.out.println("Erro ao consultar Sprint: " + e.getMessage()); return false; }
    }

    public void atualizarStatus(int id, String status) throws SQLException {
        try(Connection c=Conexao.conectar(); PreparedStatement p=c.prepareStatement(
                "UPDATE sprints SET status=? WHERE id=?")) {
            p.setString(1,status); p.setInt(2,id); p.executeUpdate();
        }
    }
}
