document.addEventListener('DOMContentLoaded', function() {
    const formLogin = document.querySelector('form');

    if(formLogin) {
        formLogin.addEventListener('submit', function(event) {
            event.preventDefault();

            const formData = new URLSearchParams();
            // Certifique-se de que os IDs no seu index.html são 'email' e 'senha'
            formData.append('email', document.getElementById('email').value);
            formData.append('senha', document.getElementById('senha').value);

            fetch('/moraki/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: formData.toString()
            })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'ok') {
                    // Se o login der certo, envia para a tela de listagem
                    window.location.href = 'pages/listagem.html';
                } else {
                    alert('Erro: ' + data.mensagem);
                }
            })
            .catch(error => console.error('Erro no login:', error));
        });
    }
});