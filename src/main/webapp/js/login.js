document.addEventListener('DOMContentLoaded', function() {
    const formLogin = document.querySelector('form');

    if(formLogin) {
        formLogin.addEventListener('submit', function(event) {
            event.preventDefault();

            const formData = new URLSearchParams();
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
                    window.location.href = 'pages/listagem.html';
                } else {
                    alert('Erro: ' + data.mensagem);
                }
            })
            .catch(error => console.error('Erro no login:', error));
        });
    }
});