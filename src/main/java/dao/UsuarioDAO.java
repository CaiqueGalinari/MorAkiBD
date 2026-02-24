package dao;

import dto.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.GerenciadorSQL;

public class UsuarioDAO {

    public void cadastrar(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, descricao, email, senha, security_key) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);

            // Preenche os ? do SQL com os dados do objeto usuario
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getDescricao());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getSenha());
            ps.setString(5, usuario.getSecurityKey());

            ps.execute(); // Executa o INSERT
            System.out.println("Usuário gravado com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao cadastrar usuário: " + e.getMessage());
        } finally {
            // Fechando conexões p não travar o BD
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    // Recebe um boolean avisando se a senha deve ser alterada no banco
    public void atualizar(Usuario usuario, boolean alterarSenha) {
        String sql;

        if (alterarSenha) {
            sql = GerenciadorSQL.getQuery("ATUALIZAR_USUARIO_COM_SENHA");
        } else {
            sql = GerenciadorSQL.getQuery("ATUALIZAR_USUARIO_SEM_SENHA");
        }

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getDescricao());
            ps.setString(3, usuario.getEmail());

            if (alterarSenha) {
                ps.setString(4, usuario.getSenha());
                ps.setInt(5, usuario.getIdUsuario());
            } else {
                ps.setInt(4, usuario.getIdUsuario());
            }

            ps.execute();
            System.out.println("Usuário atualizado com sucesso no banco!");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage());
        }
    }

    public Usuario autenticar(String email, String senha) {
        String sql = "SELECT * FROM usuario WHERE email = ? AND senha = ?";
        Usuario u = null;

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, senha);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNome(rs.getString("nome"));
                    u.setEmail(rs.getString("email"));
                    u.setDescricao(rs.getString("descricao"));
                    u.setSecurityKey(rs.getString("security_key"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return u;
    }
}