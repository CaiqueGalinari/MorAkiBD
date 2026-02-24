let meusImoveis = [];
let paginaAtual = 1;
const itensPorPagina = 3;

document.addEventListener('DOMContentLoaded', function() {
    fetch('/moraki/meus-imoveis')
        .then(response => {
            if (response.status === 401) {
                alert("Você precisa fazer login para ver seus imóveis.");
                window.location.href = '../index.html';
                throw new Error("Não autorizado");
            }
            return response.json();
        })
        .then(dados => {
            meusImoveis = dados;
            configurarBotoesPaginacao();
            renderizarImoveis();
        })
        .catch(erro => console.error('Erro ao carregar meus imóveis:', erro));
});

function configurarBotoesPaginacao() {
    const btnAnterior = document.getElementById('btn-anterior');
    const btnProximo = document.getElementById('btn-proximo');

    if (btnAnterior) {
        btnAnterior.addEventListener('click', () => {
            if (paginaAtual > 1) {
                paginaAtual--;
                renderizarImoveis();
            }
        });
    }

    if (btnProximo) {
        btnProximo.addEventListener('click', () => {
            const totalPaginas = Math.ceil(meusImoveis.length / itensPorPagina);
            if (paginaAtual < totalPaginas) {
                paginaAtual++;
                renderizarImoveis();
            }
        });
    }
}

function renderizarImoveis() {
    const container = document.getElementById('container-imoveis');
    const divPaginacao = document.querySelector('.paginacao');
    const infoPaginacao = document.getElementById('info-paginacao');

    container.innerHTML = '';

    if (meusImoveis.length === 0) {
        container.innerHTML = '<p class="aviso-vazio">Você ainda não possui imóveis cadastrados.</p>';
        if (divPaginacao) divPaginacao.style.display = 'none';
        return;
    }

    if (divPaginacao) divPaginacao.style.display = 'flex';

    const totalPaginas = Math.ceil(meusImoveis.length / itensPorPagina);
    if (infoPaginacao) infoPaginacao.innerText = `${paginaAtual} de ${totalPaginas}`;

    const inicio = (paginaAtual - 1) * itensPorPagina;
    const fim = inicio + itensPorPagina;
    const imoveisPagina = meusImoveis.slice(inicio, fim);

    for (let i = 0; i < imoveisPagina.length; i++) {
        let imovel = imoveisPagina[i];
        let card = document.createElement('div');
        card.className = 'card-imovel';

        let valorFormatado = imovel.valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

        let htmlFoto = imovel.fotoPrincipal
            ? `<img src="/moraki/${imovel.fotoPrincipal}" alt="Foto do Imóvel" class="card-img" style="width: 150px; height: 100px; object-fit: cover;">`
            : `<div class="sem-foto" style="width: 150px; height: 100px; display: flex; align-items: center; justify-content: center;">Sem foto</div>`;

        card.innerHTML = `
            ${htmlFoto}
            <div class="card-info">
                <h3>${imovel.tipo}</h3>
                <div class="card-detalhes-linha">
                    <span class="preco">${valorFormatado}</span>
                    <span class="info-extra"><i class="ph ph-calendar-blank"></i> ${imovel.tempoAluguel} meses</span>
                    <span class="info-extra"><i class="ph ph-user"></i> Max. ${imovel.maxInquilino} inquilinos</span>
                </div>
                <p class="endereco"><i class="ph ph-map-pin"></i> ${imovel.endereco}</p>
            </div>
            <div class="card-acoes">
                <button class="btn-acao info" title="Ver Informações"><i class="ph-fill ph-info"></i></button>
                <button class="btn-acao deletar" title="Excluir" onclick="deletarImovel(${imovel.idMoradia})"><i class="ph ph-trash"></i></button>
                <button class="btn-acao editar" title="Editar" onclick="editarImovel(${imovel.idMoradia})"><i class="ph-fill ph-pencil-simple"></i></button>
            </div>
        `;

        container.appendChild(card);
    }
}

function deletarImovel(id) {
    if (confirm("Tem certeza que deseja excluir este imóvel de forma permanente?")) {
        fetch('/moraki/moradias?id=' + id, {
            method: 'DELETE'
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'ok') {
                alert(data.mensagem);
                window.location.reload();
            } else {
                alert("Erro: " + data.mensagem);
            }
        })
        .catch(erro => console.error("Erro ao deletar:", erro));
    }
}

function editarImovel(id) {
    window.location.href = 'cadastro_imovel.html?id=' + id;
}