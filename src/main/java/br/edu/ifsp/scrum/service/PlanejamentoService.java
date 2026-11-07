package br.edu.ifsp.scrum.service;
import br.edu.ifsp.scrum.dao.Conexao;
import java.sql.*;
public class PlanejamentoService {
    public int adicionarTarefa(String titulo,String descricao,double horas,int sprintId,
                               int backlogId,Integer usuarioId) throws SQLException {
        if(titulo.isBlank() || horas<=0 || !Double.isFinite(horas))
            throw new IllegalArgumentException("Dados da tarefa invalidos");
        try(Connection c=Conexao.conectar()) {
            c.setAutoCommit(false);
            try {
                int id;
                try(PreparedStatement p=c.prepareStatement("""
                        INSERT INTO tarefas(titulo,descricao,estimativa_horas,sprint_id,backlog_id,usuario_id)
                        VALUES(?,?,?,?,?,?)
                        """,Statement.RETURN_GENERATED_KEYS)) {
                    p.setString(1,titulo);p.setString(2,descricao);p.setDouble(3,horas);
                    p.setInt(4,sprintId);p.setInt(5,backlogId);
                    if(usuarioId==null) p.setNull(6,Types.INTEGER); else p.setInt(6,usuarioId);
                    p.executeUpdate();
                    try(ResultSet r=p.getGeneratedKeys()) {
                        if(!r.next()) throw new SQLException("ID da tarefa nao retornado");
                        id=r.getInt(1);
                    }
                }
                try(PreparedStatement p=c.prepareStatement("UPDATE itens_backlog SET status='EM ANDAMENTO' WHERE id=?")) {
                    p.setInt(1,backlogId);
                    if(p.executeUpdate()!=1) throw new SQLException("Item do backlog nao encontrado");
                }
                try(PreparedStatement p=c.prepareStatement("UPDATE sprints SET status='INICIADA' WHERE id=?")) {
                    p.setInt(1,sprintId);
                    if(p.executeUpdate()!=1) throw new SQLException("Sprint nao encontrada");
                }
                c.commit();return id;
            } catch(SQLException | RuntimeException e) {c.rollback();throw e;}
        }
    }
}
