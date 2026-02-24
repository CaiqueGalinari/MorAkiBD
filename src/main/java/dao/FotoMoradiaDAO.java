package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import util.GerenciadorSQL;

public class FotoMoradiaDAO {

    public void adicionar(int idMoradia, String caminho, int ordem) {
        String sql = GerenciadorSQL.getQuery("ADICIONAR_FOTO");

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMoradia);
            ps.setString(2, caminho);
            ps.setInt(3, ordem);

            ps.execute();
            System.out.println("Caminho da foto guardado na base de dados!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}