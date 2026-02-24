document.querySelector('form').addEventListener('submit', function(event) {
    event.preventDefault();

    const senha = document.getElementById('senha').value;
    const confirmarSenha = document.getElementById('confirmar-senha').value;

    if (senha !== confirmarSenha) {
        alert('As senhas não coincidem!');
        return;
    }

    const formData = new URLSearchParams();
    formData.append('nome', document.getElementById('nome').value);
    formData.append('email', document.getElementById('email').value);
    formData.append('senha', senha);

    // O caminho '/MorAki/usuarios' deve corresponder ao nome do seu projeto no servidor
    fetch('/moraki/usuarios', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: formData.toString()
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === 'ok') {
            alert(data.mensagem);
            window.location.href = '../index.html';
        } else {
            alert('Erro: ' + data.mensagem);
        }
    })
    .catch(error => console.error('Erro:', error));
});