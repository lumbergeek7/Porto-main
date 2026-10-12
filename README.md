# 0. Objetivos

1. Validar os endpoints disponiveis.
2. Corrigir problemas explicando causas e soluções.
3. Adicionar testes pertinentes.
4. Documentar alterações e resultados (evidências em /Porto-main/docs).
5. Outros.

# 1. Endpoints
Endpoints testados com sucesso após correções (via 'curl' e/ou browser).
Os prints de tela estão em /Porto-main/docs.
```http
GET /api/cnaes
GET /api/cnaes/buscar?termo=programas
GET /api/cnaes/codigo?codigo=6201-5/01
GET /api/cadastros-secundarios
GET /api/cadastros-secundarios/validar-cnae?codigoCnae=6201-5/01
POST /api/cadastros-secundarios
```

# 2. Correções (5 ajustes)

### Ajuste 1:
- *Problema 1*: Busca de descrição usando termo tem retorno apenas se termo estiver no início da descrição.
- *Correção*: Alterada query em AtividadeEconomicaCnaeRepository para lower(concat('%', :termo, '%')).

### Ajuste 2:
- *Problema 2*: Em AtividadeEconomicaCnaeServiceImpl, findByCodigo retorna getFirst caso nao encontre codigo solicitado (orElseGet()).
- *Correção*: Mudança de orElseGet para orElseThrow e criação de CodigoNotFoundException e GlobalExcetionHandler.

### Ajuste 3:
- *Problema 3*: Em CadastroSecundarioServiceImpl, os métodos de busca com findByCodigo retornavam getFirst caso nao encontressem codigo solicitado (orElseGet()).
- *Correção*: Mudança de orElseGet para orElseThrow e criação de método único para busca de Cnae (buscarCnae(String codigoCnae)).

### Ajuste 4:
- *Problema 4*: Métodos do layer service não garantem atomicidade.
- *Correção*: Adicionado @Transactional nas classes de 'service'.

### Ajuste 5:
- *Problema 5*: POST de cadastro secundário aceita strings vazias.
- *Correção*: Adicionado @Valid em controller e @NotBlank no DTO.

# 3. Testes

### Testes unitários
- Adicionados testes unitários para service e controller.

### Teste integrado
- Adicionado teste integrado para o cadastro secundário.

# 4. Evidências de execução e de testes

Todos os prints de tela estão organizados em /Porto-main/docs.

# 5. Outros

As validações e correções se basearam nos comportamentos esperados e exemplos citados no enunciado da atividade.

Comportamentos esperados:

- 1-) `GET /api/cnaes` deve retornar todas as atividades cadastradas.
- 2-) `GET /api/cnaes/buscar?termo={texto}` deve buscar CNAEs que contenham o texto informado em qualquer parte da descricao, ignorando maiusculas e minusculas.
- 3-) `GET /api/cnaes/codigo?codigo={codigo}` deve retornar o CNAE do codigo informado.
- 4-) Códigos CNAE inexistentes devem retornar uma resposta HTTP adequada para recurso nao encontrado.
- 5-) `POST /api/cadastros-secundarios` deve criar um cadastro vinculado a um CNAE existente.
- 6-) `GET /api/cadastros-secundarios/validar-cnae?codigoCnae={codigo}` deve validar se o CNAE informado pode ser usado no cadastro.
- 7-) Cadastros secundários com CNAE inexistente nao devem ser criados.

Exemplo:

```bash
curl http://localhost:8080/api/cnaes
curl "http://localhost:8080/api/cnaes/buscar?termo=programas"
curl "http://localhost:8080/api/cnaes/codigo?codigo=6201-5/01"
curl -X POST http://localhost:8080/api/cadastros-secundarios \
  -H "Content-Type: application/json" \
  -d '{"nomeFantasia":"Tech Porto","documento":"12345678000199","codigoCnae":"6201-5/01"}'
```
