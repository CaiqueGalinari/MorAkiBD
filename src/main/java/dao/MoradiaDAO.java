package dao;

import dto.Moradia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;
import util.GerenciadorSQL;

public class MoradiaDAO {

    public int cadastrar(Moradia m) {
        String sql = GerenciadorSQL.getQuery("CADASTRAR_MORADIA");
        int idGerado = -1;

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getEndereco());
            ps.setInt(2, m.getTotInquilino());
            ps.setInt(3, m.getMaxInquilino());
            ps.setString(4, m.getTipo());
            ps.setString(5, m.getNomeDono());
            ps.setString(6, m.getTelefoneDono());
            ps.setInt(7, m.getTempoAluguel());
            ps.setDouble(8, m.getValor());
            ps.setString(9, m.getDescricao());
            ps.setInt(10, m.getIdQuemCadastrou());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idGerado = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao cadastrar moradia: " + e.getMessage());
        }
        return idGerado;
    }

    public List<Moradia> listarTodas() {
        List<Moradia> lista = new ArrayList<>();
        String sql = GerenciadorSQL.getQuery("LISTAR_TODAS_MORADIAS");

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Moradia m = new Moradia();
                m.setIdMoradia(rs.getInt("id_moradia"));
                m.setTipo(rs.getString("tipo"));
                m.setValor(rs.getDouble("valor"));
                m.setMaxInquilino(rs.getInt("max_inquilino"));
                m.setDescricao(rs.getString("descricao"));
                m.setEndereco(rs.getString("endereco_completo"));
                m.setNomeDono(rs.getString("nome_dono"));
                m.setTelefoneDono(rs.getString("telefone_dono"));
                m.setTempoAluguel(rs.getInt("tempo_aluguel"));
                m.setTotInquilino(rs.getInt("tot_inquilino"));
                m.setFotoPrincipal(rs.getString("foto_principal"));

                lista.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Moradia> listarPorUsuario(int idUsuario) {
        List<Moradia> lista = new ArrayList<>();
        String sql = GerenciadorSQL.getQuery("LISTAR_MORADIAS_POR_USUARIO");

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Moradia m = new Moradia();
                    m.setIdMoradia(rs.getInt("id_moradia"));
                    m.setTipo(rs.getString("tipo"));
                    m.setValor(rs.getDouble("valor"));
                    m.setMaxInquilino(rs.getInt("max_inquilino"));
                    m.setDescricao(rs.getString("descricao"));

                    // Lendo as colunas que faltavam para preencher o cartão!
                    m.setTempoAluguel(rs.getInt("tempo_aluguel"));
                    m.setEndereco(rs.getString("endereco"));
                    m.setFotoPrincipal(rs.getString("foto_principal"));

                    lista.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void deletar(int idMoradia) {
        String sql = GerenciadorSQL.getQuery("DELETAR_MORADIA");

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMoradia);
            ps.execute();
            System.out.println("Moradia deletada com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao deletar moradia: " + e.getMessage());
        }
    }

    public Moradia buscarPorId(int idMoradia) {
        String sql = GerenciadorSQL.getQuery("BUSCAR_MORADIA_POR_ID");
        Moradia m = null;

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMoradia);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    m = new Moradia();
                    m.setIdMoradia(rs.getInt("id_moradia"));
                    m.setEndereco(rs.getString("endereco"));
                    m.setTotInquilino(rs.getInt("tot_inquilino"));
                    m.setMaxInquilino(rs.getInt("max_inquilino"));
                    m.setTipo(rs.getString("tipo"));
                    m.setNomeDono(rs.getString("nome_dono"));
                    m.setTelefoneDono(rs.getString("telefone_dono"));
                    m.setTempoAluguel(rs.getInt("tempo_aluguel"));
                    m.setValor(rs.getDouble("valor"));
                    m.setDescricao(rs.getString("descricao"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return m;
    }

    public void atualizar(Moradia m) {
        String sql = GerenciadorSQL.getQuery("ATUALIZAR_MORADIA");

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getEndereco());
            ps.setInt(2, m.getMaxInquilino());
            ps.setString(3, m.getTipo());
            ps.setString(4, m.getNomeDono());
            ps.setString(5, m.getTelefoneDono());
            ps.setInt(6, m.getTempoAluguel());
            ps.setDouble(7, m.getValor());
            ps.setString(8, m.getDescricao());
            ps.setInt(9, m.getIdMoradia());
            ps.setInt(10, m.getIdQuemCadastrou());

            ps.execute();
            System.out.println("Moradia atualizada com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar moradia: " + e.getMessage());
        }
    }

    public List<Moradia> buscarComFiltros(String[] cidades, String[] bairros, String tipo, double valorMax, String ordem, boolean apenasFav, int idUsuario) {
        List<Moradia> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        // 1. Puxa a base da consulta
        if (apenasFav && idUsuario > 0) {
            sql.append(GerenciadorSQL.getQuery("BUSCA_AVANCADA_FAVORITOS_BASE"));
        } else {
            sql.append(GerenciadorSQL.getQuery("BUSCA_AVANCADA_BASE"));
        }

        // 2. Filtro de Cidades (Notem o " " antes de anexar!)
        if (cidades != null && cidades.length > 0) {
            sql.append(" ").append(GerenciadorSQL.getQuery("FILTRO_CIDADE"));
            for (int i = 0; i < cidades.length; i++) {
                sql.append("?");
                if (i < cidades.length - 1) sql.append(",");
            }
            sql.append(")");
        }

        // 3. Filtro de Bairros
        if (bairros != null && bairros.length > 0) {
            sql.append(" ").append(GerenciadorSQL.getQuery("FILTRO_BAIRRO"));
            for (int i = 0; i < bairros.length; i++) {
                sql.append("?");
                if (i < bairros.length - 1) sql.append(",");
            }
            sql.append(")");
        }

        // 4. Filtro de Tipo
        if (tipo != null && !tipo.isEmpty()) {
            sql.append(" ").append(GerenciadorSQL.getQuery("FILTRO_TIPO"));
        }

        // 5. Filtro de Valor
        sql.append(" ").append(GerenciadorSQL.getQuery("FILTRO_VALOR"));

        // 6. Ordenação
        if ("menor".equals(ordem)) {
            sql.append(" ").append(GerenciadorSQL.getQuery("ORDEM_MENOR_PRECO"));
        } else if ("maior".equals(ordem)) {
            sql.append(" ").append(GerenciadorSQL.getQuery("ORDEM_MAIOR_PRECO"));
        }

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;

            if (apenasFav && idUsuario > 0) {
                ps.setInt(index++, idUsuario);
            }
            if (cidades != null) {
                for (String c : cidades) ps.setString(index++, c);
            }
            if (bairros != null) {
                for (String b : bairros) ps.setString(index++, b);
            }
            if (tipo != null && !tipo.isEmpty()) {
                ps.setString(index++, tipo);
            }
            ps.setDouble(index++, valorMax);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Moradia m = new Moradia();
                    m.setIdMoradia(rs.getInt("id_moradia"));
                    m.setTipo(rs.getString("tipo"));
                    m.setValor(rs.getDouble("valor"));
                    m.setMaxInquilino(rs.getInt("max_inquilino"));
                    m.setDescricao(rs.getString("descricao"));
                    m.setEndereco(rs.getString("endereco_completo"));
                    m.setNomeDono(rs.getString("nome_dono"));
                    m.setTelefoneDono(rs.getString("telefone_dono"));
                    m.setTempoAluguel(rs.getInt("tempo_aluguel"));
                    m.setTotInquilino(rs.getInt("tot_inquilino"));
                    m.setFotoPrincipal(rs.getString("foto_principal"));
                    lista.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}