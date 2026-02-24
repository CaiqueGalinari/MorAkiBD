document.addEventListener('DOMContentLoaded', async function() {
    try {
        const response = await fetch('/moraki/sessao');
        if (response.ok) {
            const usuario = await response.json();
            document.getElementById('nome').value = usuario.nome;
            document.getElementById('email').value = usuario.email;
            document.getElementById('descricao').value = usuario.descricao || '';
        } else {
            alert("Você precisa fazer login!");
            window.location.href = '../index.html';
        }
    } catch (erro) {
        console.error("Erro ao carregar perfil:", erro);
    }

    document.getElementById('form-perfil').addEventListener('submit', function(e) {
        e.preventDefault();

        const novaSenha = document.getElementById('nova-senha').value;
        const securityKey = document.getElementById('security-key').value;

        if (novaSenha && !securityKey) {
            alert("Para alterar a sua senha, você precisa informar a sua Chave de Segurança!");
            document.getElementById('security-key').focus();
            return;
        }

        const formData = new URLSearchParams();
        formData.append('nome', document.getElementById('nome').value);
        formData.append('email', document.getElementById('email').value);
        formData.append('descricao', document.getElementById('descricao').value);
        formData.append('novaSenha', novaSenha);
        formData.append('securityKey', securityKey);

        fetch('/moraki/perfil', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData.toString()
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'ok') {
                alert(data.mensagem);
                document.getElementById('nova-senha').value = '';
                document.getElementById('security-key').value = '';
            } else {
                alert('Erro: ' + data.mensagem);
            }
        })
        .catch(erro => console.error("Erro ao atualizar:", erro));
    });
});