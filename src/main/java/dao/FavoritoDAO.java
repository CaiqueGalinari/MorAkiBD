package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.GerenciadorSQL;

public class FavoritoDAO {

    public List<Integer> listarIdsFavoritos(int idUsuario) {
        List<Integer> lista = new ArrayList<>();
        String sql = GerenciadorSQL.getQuery("LISTAR_FAVORITOS");

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getInt("id_moradia"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void adicionar(int idUsuario, int idMoradia) {
        String sql = GerenciadorSQL.getQuery("ADICIONAR_FAVORITO");
        executarUpdate(sql, idUsuario, idMoradia);
    }

    public void remover(int idUsuario, int idMoradia) {
        String sql = GerenciadorSQL.getQuery("REMOVER_FAVORITO");
        executarUpdate(sql, idUsuario, idMoradia);
    }

    private void executarUpdate(String sql, int idUsuario, int idMoradia) {
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idMoradia);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}