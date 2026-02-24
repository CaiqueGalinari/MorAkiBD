-- Busca por Tipo
CREATE INDEX idx_moradia_tipo ON moradia(tipo);

-- Busca por Faixa de Preço
CREATE INDEX idx_moradia_valor ON moradia(valor);

-- Busca por Endereço/Cidade
CREATE INDEX idx_moradia_endereco ON moradia(endereco);

-- Quebra o endereço em partes para filtrar

CREATE OR REPLACE VIEW vw_imoveis_detalhados AS
SELECT 
    m.id_moradia,
    m.tipo,
    m.valor,
    m.max_inquilino,
    m.tot_inquilino,
    m.descricao,
    m.nome_dono,
    m.telefone_dono,
    m.tempo_aluguel,
    m.endereco AS endereco_completo,
    TRIM(SPLIT_PART(m.endereco, ',', 4)) AS bairro,  -- Bairro
    TRIM(SPLIT_PART(m.endereco, ',', 5)) AS cidade,  -- Cidade
    TRIM(SPLIT_PART(m.endereco, ',', 6)) AS uf,      -- UF
    (SELECT caminho FROM foto_moradia WHERE id_moradia = m.id_moradia ORDER BY ordem ASC LIMIT 1) AS foto_principal
FROM moradia m;