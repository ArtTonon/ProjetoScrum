package br.edu.ifsp.scrum.dao;

import br.edu.ifsp.scrum.model.Usuario;
import br.edu.ifsp.scrum.security.Senhas;
import java.sql.*;
import java.util.*;

public class UsuarioDAO {
    public Usuario autenticar(String email, String senha) {
        if (email == null || senha == null) return null;
        String sql = "SELECT id,nome,email,senha,cargo FROM usuarios WHERE email = ? COLLATE NOCASE";
        try (Connection c = Conexao.conectar(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, email.trim());
            try (ResultSet r = p.executeQuery()) {
                if (r.next() && Senhas.verificar(senha, r.getString("senha"))) {
                    return new Usuario(r.getInt("id"), r.getString("nome"),
                        r.getString("email"), "", r.getString("cargo"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar login: " + e.getMessage());
        }
        return null;
    }

    public int contarUsuarios() {
        try (Connection c = Conexao.conectar(); PreparedStatement p = c.prepareStatement(
                "SELECT COUNT(*) FROM usuarios"); ResultSet r = p.executeQuery()) {
            return r.next() ? r.getInt(1) : -1;
        } catch (SQLException e) {
            System.out.println("Erro ao verificar usuarios: " + e.getMessage());
            return -1;
        }
    }

    public boolean salvar(Usuario u) {
        if (u.getNome().isBlank() || u.getEmail().isBlank() || u.getSenha().isBlank()) {
            System.out.println("Nome, e-mail e senha sao obrigatorios.");
            return false;
        }
        String sql = "INSERT INTO usuarios (nome,email,senha,cargo) VALUES (?,?,?,?)";
        try (Connection c = Conexao.conectar(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, u.getNome());
            p.setString(2, u.getEmail().trim().toLowerCase(Locale.ROOT));
            p.setString(3, Senhas.gerarHash(u.getSenha()));
            p.setString(4, u.getCargo());
            p.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE"))
                System.out.println("Este e-mail ja esta cadastrado. Utilize outro e-mail.");
            else System.out.println("Nao foi possivel cadastrar o usuario: " + e.getMessage());
            return false;
        }
    }

    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = Conexao.conectar(); PreparedStatement p = c.prepareStatement(
                "SELECT id,nome,email,cargo FROM usuarios ORDER BY id");
             ResultSet r = p.executeQuery()) {
            while (r.next()) lista.add(new Usuario(r.getInt("id"),r.getString("nome"),
                r.getString("email"),"",r.getString("cargo")));
        } catch (SQLException e) { System.out.println("Erro ao listar usuarios: " + e.getMessage()); }
        return lista;
    }

    public boolean excluir(int id, String senha) {
        try (Connection c = Conexao.conectar()) {
            c.setAutoCommit(false);
            try {
                String hash;
                try (PreparedStatement p = c.prepareStatement("SELECT senha FROM usuarios WHERE id=?")) {
                    p.setInt(1,id);
                    try (ResultSet r=p.executeQuery()) {
                        if (!r.next()) { c.rollback(); return false; }
                        hash=r.getString(1);
                    }
                }
                if (!Senhas.verificar(senha,hash)) { c.rollback(); return false; }
                try (PreparedStatement p=c.prepareStatement("DELETE FROM usuarios WHERE id=?")) {
                    p.setInt(1,id);
                    boolean removido=p.executeUpdate()>0;
                    c.commit();
                    return removido;
                }
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Nao foi possivel excluir o usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean alterarCargo(int id, String novoCargo) {
        if (!Set.of("Product Owner", "Scrum Master", "Desenvolvedor").contains(novoCargo)) {
            return false;
        }
        try (Connection c = Conexao.conectar()) {
            c.setAutoCommit(false);
            try {
                String cargoAtual;
                try (PreparedStatement p = c.prepareStatement("SELECT cargo FROM usuarios WHERE id=?")) {
                    p.setInt(1, id);
                    try (ResultSet r = p.executeQuery()) {
                        if (!r.next()) { c.rollback(); return false; }
                        cargoAtual = r.getString(1);
                    }
                }
                if ("Product Owner".equals(cargoAtual) && !"Product Owner".equals(novoCargo)) {
                    try (PreparedStatement p = c.prepareStatement(
                            "SELECT COUNT(*) FROM usuarios WHERE cargo='Product Owner'");
                         ResultSet r = p.executeQuery()) {
                        if (r.next() && r.getInt(1) <= 1) {
                            System.out.println("O sistema precisa manter pelo menos um Product Owner.");
                            c.rollback();
                            return false;
                        }
                    }
                }
                try (PreparedStatement p = c.prepareStatement("UPDATE usuarios SET cargo=? WHERE id=?")) {
                    p.setString(1, novoCargo);
                    p.setInt(2, id);
                    boolean atualizado = p.executeUpdate() > 0;
                    c.commit();
                    return atualizado;
                }
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao alterar cargo: " + e.getMessage());
            return false;
        }
    }

    public void migrarSenhasAntigas() throws SQLException {
        try (Connection c=Conexao.conectar()) {
            c.setAutoCommit(false);
            try (PreparedStatement p=c.prepareStatement("SELECT id,senha FROM usuarios");
                 ResultSet r=p.executeQuery()) {
                List<Map.Entry<Integer,String>> pendentes=new ArrayList<>();
                while(r.next()) {
                    String senha=r.getString("senha");
                    if(senha!=null && !senha.startsWith("pbkdf2_sha256$"))
                        pendentes.add(Map.entry(r.getInt("id"),senha));
                }
                try (PreparedStatement u=c.prepareStatement("UPDATE usuarios SET senha=? WHERE id=?")) {
                    for(var item:pendentes) {
                        u.setString(1,Senhas.gerarHash(item.getValue()));
                        u.setInt(2,item.getKey());
                        u.executeUpdate();
                    }
                }
                c.commit();
            } catch(SQLException | RuntimeException e) { c.rollback(); throw e; }
        }
    }
}
