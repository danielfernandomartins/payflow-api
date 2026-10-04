# Playbook de Suporte — PayFlow API

Este documento mostra como eu investigaria ocorrências comuns em um sistema transacional.

## Caso 1 — Transferência recusada

**Sintoma:** usuário informa que a transferência não foi concluída.

### Hipóteses
- Saldo insuficiente
- Perfil sem permissão para transferir
- Requisição inválida
- Serviço autorizador indisponível
- Falha de persistência

### Sequência de investigação
1. Reproduzir o cenário com os mesmos dados.
2. Validar payload e identificadores.
3. Conferir saldo e tipo do usuário.
4. Verificar resposta do autorizador.
5. Confirmar retorno HTTP da API.
6. Validar se houve gravação no banco.
7. Registrar evidências e causa encontrada.

### Classificação
- **Regra de negócio:** comportamento esperado do sistema.
- **Erro de entrada:** dado inválido ou incompleto.
- **Dependência externa:** falha fora da aplicação.
- **Defeito:** comportamento diferente do esperado.

---

## Caso 2 — Saldo divergente

**Sintoma:** saldo apresentado não corresponde às movimentações esperadas.

### Investigação
1. Consultar carteira.
2. Conferir histórico de transações.
3. Comparar débitos e créditos.
4. Identificar transações duplicadas ou ausentes.
5. Verificar status das operações.
6. Validar persistência e integridade dos registros.

---

## Caso 3 — API indisponível

### Investigação
1. Verificar se a aplicação iniciou corretamente.
2. Testar endpoint de forma isolada.
3. Conferir conexão com banco.
4. Verificar dependências externas.
5. Analisar logs de inicialização e erro.
6. Separar falha da aplicação de falha de infraestrutura.

## Como eu explicaria em entrevista

> "Eu uso o PayFlow para demonstrar que sei investigar um incidente seguindo o fluxo do sistema. Primeiro identifico o sintoma, depois valido entrada, regra, dependências e persistência. Meu objetivo é isolar a causa antes de propor uma correção ou escalar o chamado."
