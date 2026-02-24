-- [CADASTRAR_MORADIA]
INSERT INTO moradia (endereco, tot_inquilino, max_inquilino, tipo, nome_dono, telefone_dono, tempo_aluguel, valor, descricao, id_quem_cadastrou) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)

-- [LISTAR_TODAS_MORADIAS]
SELECT * FROM vw_imoveis_detalhados

-- [LISTAR_MORADIAS_POR_USUARIO]
SELECT m.*, (SELECT caminho FROM foto_moradia f WHERE f.id_moradia = m.id_moradia ORDER BY ordem ASC LIMIT 1) AS foto_principal FROM moradia m WHERE id_quem_cadastrou = ?

-- [BUSCAR_MORADIA_POR_ID]
SELECT * FROM moradia WHERE id_moradia = ?

-- [ATUALIZAR_MORADIA]
UPDATE moradia SET endereco = ?, max_inquilino = ?, tipo = ?, nome_dono = ?, telefone_dono = ?, tempo_aluguel = ?, valor = ?, descricao = ? WHERE id_moradia = ? AND id_quem_cadastrou = ?

-- [DELETAR_MORADIA]
DELETE FROM moradia WHERE id_moradia = ?

-- [ADICIONAR_FOTO]
INSERT INTO foto_moradia (id_moradia, caminho, ordem) VALUES (?, ?, ?)

-- [LISTAR_FAVORITOS]
SELECT id_moradia FROM favoritos WHERE id_usuario = ?

-- [ADICIONAR_FAVORITO]
INSERT INTO favoritos (id_usuario, id_moradia) VALUES (?, ?)

-- [REMOVER_FAVORITO]
DELETE FROM favoritos WHERE id_usuario = ? AND id_moradia = ?

-- [ATUALIZAR_USUARIO_COM_SENHA]
UPDATE usuario SET nome = ?, descricao = ?, email = ?, senha = ? WHERE id_usuario = ?

-- [ATUALIZAR_USUARIO_SEM_SENHA]
UPDATE usuario SET nome = ?, descricao = ?, email = ? WHERE id_usuario = ?

-- [BUSCA_AVANCADA_BASE]
SELECT * FROM vw_imoveis_detalhados WHERE 1=1

-- [BUSCA_AVANCADA_FAVORITOS_BASE]
SELECT v.* FROM vw_imoveis_detalhados v INNER JOIN favoritos f ON v.id_moradia = f.id_moradia WHERE f.id_usuario = ?

-- [FILTRO_CIDADE]
 AND cidade IN (

-- [FILTRO_BAIRRO]
 AND bairro IN (

-- [FILTRO_TIPO]
 AND tipo = ?

-- [FILTRO_VALOR]
 AND valor <= ?

-- [ORDEM_MENOR_PRECO]
 ORDER BY valor ASC

-- [ORDEM_MAIOR_PRECO]
 ORDER BY valor DESC