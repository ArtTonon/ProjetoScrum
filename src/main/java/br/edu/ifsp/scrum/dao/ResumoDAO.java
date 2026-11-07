package br.edu.ifsp.scrum.dao;
import java.sql.*;
public class ResumoDAO {
    public int contar(String tabela) {
        String sql=switch(tabela) {
            case "usuarios" -> "SELECT COUNT(*) FROM usuarios";
            case "itens_backlog" -> "SELECT COUNT(*) FROM itens_backlog";
            case "sprints" -> "SELECT COUNT(*) FROM sprints";
            case "tarefas" -> "SELECT COUNT(*) FROM tarefas";
            default -> throw new IllegalArgumentException("Tabela invalida");
        };
        try(Connection c=Conexao.conectar(); PreparedStatement p=c.prepareStatement(sql);
            ResultSet r=p.executeQuery()) { return r.next()?r.getInt(1):0; }
        catch(SQLException e) { throw new IllegalStateException("Erro ao gerar resumo",e); }
    }
}
