
package br.edu.ifsp.scrum.dao;

import br.edu.ifsp.scrum.model.*;

import java.sql.*;

public class TarefaDAO {

    public int salvar(String titulo, String descricao,
                      double horas, int sprintId,
                      Integer backlogId, Integer usuarioId) {

        if (horas <= 0 || !Double.isFinite(horas)) {
            System.out.println("Estimativa de horas inválida.");
            return -1;
        }

        String sql = """
            INSERT INTO tarefas
            (titulo, descricao, estimativa_horas,
             sprint_id, backlog_id, usuario_id)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                 sql, Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, titulo);
            comando.setString(2, descricao);
            comando.setDouble(3, horas);
            comando.setInt(4, sprintId);

            if (backlogId == null) {
                comando.setNull(5, Types.INTEGER);
            } else {
                comando.setInt(5, backlogId);
            }

            if (usuarioId == null) {
                comando.setNull(6, Types.INTEGER);
            } else {
                comando.setInt(6, usuarioId);
            }

            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao salvar tarefa: " + e.getMessage());
        }

        return -1;
    }

    public void listar() {

        String sql = """
            SELECT t.id, t.titulo, t.status,
                   t.estimativa_horas, s.nome AS sprint
            FROM tarefas t
            JOIN sprints s ON s.id = t.sprint_id
            ORDER BY t.id
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
                    " | Sprint: " + resultado.getString("sprint") +
                    " | Status: " + resultado.getString("status") +
                    " | Horas: " + resultado.getDouble("estimativa_horas")
                );
            }

            if (!encontrou) {
                System.out.println("Nenhuma tarefa cadastrada.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar tarefas: " + e.getMessage());
        }
    }

    public boolean alterarStatus(int id, String status) {
        try(Connection c=Conexao.conectar(); PreparedStatement p=c.prepareStatement(
                "UPDATE tarefas SET status=? WHERE id=?")) {
            p.setString(1,status); p.setInt(2,id);
            return p.executeUpdate()>0;
        } catch(SQLException e) { System.out.println("Erro ao atualizar tarefa: " + e.getMessage()); return false; }
    }

    public void listarPorSprint(int sprintId) {
        String sql="""
            SELECT t.id,t.titulo,t.status,t.estimativa_horas,u.nome AS responsavel
            FROM tarefas t LEFT JOIN usuarios u ON u.id=t.usuario_id
            WHERE t.sprint_id=? ORDER BY t.id
            """;
        try(Connection c=Conexao.conectar(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setInt(1,sprintId);
            try(ResultSet r=p.executeQuery()) {
                boolean encontrou=false;
                while(r.next()) {
                    encontrou=true;
                    String nome=r.getString("responsavel");
                    System.out.println(r.getInt("id")+" - "+r.getString("titulo")+
                        " | Status: "+r.getString("status")+
                        " | Horas: "+r.getDouble("estimativa_horas")+
                        " | Responsavel: "+(nome==null?"Nao definido":nome));
                }
                if(!encontrou) System.out.println("Nenhuma tarefa nesta Sprint.");
            }
        } catch(SQLException e) { System.out.println("Erro ao listar tarefas: "+e.getMessage()); }
    }
}
