package servlet;

import dao.UsuarioDAO;
import dto.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/perfil")
public class PerfilServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        usuario.setNome(req.getParameter("nome"));
        usuario.setEmail(req.getParameter("email"));
        usuario.setDescricao(req.getParameter("descricao"));

        String novaSenha = req.getParameter("novaSenha");
        String securityKey = req.getParameter("securityKey");
        boolean alterarSenha = false;

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (novaSenha != null && !novaSenha.isEmpty()) {
            if (usuario.getSecurityKey() != null && usuario.getSecurityKey().equals(securityKey)) {
                usuario.setSenha(novaSenha);
                alterarSenha = true;
            } else {
                resp.getWriter().write("{\"status\":\"erro\",\"mensagem\":\"Chave de Segurança (Security Key) incorreta! A sua senha não foi alterada.\"}");
                return;
            }
        }

        UsuarioDAO dao = new UsuarioDAO();
        try {
            dao.atualizar(usuario, alterarSenha);
            session.setAttribute("usuarioLogado", usuario);
            resp.getWriter().write("{\"status\":\"ok\",\"mensagem\":\"Dados atualizados com sucesso!\"}");
        } catch (Exception e) {
            resp.getWriter().write("{\"status\":\"erro\",\"mensagem\":\"" + e.getMessage() + "\"}");
        }
    }
}