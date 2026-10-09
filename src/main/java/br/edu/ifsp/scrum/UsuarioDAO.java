
package br.edu.ifsp.scrum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public boolean salvar(Usuario usuario) {
        String sql = """
            INSERT INTO usuarios (nome, email, senha, cargo)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, usuario.getNome());
            comando.setString(2, usuario.getEmail());
            comando.setString(3, usuario.getSenha());
            comando.setString(4, usuario.getCargo());

            comando.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao salvar usuário: " + e.getMessage());
            return false;
        }
    }

    public List<Usuario> listar() {
        List<Usuario> usuarios = new ArrayList<>();

        String sql = """
            SELECT id, nome, email, senha, cargo
            FROM usuarios
            ORDER BY id
            """;

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                Usuario usuario = new Usuario(
                    resultado.getInt("id"),
                    resultado.getString("nome"),
                    resultado.getString("email"),
                    resultado.getString("senha"),
                    resultado.getString("cargo")
                );

                usuarios.add(usuario);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar usuários: " + e.getMessage());
        }

        return usuarios;
    }
}