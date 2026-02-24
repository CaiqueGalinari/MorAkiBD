package servlet;

import dao.FotoMoradiaDAO;
import dao.MoradiaDAO;
import dto.Moradia;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@WebServlet("/moradias")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 10,       // 10MB limite por ficheiro
        maxRequestSize = 1024 * 1024 * 50     // 50MB limite total
)

public class MoradiaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        MoradiaDAO dao = new MoradiaDAO();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (idParam != null && !idParam.isEmpty()) {
            dto.Moradia m = dao.buscarPorId(Integer.parseInt(idParam));
            resp.getWriter().write(new Gson().toJson(m));
            return;
        }

        String[] cidades = req.getParameterValues("cidades");
        String[] bairros = req.getParameterValues("bairros");
        String tipo = req.getParameter("tipo");
        String valorMaxStr = req.getParameter("valorMax");
        String ordem = req.getParameter("ordem");
        String apenasFavStr = req.getParameter("apenasFav");

        double valorMax = (valorMaxStr != null && !valorMaxStr.isEmpty()) ? Double.parseDouble(valorMaxStr) : 99999.0;
        boolean apenasFav = "true".equals(apenasFavStr);

        int idUsuarioLogado = 0;
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("usuarioLogado") != null) {
            dto.Usuario u = (dto.Usuario) session.getAttribute("usuarioLogado");
            idUsuarioLogado = u.getIdUsuario();
        }

        List<dto.Moradia> moradias;
        if (cidades == null && bairros == null && (tipo == null || tipo.isEmpty()) && valorMaxStr == null && ordem == null && !apenasFav) {
            moradias = dao.listarTodas();
        } else {
            moradias = dao.buscarComFiltros(cidades, bairros, tipo, valorMax, ordem, apenasFav, idUsuarioLogado);
        }

        resp.getWriter().write(new Gson().toJson(moradias));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Verificação da Sessão
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        dto.Usuario usuarioLogado = (dto.Usuario) session.getAttribute("usuarioLogado");

        // 2. Receber os parâmetros reais do HTML
        String idMoradiaStr = req.getParameter("idMoradia");
        String endereco = req.getParameter("endereco");
        int totInquilino = Integer.parseInt(req.getParameter("totInquilino") != null ? req.getParameter("totInquilino") : "0");
        int maxInquilino = Integer.parseInt(req.getParameter("maxInquilino") != null ? req.getParameter("maxInquilino") : "1");
        String tipo = req.getParameter("tipo");
        String nomeDono = req.getParameter("nomeDono");
        String telefoneDono = req.getParameter("telefoneDono");
        int tempoAluguel = Integer.parseInt(req.getParameter("tempoAluguel") != null ? req.getParameter("tempoAluguel") : "0");
        double valor = Double.parseDouble(req.getParameter("valor") != null ? req.getParameter("valor") : "0");
        String descricao = req.getParameter("descricao");

        // Montar o objeto Moradia
        dto.Moradia m = new dto.Moradia();
        m.setEndereco(endereco);
        m.setTotInquilino(totInquilino);
        m.setMaxInquilino(maxInquilino);
        m.setTipo(tipo);
        m.setNomeDono(nomeDono);
        m.setTelefoneDono(telefoneDono);
        m.setTempoAluguel(tempoAluguel);
        m.setValor(valor);
        m.setDescricao(descricao);
        m.setIdQuemCadastrou(usuarioLogado.getIdUsuario());

        // 3. Salvar na Base de Dados e capturar o ID
        MoradiaDAO dao = new MoradiaDAO();
        int idSalvo = -1;
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (idMoradiaStr != null && !idMoradiaStr.isEmpty()) {
                m.setIdMoradia(Integer.parseInt(idMoradiaStr));
                dao.atualizar(m);
                idSalvo = m.getIdMoradia();
            } else {
                idSalvo = dao.cadastrar(m);
            }

            // 4. LÓGICA DO UPLOAD DE FOTOGRAFIA
            Part filePart = req.getPart("foto");

            // Verifica se o utilizador enviou realmente um ficheiro
            if (filePart != null && filePart.getSize() > 0) {
                String basePath = getServletContext().getRealPath("/") + "Imagens" + File.separator + "MorAki";
                File pastaUpload = new File(basePath);
                if (!pastaUpload.exists()) pastaUpload.mkdirs();

                String nomeOriginal = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
                String nomeFinal = "moradia_" + idSalvo + "_" + System.currentTimeMillis() + extensao;

                filePart.write(basePath + File.separator + nomeFinal);

                String caminhoBanco = "Imagens/MorAki/" + nomeFinal;
                FotoMoradiaDAO fotoDao = new FotoMoradiaDAO();
                fotoDao.adicionar(idSalvo, caminhoBanco, 1);
            }

            resp.getWriter().write("{\"status\":\"ok\",\"mensagem\":\"Imóvel salvo com sucesso!\"}");
        } catch (Exception e) {
            e.printStackTrace();
            java.util.Map<String, String> respostaErro = new java.util.HashMap<>();
            respostaErro.put("status", "erro");
            respostaErro.put("mensagem", e.getMessage());
            resp.getWriter().write(new com.google.gson.Gson().toJson(respostaErro));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Verifica a sessão para impedir que visitantes apaguem imóveis
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"status\":\"erro\",\"mensagem\":\"Você precisa fazer login.\"}");
            return;
        }

        // Captura o ID do imóvel que foi enviado na URL
        int idMoradia = Integer.parseInt(req.getParameter("id"));

        MoradiaDAO dao = new MoradiaDAO();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            dao.deletar(idMoradia);
            resp.getWriter().write("{\"status\":\"ok\",\"mensagem\":\"Imóvel excluído com sucesso!\"}");
        } catch (Exception e) {
            resp.getWriter().write("{\"status\":\"erro\",\"mensagem\":\"" + e.getMessage() + "\"}");
        }
    }
}