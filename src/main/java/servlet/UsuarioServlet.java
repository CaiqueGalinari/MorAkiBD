package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/usuarios")
public class UsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nome = req.getParameter("nome");
        String email = req.getParameter("email");
        String senha = req.getParameter("senha");
        String descricao = new String();
        descricao = "Escreva aqui sua descrição!";

        dto.Usuario u = new dto.Usuario();
        u.setNome(nome);
        u.setEmail(email);
        u.setSenha(senha);
        u.setDescricao(descricao);

        String key = java.util.UUID.randomUUID().toString().substring(0, 8);
        u.setSecurityKey(key);

        dao.UsuarioDAO dao = new dao.UsuarioDAO();

        try {
            dao.cadastrar(u);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"status\":\"ok\",\"mensagem\":\"Usuário cadastrado com sucesso\"}");
        } catch (Exception e) {
            resp.setContentType("application/json");
            resp.getWriter().write("{\"status\":\"erro\",\"mensagem\":\"" + e.getMessage() + "\"}");
        }
    }
}
