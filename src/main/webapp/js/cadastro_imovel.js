document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const idMoradia = urlParams.get('id');

    if (idMoradia) {
        document.querySelector('h2').innerText = "Editar Imóvel";
        document.querySelector('.btn-cadastrar').innerText = "Atualizar";

        fetch('/moraki/moradias?id=' + idMoradia)
            .then(response => response.json())
            .then(imovel => {
                document.getElementById('tipo').value = imovel.tipo;
                document.getElementById('tempo').value = imovel.tempoAluguel;
                document.getElementById('valor').value = imovel.valor;
                document.getElementById('inquilinos').value = imovel.maxInquilino;
                document.getElementById('dono').value = imovel.nomeDono || '';
                document.getElementById('telefone').value = imovel.telefoneDono || '';
                document.getElementById('descricao').value = imovel.descricao || '';

                if (imovel.endereco) {
                    const partes = imovel.endereco.split(', ');
                        if (partes.length >= 7) {
                            if(document.getElementById('complemento')) document.getElementById('complemento').value = partes[0] !== 'Sem complemento' ? partes[0] : '';
                                document.getElementById('numero').value = partes[1];
                                document.getElementById('rua').value = partes[2];
                                document.getElementById('bairro').value = partes[3];
                                document.getElementById('cidade').value = partes[4];
                                document.getElementById('uf').value = partes[5];
                                document.getElementById('cep').value = partes[6];
                        }
                }
            })
            .catch(erro => console.error('Erro ao buscar imóvel:', erro));
    }

    document.querySelector('form').addEventListener('submit', function(event) {
        event.preventDefault();

        const complementoRaw = document.getElementById('complemento') ? document.getElementById('complemento').value : '';
        const complemento = complementoRaw.trim() !== '' ? complementoRaw : 'Sem complemento';
        const numero = document.getElementById('numero').value;
        const rua = document.getElementById('rua').value;
        const bairro = document.getElementById('bairro').value;
        const cidade = document.getElementById('cidade').value;
        const uf = document.getElementById('uf').value;
        const cep = document.getElementById('cep').value;
        const endereco = `${complemento}, ${numero}, ${rua}, ${bairro}, ${cidade}, ${uf}, ${cep}`;

        const tipo = document.getElementById('tipo').value;
        const tempoAluguel = document.getElementById('tempo').value;
        const valor = document.getElementById('valor').value;
        const maxInquilino = document.getElementById('inquilinos').value;
        const nomeDono = document.getElementById('dono').value;
        const telefoneDono = document.getElementById('telefone').value;
        const descricao = document.getElementById('descricao').value;

        const formData = new FormData();

        if (idMoradia) {
            formData.append('idMoradia', idMoradia);
        }

        formData.append('endereco', endereco);
        formData.append('totInquilino', 0);
        formData.append('maxInquilino', maxInquilino);
        formData.append('tipo', tipo);
        formData.append('nomeDono', nomeDono);
        formData.append('telefoneDono', telefoneDono);
        formData.append('tempoAluguel', tempoAluguel);
        formData.append('valor', valor);
        formData.append('descricao', descricao);

        const inputFoto = document.getElementById('foto');
        if (inputFoto.files.length > 0) {
            formData.append('foto', inputFoto.files[0]);
        }

        fetch('/moraki/moradias', {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (response.status === 401) {
                throw new Error('Sessão expirada. Faça login novamente.');
            }
            return response.json();
        })
        .then(data => {
            if (data.status === 'ok') {
                alert(data.mensagem);
                window.location.href = 'meus_imoveis.html';
            } else {
                alert('Erro: ' + data.mensagem);
            }
        })
        .catch(error => alert(error.message));
    });
});