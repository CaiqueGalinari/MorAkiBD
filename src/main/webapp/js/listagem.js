let imoveisGlobais = [];
let imoveisFiltrados = [];
let meusFavoritos = [];

let paginaAtual = 1;
const itensPorPagina = 3;

document.addEventListener('DOMContentLoaded', async function() {
    try {
        const resSessao = await fetch('/moraki/sessao');
        if (resSessao.ok) {
            const usuario = await resSessao.json();
            const msgOla = document.getElementById('mensagem-ola');
            if(msgOla) msgOla.innerText = "Olá, " + usuario.nome.split(' ')[0];
        } else {
            window.location.href = '../index.html';
            return;
        }
    } catch (erro) { console.error(erro); }

    const btnSair = document.getElementById('btn-sair');
    if (btnSair) {
        btnSair.addEventListener('click', function() {
            fetch('/moraki/logout', { method: 'POST' }).then(() => window.location.href = '../index.html');
        });
    }

    try {
        const resMoradias = await fetch('/moraki/moradias');
        const dadosIniciais = await resMoradias.json();

        const resFavoritos = await fetch('/moraki/favoritos');
        if (resFavoritos.ok) meusFavoritos = await resFavoritos.json();

        dadosIniciais.forEach(m => {
            let partes = m.endereco ? m.endereco.split(', ') : [];
            m.bairroStr = partes.length > 3 ? partes[3].trim() : "Outro";
            m.cidadeStr = partes.length > 4 ? partes[4].trim() : "Outra";
        });

        imoveisGlobais = dadosIniciais;
        imoveisFiltrados = dadosIniciais;

        renderizarFiltroCidades();
        renderizarImoveis();
    } catch (erro) { console.error(erro); }

    const filtroValor = document.getElementById('filtro-valor');
    const labelValor = document.getElementById('label-valor');
    if (filtroValor && labelValor) {
        filtroValor.addEventListener('input', function() {
            let valorFmt = parseFloat(this.value).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
            labelValor.innerText = "Até " + valorFmt;
        });
        filtroValor.addEventListener('change', aplicarFiltros);
    }

    document.getElementById('filtro-tipo')?.addEventListener('change', aplicarFiltros);
    document.getElementById('ordem-valor')?.addEventListener('change', aplicarFiltros);
    document.getElementById('filtro-favoritos')?.addEventListener('change', aplicarFiltros);

    document.getElementById('filtro-cidade')?.addEventListener('change', () => {
        renderizarFiltroBairros();
        aplicarFiltros();
    });
    document.getElementById('filtro-bairro')?.addEventListener('change', aplicarFiltros);
});

function renderizarFiltroCidades() {
    const select = document.getElementById('filtro-cidade');
    if(!select) return;

    let cidadesUnicas = [...new Set(imoveisGlobais.map(m => m.cidadeStr))].sort();
    let cidadeAtual = select.value;

    select.innerHTML = '<option value="">Todas as cidades</option>';

    cidadesUnicas.forEach(cidade => {
        let option = document.createElement('option');
        option.value = cidade;
        option.textContent = cidade;
        if (cidade === cidadeAtual) option.selected = true;
        select.appendChild(option);
    });
}

function renderizarFiltroBairros() {
    const selectCidade = document.getElementById('filtro-cidade');
    const selectBairro = document.getElementById('filtro-bairro');
    if(!selectCidade || !selectBairro) return;

    const cidadeSelecionada = selectCidade.value;
    let bairroAtual = selectBairro.value;

    if(!cidadeSelecionada) {
        selectBairro.innerHTML = '<option value="">Selecione a cidade...</option>';
        return;
    }

    let imoveisNaCidade = imoveisGlobais.filter(m => m.cidadeStr === cidadeSelecionada);
    let bairrosUnicos = [...new Set(imoveisNaCidade.map(m => m.bairroStr))].sort();

    selectBairro.innerHTML = '<option value="">Todos os bairros</option>';

    bairrosUnicos.forEach(bairro => {
        let option = document.createElement('option');
        option.value = bairro;
        option.textContent = bairro;
        if (bairro === bairroAtual) option.selected = true;
        selectBairro.appendChild(option);
    });
}

function aplicarFiltros() {
    const cidade = document.getElementById('filtro-cidade')?.value || '';
    const bairro = document.getElementById('filtro-bairro')?.value || '';
    const tipo = document.getElementById('filtro-tipo')?.value || '';
    const valor = document.getElementById('filtro-valor')?.value || '3000';
    const ordem = document.getElementById('ordem-valor')?.value || '';
    const apenasFav = document.getElementById('filtro-favoritos')?.checked || false;

    const params = new URLSearchParams();
    if (tipo) params.append('tipo', tipo);
    params.append('valorMax', valor);
    if (ordem) params.append('ordem', ordem);
    if (apenasFav) params.append('apenasFav', 'true');

    if (cidade) params.append('cidades', cidade);
    if (bairro) params.append('bairros', bairro);

    fetch(`/moraki/moradias?${params.toString()}`)
        .then(response => response.json())
        .then(dadosFiltrados => {
            dadosFiltrados.forEach(m => {
                let partes = m.endereco ? m.endereco.split(', ') : [];
                m.bairroStr = partes.length > 3 ? partes[3].trim() : "Outro";
                m.cidadeStr = partes.length > 4 ? partes[4].trim() : "Outra";
            });

            imoveisFiltrados = dadosFiltrados;
            paginaAtual = 1;
            renderizarImoveis();
        })
        .catch(erro => console.error("Erro ao buscar filtros:", erro));
}

