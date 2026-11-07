
package br.edu.ifsp.scrum.dao;

import br.edu.ifsp.scrum.model.*;

import java.sql.*;

public class ProductBacklogDAO {

    public int salvar(String titulo, String descricao, int prioridade) {

        String sql = """
            INSERT INTO itens_backlog (titulo, descricao, prioridade)
            VALUES (?, ?, ?)
            """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                 sql, Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, titulo);
            comando.setString(2, descricao);
            comando.setInt(3, prioridade);
            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao salvar item: " + e.getMessage());
        }

        return -1;
    }

    public void listar() {

        String sql = """
            SELECT id, titulo, prioridade, status
            FROM itens_backlog
            ORDER BY prioridade ASC, id ASC
            """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            boolean encontrou = false;

            while (resultado.next()) {
                encontrou = true;

                System.out.println(
                    resultado.getInt("id") + " - " +
                    resultado.getString("titulo") +
                    " | Prioridade: " + resultado.getInt("prioridade") +
                    " | Status: " + resultado.getString("status")
                );
            }

            if (!encontrou) {
                System.out.println("Nenhum item cadastrado.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar backlog: " + e.getMessage());
        }
    }

    public boolean existe(int id) {
        try (Connection c=Conexao.conectar(); PreparedStatement p=c.prepareStatement(
                "SELECT 1 FROM itens_backlog WHERE id=?")) {
            p.setInt(1,id);
            try(ResultSet r=p.executeQuery()) { return r.next(); }
        } catch(SQLException e) { System.out.println("Erro ao consultar backlog: " + e.getMessage()); return false; }
    }

    public void atualizarStatus(int id, String status) throws SQLException {
        try(Connection c=Conexao.conectar(); PreparedStatement p=c.prepareStatement(
                "UPDATE itens_backlog SET status=? WHERE id=?")) {
            p.setString(1,status); p.setInt(2,id); p.executeUpdate();
        }
    }
}
