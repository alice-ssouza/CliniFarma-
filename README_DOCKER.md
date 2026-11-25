# Projeto Atendimento - POO / Persistência com Docker

## O que foi corrigido e adicionado:
1. **Docker & Banco de Dados (`Dockerfile`, `docker-compose.yml`, `init.sql`)**:
   - Criação automática das 5 tabelas exatas usadas pelos DAOs (`paciente`, `contato_emergencia`, `medicamento`, `servico`, `acompanhamento`).
   - Correção do caminho absoluto do driver `postgresql-42.7.14.jar` no `project.properties` para caminho relativo (`lib/postgresql-42.7.14.jar`), permitindo compilar em qualquer máquina ou container.
   - `ConexaoBD.java` atualizado com padrão **Singleton** e leitura de variáveis de ambiente do Docker (mantendo fallback para execução local no NetBeans).
2. **Critérios de POO**:
   - **Persistência**: DAOs com `OperacoesDAO<T>` (Generics), transações (`setAutoCommit(false)` / `commit()` / `rollback()`), `PacienteDTO` e `AcompanhamentoDTO`.
   - **Design Patterns**: Singleton (`ConexaoBD`), Factory (`PacienteFactory`), Facade (`AtendimentoFacade`), Strategy (`EstrategiaExibicaoPaciente`).
   - **Recursos Java**: Classe interna (`Paciente.ContatoEmergencia`), Wrapper Classes (`Integer`, `Long`), Interfaces Funcionais (`Predicate`, `Function`), Lambdas e Streams (opção 13 no menu).

## Como rodar:
```bash
docker compose up -d --build
docker attach app_atendimento
```

## Revisão (correções aplicadas)
1. **Cadastro com CPF duplicado**: `OperacoesDAO.inserir` agora retorna `boolean`. Se o CPF já existe, nada mais é gravado e o sistema informa o erro.
2. **Singleton de verdade**: `ConexaoBD` mantém uma única conexão; os DAOs só fecham `PreparedStatement`/`ResultSet`. A conexão é fechada uma vez, ao sair do sistema (`ConexaoBD.fechar()`). As transações restauram o `autoCommit` no `finally`.
3. **Todos os DAOs implementam `OperacoesDAO<T>`** (`PacienteDAO`, `MedicamentoDAO`, `ServicoDAO`, `AcompanhamentoDAO`). `Medicamento`, `Servico` e `Acompanhamento` ganharam `id`; atualizar e excluir usam o id, não mais (cpf, data).
4. **Injeção de dependência pelo construtor**: `PacienteDAO` recebe os outros DAOs e `AtendimentoFacade` recebe os quatro DAOs. A montagem fica em `Main.criarFacade()`.
5. **Validação nos setters**: os construtores de `Paciente`, `Medicamento`, `Servico`, `Acompanhamento` e `ContatoEmergencia` usam os setters, que lançam `IllegalArgumentException` (limites iguais aos do `init.sql`). O `Main` captura e informa "Dado invalido".
6. **Main usa só o Facade**. Novas opções: 14 (contagem de medicamentos por dosagem, `Long`) e 15 (acompanhamentos resumidos, Stream + DTO). A opção 13 agora filtra por nome **ou** alergia.

7. **Mapeamento**: removida a lista `alergiasMedicamentos` de `Paciente` (não era usada nem persistida; a alergia fica na coluna `alergia`). A tabela `medicamento` ganhou `UNIQUE (paciente_cpf, nome, dosagem)` e o `MedicamentoDAO` avisa quando o medicamento já existe.

Obs.: se houver um PostgreSQL local na porta 5432, troque o mapeamento no `docker-compose.yml` para `5433:5432`. O `init.sql` só roda na primeira criação do volume: para receber a restrição `UNIQUE`, rode `docker compose down -v` (isso apaga os dados do banco) e suba de novo.