function mudarPagina(direcao) {
    const totalPaginas = Math.ceil(imoveisFiltrados.length / itensPorPagina);
    paginaAtual += direcao;
    if (paginaAtual < 1) paginaAtual = 1;
    if (paginaAtual > totalPaginas) paginaAtual = totalPaginas;
    renderizarImoveis();
}

function renderizarImoveis() {
    const container = document.getElementById('lista-imoveis');
    const areaPaginacao = document.getElementById('area-paginacao');
    const textoPaginacao = document.getElementById('texto-paginacao');
    container.innerHTML = '';

    if (imoveisFiltrados.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: white; font-size: 1.2rem; margin-top: 40px; text-shadow: 1px 1px 3px rgba(0,0,0,0.5);">Nenhum imóvel encontrado com estes filtros.</p>';
        if (areaPaginacao) areaPaginacao.style.display = 'none';
        return;
    }

    if (areaPaginacao) areaPaginacao.style.display = 'flex';
    const totalPaginas = Math.ceil(imoveisFiltrados.length / itensPorPagina);
    if (textoPaginacao) textoPaginacao.innerText = `${paginaAtual} de ${totalPaginas}`;

    const inicio = (paginaAtual - 1) * itensPorPagina;
    const fim = inicio + itensPorPagina;
    const imoveisPagina = imoveisFiltrados.slice(inicio, fim);

    imoveisPagina.forEach(imovel => {
        let card = document.createElement('div');
        card.style.display = 'flex';
        card.style.background = 'white';
        card.style.borderRadius = '15px';
        card.style.overflow = 'hidden';
        card.style.boxShadow = '0 4px 10px rgba(0,0,0,0.15)';
        card.style.height = '180px';
        card.style.width = '100%';

        let valorFormatado = imovel.valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
        let isFav = meusFavoritos.includes(imovel.idMoradia);
        let iconeCoracao = isFav ? '<i class="ph-fill ph-heart" style="color: #a63a58;"></i>' : '<i class="ph ph-heart" style="color: #aaa;"></i>';

        let tel = imovel.telefoneDono || 'Não informado';
        if (tel.length === 11) {
            tel = `(${tel.substring(0,2)}) ${tel.substring(2,7)}-${tel.substring(7)}`;
        } else if (tel.length === 10) {
            tel = `(${tel.substring(0,2)}) ${tel.substring(2,6)}-${tel.substring(6)}`;
        }

        let htmlFoto = imovel.fotoPrincipal
            ? `<img src="/moraki/${imovel.fotoPrincipal}" alt="Foto" style="width: 100%; height: 100%; object-fit: cover;">`
            : `<div style="width: 100%; height: 100%; background: #eee; display: flex; align-items: center; justify-content: center; color: #999;">Sem foto</div>`;

        card.innerHTML = `
            <div style="width: 260px; height: 100%; flex-shrink: 0;">
                ${htmlFoto}
            </div>
            <div style="flex: 1; padding: 20px; display: flex; flex-direction: column; justify-content: space-between;">
                <div>
                    <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                        <h3 style="margin: 0; font-size: 1.3rem; color: #111;">${imovel.tipo} ${imovel.endereco ? 'em ' + imovel.endereco.split(', ')[3] : ''}</h3>
                        <button title="Favoritar" onclick="toggleFavorito(${imovel.idMoradia}, this)" style="background: none; border: none; font-size: 1.8rem; cursor: pointer; padding: 0;">
                            ${iconeCoracao}
                        </button>
                    </div>

                    <div style="display: flex; align-items: center; gap: 20px; margin-top: 15px; flex-wrap: wrap;">
                        <span style="color: #a63a58; font-weight: bold; font-size: 1.2rem;">${valorFormatado}/mês</span>
                        <span style="color: #666; font-size: 0.95rem; display: flex; align-items: center; gap: 5px;"><i class="ph ph-calendar-blank"></i> ${imovel.tempoAluguel} meses</span>
                        <span style="color: #666; font-size: 0.95rem; display: flex; align-items: center; gap: 5px;"><i class="ph ph-user"></i> Max. ${imovel.maxInquilino} inq.</span>
                        <span style="color: #25D366; font-weight: bold; font-size: 0.95rem; display: flex; align-items: center; gap: 5px;" title="Telefone do proprietário"><i class="ph ph-whatsapp-logo" style="font-size: 1.1rem;"></i> ${tel}</span>
                    </div>
                </div>
                <p style="color: #888; font-size: 0.95rem; margin: 0; display: flex; align-items: center; gap: 5px; margin-top: 10px;">
                    <i class="ph ph-map-pin"></i> ${imovel.endereco || imovel.descricao}
                </p>
            </div>
        `;
        container.appendChild(card);
    });
}

function toggleFavorito(idMoradia, botaoHtml) {
    const isFav = meusFavoritos.includes(idMoradia);
    const acao = isFav ? "remover" : "adicionar";
    const formData = new URLSearchParams();
    formData.append('idMoradia', idMoradia);
    formData.append('acao', acao);

    fetch('/moraki/favoritos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    }).then(response => {
        if (response.status === 401) { alert("Faça login para favoritar!"); return; }
        if (response.ok) {
            if (isFav) {
                meusFavoritos = meusFavoritos.filter(id => id !== idMoradia);
                botaoHtml.innerHTML = '<i class="ph ph-heart" style="color: #aaa;"></i>';
            } else {
                meusFavoritos.push(idMoradia);
                botaoHtml.innerHTML = '<i class="ph-fill ph-heart" style="color: #a63a58;"></i>';
            }
            if (document.getElementById('filtro-favoritos')?.checked) aplicarFiltros();
        }
    }).catch(e => console.error(e));
}