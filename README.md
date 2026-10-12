# 0. Objetivos

1. Validar os endpoints disponiveis.
2. Corrigir problemas explicando causas e soluções.
3. Adicionar testes pertinentes.
4. Documentar alterações e resultados (evidências em [/Porto-main/docs](https://github.com/lumbergeek7/Porto-main/tree/fix-porto/docs)).
5. Outros.

# 1. Endpoints
Endpoints testados com sucesso após correções (via 'curl' e/ou browser).
Os prints de tela estão em [/Porto-main/docs/1-Endpoints.jpeg](https://github.com/lumbergeek7/Porto-main/tree/fix-porto/docs/1-Endpoints.jpeg).
```http
GET /api/cnaes
GET /api/cnaes/buscar?termo=programas
GET /api/cnaes/codigo?codigo=6201-5/01
GET /api/cadastros-secundarios
GET /api/cadastros-secundarios/validar-cnae?codigoCnae=6201-5/01
POST /api/cadastros-secundarios
```

# 2. Correções (6 ajustes)
Foram realizados 6 ajustes e propostos 2 ajustes adicionais
Os prints de tela estão em [/Porto-main/docs/2-Changelog.jpeg](https://github.com/lumbergeek7/Porto-main/tree/fix-porto/docs/2-Changelog.jpeg).

### Ajuste 1:
- *Problema*: Busca de descrição usando termo tem retorno apenas se termo estiver no início da descrição.
- *Causa*: Em AtividadeEconomicaCnaeRepository é usado lower(concat(:termo, '%')).
- *Solução*: Alterada query em AtividadeEconomicaCnaeRepository para lower(concat('%', :termo, '%')).

### Ajuste 2:
- *Problema*: Ao procurar em /cnaes um código inexistente no BD, endpoint retorna primeiro registro ao invés apontar erro.
- *Causa*: Em AtividadeEconomicaCnaeServiceImpl, findByCodigo retorna getFirst caso nao encontre codigo solicitado (orElseGet()).
- *Solução*: Mudança de orElseGet para orElseThrow e criação de CodigoNotFoundException e GlobalExcetionHandler.

### Ajuste 3:
- *Problema*: Ao procurar em /cadastro-secundário um código inexistente no BD, endpoint retorna primeiro registro ao invés apontar erro.
- *Causa*: Em CadastroSecundarioServiceImpl, os métodos de busca com findByCodigo retornavam getFirst caso nao encontressem codigo solicitado (orElseGet()).
- *Solução*: Mudança de orElseGet para orElseThrow uso de exceção criada anteriormente.

### Ajuste 4:
- *Problema*: Códigos inexistentes retornariam resposta HTTP default não adequada.
- *Causa*: Não há exceção específica para código inexistente e não há ExceptionHandler.
- *Solução*: criação de CodigoNotFoundException e GlobalExcetionHandler.

### Ajuste 5:
- *Problema*: Métodos do layer service não garantem atomicidade.
- *Causa*: A atomicidadade deve ser garantida também nos serviços com a anotação @Transacional.
- *Solução*: Adicionado @Transactional nas classes de 'service'.

### Ajuste 6:
- *Problema*: POST de cadastro secundário aceita strings vazias.
- *Causa*: Não há validação para parâmentos fornecidos no POST de cadastro secundário em CadastroSecundarioRequest.
- *Solução*: Adicionado @Valid em controller e @NotBlank no DTO para não permitir adição sem nome e documento.

### Ajuste 7 (opcional):
- *Problema*: POST utiliza camelCase, mas não aceita snake-case (padrão JSON) para os nomes dos parâmetros.
- *Solução*: Não ajustado, pois o exemplo 'curl' fornecido pelo enunciado utiliza camelCase.

### Ajuste 8 (opcional):
- *Problema*: Arquivo copilot-instructions rastreia uso de copilot.
- *Solução*: Não ajustado, pois o arquivo e utilizado para a atividade em questão.

# 3. Testes
Foram adicionados os testes pertinentes.
Os prints de tela estão em [/Porto-main/docs/3-Testes.jpeg](https://github.com/lumbergeek7/Porto-main/tree/fix-porto/docs/3-Testes.jpeg).

### Testes unitários
- Adicionados testes unitários para service e controller (100% coverage para esses casos).

### Teste integrado
- Adicionado teste integrado para o cadastro secundário (considerado caso mais crítico).

# 4. Evidências de execução e de testes

Todos os prints de tela estão organizados em [/Porto-main/docs](https://github.com/lumbergeek7/Porto-main/tree/fix-porto/docs).

# 5. Outros

As validações e correções se basearam nos comportamentos esperados e exemplos citados no enunciado da atividade.

Comportamentos esperados:

- 1-) `GET /api/cnaes` deve retornar todas as atividades cadastradas.
- 2-) `GET /api/cnaes/buscar?termo={texto}` deve buscar CNAEs que contenham o texto informado em qualquer parte da descricao.
- 3-) `GET /api/cnaes/codigo?codigo={codigo}` deve retornar o CNAE do codigo informado.
- 4-) Códigos CNAE inexistentes devem retornar uma resposta HTTP adequada para recurso nao encontrado.
- 5-) `POST /api/cadastros-secundarios` deve criar um cadastro vinculado a um CNAE existente.
- 6-) `GET /api/cadastros-secundarios/validar-cnae?codigoCnae={codigo}` deve validar se o CNAE informado pode ser usado no cadastro.
- 7-) Cadastros secundários com CNAE inexistente nao devem ser criados.

Exemplos:

```bash
curl http://localhost:8080/api/cnaes
curl "http://localhost:8080/api/cnaes/buscar?termo=programas"
curl "http://localhost:8080/api/cnaes/codigo?codigo=6201-5/01"
curl -X POST http://localhost:8080/api/cadastros-secundarios \
  -H "Content-Type: application/json" \
  -d '{"nomeFantasia":"Tech Porto","documento":"12345678000199","codigoCnae":"6201-5/01"}'
```
