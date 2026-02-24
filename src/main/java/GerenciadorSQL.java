package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class GerenciadorSQL {
    private static final Map<String, String> queries = new HashMap<>();

    // O bloco estático roda automaticamente assim que o sistema inicia
    static {
        carregarQueries();
    }

    private static void carregarQueries() {
        try (InputStream is = GerenciadorSQL.class.getClassLoader().getResourceAsStream("queries.sql")) {
            if (is == null) {
                throw new RuntimeException("Arquivo queries.sql não encontrado na pasta resources!");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String linha;
            String nomeQueryAtual = null;
            StringBuilder sqlAtual = new StringBuilder();

            while ((linha = reader.readLine()) != null) {
                linha = linha.trim();

                // Se achou uma tag nossa, ex: -- [LISTAR_TODAS_MORADIAS]
                if (linha.startsWith("-- [") && linha.endsWith("]")) {
                    if (nomeQueryAtual != null) {
                        queries.put(nomeQueryAtual, sqlAtual.toString().trim());
                        sqlAtual.setLength(0); // Limpa para a próxima query
                    }
                    nomeQueryAtual = linha.substring(4, linha.length() - 1);
                }
                // Se for linha de SQL normal (ignora comentários comuns começando com --)
                else if (!linha.isEmpty() && !linha.startsWith("--")) {
                    sqlAtual.append(linha).append(" ");
                }
            }
            // Salva a última query lida
            if (nomeQueryAtual != null) {
                queries.put(nomeQueryAtual, sqlAtual.toString().trim());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar queries.sql: " + e.getMessage());
        }
    }

    public static String getQuery(String nome) {
        String sql = queries.get(nome);
        if (sql == null) {
            throw new RuntimeException("Query SQL não encontrada no arquivo: " + nome);
        }
        return sql;
    }
}