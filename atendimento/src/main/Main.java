
package main;

import DAO.PacienteDAO;
import DAO.MedicamentoDAO;
import DAO.ServicoDAO;
import DAO.AcompanhamentoDAO;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import model.Paciente;
import model.Medicamento;
import model.Servico;
import model.Acompanhamento;
import model.TipoServico;
import factory.PacienteFactory;
import facade.AtendimentoFacade;
import strategy.EstrategiaExibicaoPaciente;
import strategy.ExibicaoCompleta;
import strategy.ExibicaoResumida;
import DTO.PacienteDTO;
import DTO.AcompanhamentoDTO;
import database.ConexaoBD;

/**
 * Classe principal do sistema de atendimento.
 * Permite gerenciar pacientes, medicamentos, serviços e acompanhamentos.
 *
 * @author Alice
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    // Objetos responsáveis pelas operações no banco de dados
    // A Main só conversa com o Facade; os DAOs são montados em criarFacade()
    private static final AtendimentoFacade atendimentoFacade = criarFacade();

    /**
     * Raiz de composição: cria os DAOs e os injeta pelos construtores.
     */
    private static AtendimentoFacade criarFacade() {
        MedicamentoDAO medicamentoDAO = new MedicamentoDAO();
        ServicoDAO servicoDAO = new ServicoDAO();
        AcompanhamentoDAO acompanhamentoDAO = new AcompanhamentoDAO();
        PacienteDAO pacienteDAO =
                new PacienteDAO(medicamentoDAO, servicoDAO, acompanhamentoDAO);

        return new AtendimentoFacade(
                pacienteDAO, medicamentoDAO, servicoDAO, acompanhamentoDAO);
    }

    /**
     * Método principal que inicia o sistema.
     */
    public static void main(String[] args) {

        int opcao = -1;

        do {
            System.out.println("______________________________________");
            System.out.println("               MENU                   ");
            System.out.println("______________________________________");
            System.out.println("0. Sair");
            System.out.println("1. Cadastrar paciente");
            System.out.println("2. Listar pacientes");
            System.out.println("3. Buscar paciente por CPF");
            System.out.println("4. Registrar servico");
            System.out.println("5. Visualizar historico de servicos");
            System.out.println("6. Registrar acompanhamento clinico");
            System.out.println("7. Excluir paciente");
            System.out.println("8. Visualizar acompanhamentos");
            System.out.println("9. Editar paciente");
            System.out.println("10. Cadastrar medicamento");
            System.out.println("11. Editar medicamento");
            System.out.println("12. Buscar paciente usando DTO");
            System.out.println("13. Filtrar pacientes por nome/alergia (Stream + Lambda + DTO)");
            System.out.println("14. Contar medicamentos por dosagem (Wrapper Long)");
            System.out.println("15. Acompanhamentos resumidos (Stream + DTO)");
            System.out.println("______________________________________");

            try {
                System.out.print("Escolha uma opcao: ");
                opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1 -> cadastrarPaciente();
                    case 2 -> listarPacientes();
                    case 3 -> buscarPacientePorCpf();
                    case 4 -> registrarServico();
                    case 5 -> visualizarHistoricoServicos();
                    case 6 -> registrarAcompanhamento();
                    case 7 -> excluirPaciente();
                    case 8 -> visualizarAcompanhamento();
                    case 9 -> editarPaciente();
                    case 10 -> cadastrarMedicamento();
                    case 11 -> editarMedicamento();
                    case 12 -> buscarPacienteDTO();
                    case 13 -> filtrarPacientesStream();
                    case 14 -> contarMedicamentosPorDosagem();
                    case 15 -> listarAcompanhamentosResumidos();
                    case 0 -> System.out.println("Saindo...");
                    default -> System.out.println("Opcao invalida!");
                }

            } catch (InputMismatchException e) {
                System.out.println("Por favor, insira um numero.");
                scanner.nextLine();
            } catch (IllegalArgumentException e) {
                // Validações dos setters das classes de modelo
                System.out.println("Dado invalido: " + e.getMessage());
            }

        } while (opcao != 0);

        // Fecha a conexão única do Singleton ao encerrar o sistema
        ConexaoBD.fechar();
    }

    /**
     * Cadastra um paciente e seu medicamento inicial.
     */
    private static void cadastrarPaciente() {

        System.out.println("\n-- Cadastro de Paciente --");

        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("CPF: ");
        String cpf = scanner.nextLine();

        LocalDate nascimento = null;
        boolean dataValida = false;

        while (!dataValida) {
            System.out.print("Data de nascimento (AAAA-MM-DD): ");
            String inputData = scanner.nextLine();

            try {
                nascimento = LocalDate.parse(inputData);
                dataValida = true;
            } catch (DateTimeParseException e) {
                System.out.println("Data invalida!");
            }
        }

        System.out.print("Endereco: ");
        String endereco = scanner.nextLine();

        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();

        System.out.print("Nome do contato de emergencia: ");
        String nomeContato = scanner.nextLine();

        System.out.print("Telefone do contato de emergencia: ");
        String telefoneContato = scanner.nextLine();

        System.out.print("Nome do medicamento: ");
        String nomeMedicamento = scanner.nextLine();

        System.out.print("Dosagem: ");
        String dosagem = scanner.nextLine();

        System.out.print("Posologia: ");
        String posologia = scanner.nextLine();

        System.out.print("Fabricante: ");
        String fabricante = scanner.nextLine();

        System.out.print("O paciente tem alergia a algum medicamento? (S/N): ");
        String respostaAlergia = scanner.nextLine().trim().toUpperCase();

        String alergia = null;

        if (respostaAlergia.equals("S")) {
            System.out.print("Informe o nome do medicamento que causa alergia: ");
            alergia = scanner.nextLine();
        }

        PacienteFactory pacienteFactory = new PacienteFactory();

        // Os objetos são criados (e validados) ANTES de qualquer gravação no banco
        Paciente paciente = pacienteFactory.criarPaciente(
                cpf, nome, endereco, telefone, alergia, nascimento
        );

        Paciente.ContatoEmergencia contato =
                paciente.new ContatoEmergencia(nomeContato, telefoneContato);

        paciente.setContatoEmergencia(contato);
        paciente.setAlergia(alergia);

        Medicamento medicamento = new Medicamento(
                nomeMedicamento, dosagem, posologia, fabricante
        );

        // Primeiro cadastra o paciente; se o CPF já existir, nada mais é gravado
        if (!atendimentoFacade.cadastrarPaciente(paciente)) {
            System.out.println("Nao foi possivel cadastrar o paciente.");
            return;
        }

        // Depois cadastra o medicamento vinculado ao paciente
        boolean cadastrado =
                atendimentoFacade.cadastrarMedicamento(paciente.getCpf(), medicamento);

        if (cadastrado) {
            System.out.println("Paciente e medicamento cadastrados com sucesso!");
        } else {
            System.out.println("Paciente cadastrado, mas houve erro ao cadastrar o medicamento.");
        }
    }

    /**
     * Lista todos os pacientes cadastrados.
     */
    private static void listarPacientes() {

        System.out.println("\n-- Lista de Pacientes --");

        List<Paciente> pacientes = atendimentoFacade.listarTodosPacientes();

        if (pacientes.isEmpty()) {
            System.out.println("Nenhum paciente cadastrado.");
            return;
        }

        for (Paciente p : pacientes) {

            System.out.println("\n========================================");
            System.out.println("DADOS DO PACIENTE");
            System.out.println("========================================");

            System.out.println("Nome: " + p.getNome());
            System.out.println("CPF: " + p.getCpf());
            System.out.println("Nascimento: " + p.getDataNascimento());
            System.out.println("Endereco: " + p.getEndereco());
            System.out.println("Telefone: " + p.getTelefone());
            System.out.println("Alergia: "
                    + (p.getAlergia() == null ? "Nenhuma" : p.getAlergia()));

            System.out.println("\nMEDICAMENTOS:");

            List<Medicamento> medicamentos = p.getMedicamentos();

            if (medicamentos.isEmpty()) {
                System.out.println("  Nenhum medicamento cadastrado.");
            } else {
                for (Medicamento m : medicamentos) {
                    System.out.println("  - " + m.getNome()
                            + " (" + m.getDosagem()
                            + ", " + m.getPosologia() + ")");
                }
            }

            System.out.println("\nHISTORICO DE SERVICOS:");

            List<Servico> servicos = p.getHistoricoServicos();

            if (servicos.isEmpty()) {
                System.out.println("  Nenhum servico cadastrado.");
            } else {
                for (Servico s : servicos) {
                    System.out.println("  - " + s);
                }
            }

            System.out.println("\nACOMPANHAMENTOS CLINICOS:");

            List<Acompanhamento> acompanhamentos =
                    p.getHistoricoAcompanhamentos();

            if (acompanhamentos.isEmpty()) {
                System.out.println("  Nenhum acompanhamento cadastrado.");
            } else {
                for (Acompanhamento a : acompanhamentos) {

                    System.out.println("  Data: " + a.getData());
                    System.out.println("  Notas clinicas: "
                            + a.getNotasClinicas());
                    System.out.println("  Resultado: " + a.getResultado());

                    System.out.println("  Medicamento: "
                            + (a.getMedicamento() == null
                                    ? "Nenhum"
                                    : a.getMedicamento().getNome()));

                    System.out.println("  ------------------------------");
                }
            }

            System.out.println("========================================");
        }
    }

    /**
     * Busca um paciente pelo CPF.
     */
    private static void buscarPacientePorCpf() {

        System.out.print("\nDigite o CPF do paciente: ");
        String cpf = scanner.nextLine();

        Paciente paciente = atendimentoFacade.buscarPaciente(cpf);

        if (paciente != null) {

            System.out.println("\nComo deseja visualizar os dados?");
            System.out.println("1. Exibicao completa");
            System.out.println("2. Exibicao resumida");
            System.out.print("Escolha: ");

            int opcao;

            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opcao invalida!");
                return;
            }

            EstrategiaExibicaoPaciente estrategia;

            if (opcao == 1) {
                estrategia = new ExibicaoCompleta();
            } else if (opcao == 2) {
                estrategia = new ExibicaoResumida();
            } else {
                System.out.println("Opcao invalida!");
                return;
            }

            estrategia.exibir(paciente);

        } else {
            System.out.println("Paciente nao encontrado.");
        }
    }

    /**
     * Registra um serviço para um paciente.
     */
    private static void registrarServico() {

        System.out.print("\nDigite o CPF do paciente para registrar servico: ");
        String cpf = scanner.nextLine();

        Paciente paciente = atendimentoFacade.buscarPaciente(cpf);

        if (paciente == null) {
            System.out.println("Paciente nao encontrado.");
            return;
        }

        System.out.println("\nTipos de servico disponiveis:");

        TipoServico[] tipos = TipoServico.values();

        for (int i = 0; i < tipos.length; i++) {
            System.out.println((i + 1) + ". " + tipos[i]);
        }

        System.out.print("Escolha o numero do tipo de servico: ");

        int tipoEscolhido;

        try {
            tipoEscolhido = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida! Operacao cancelada.");
            return;
        }

        if (tipoEscolhido < 1 || tipoEscolhido > tipos.length) {
            System.out.println("Tipo de servico invalido.");
            return;
        }

        TipoServico tipo = tipos[tipoEscolhido - 1];

        System.out.print("Anotacao clinica: ");
        String anotacao = scanner.nextLine();

        System.out.print("Resultado: ");
        String resultado = scanner.nextLine();

        LocalDate data;

        while (true) {
            System.out.print("Data do servico (AAAA-MM-DD): ");
            String inputData = scanner.nextLine();

            try {
                data = LocalDate.parse(inputData);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Data invalida! Tente novamente.");
            }
        }

        System.out.println("\nResumo do servico a ser cadastrado:");
        System.out.println("Paciente: " + paciente.getNome());
        System.out.println("Tipo de servico: " + tipo);
        System.out.println("Anotacao clinica: " + anotacao);
        System.out.println("Resultado: " + resultado);
        System.out.println("Data do servico: " + data);

        System.out.print("Confirmar cadastro? (S/N): ");
        String confirmacao = scanner.nextLine().trim().toUpperCase();

        if (!confirmacao.equals("S")) {
            System.out.println("Cadastro de servico cancelado.");
            return;
        }

        Servico servico = new Servico(data, anotacao, resultado, tipo);

        boolean cadastrado = atendimentoFacade.cadastrarServico(cpf, servico);

        if (cadastrado) {
            System.out.println("Servico registrado com sucesso para o paciente "
                    + paciente.getNome());
        } else {
            System.out.println("Nao foi possivel registrar o servico.");
        }
    }

    /**
     * Exibe o histórico de serviços de um paciente.
     */
    private static void visualizarHistoricoServicos() {

        System.out.print("\nDigite o CPF do paciente para ver historico de servicos: ");
        String cpf = scanner.nextLine();

        Paciente paciente = atendimentoFacade.buscarPaciente(cpf);

        if (paciente == null) {
            System.out.println("Paciente nao encontrado.");
            return;
        }

        List<Servico> servicos = paciente.getHistoricoServicos();

        if (servicos.isEmpty()) {
            System.out.println("Nenhum servico registrado para o paciente "
                    + paciente.getNome());
            return;
        }

        System.out.println("\n-- Historico de Servicos do paciente "
                + paciente.getNome() + " --");

        for (Servico s : servicos) {
            System.out.println(s);
        }
    }

    /**
     * Registra um acompanhamento clínico para um paciente.
     */
    private static void registrarAcompanhamento() {

        System.out.print("\nDigite o CPF do paciente para registrar acompanhamento: ");
        String cpf = scanner.nextLine();

        Paciente paciente = atendimentoFacade.buscarPaciente(cpf);

        if (paciente == null) {
            System.out.println("Paciente nao encontrado.");
            return;
        }

        LocalDate data = LocalDate.now();

        System.out.print("Notas clinicas: ");
        String notasClinicas = scanner.nextLine();

        System.out.print("Resultado: ");
        String resultado = scanner.nextLine();

        System.out.print("Nome do medicamento (ou vazio para nenhum): ");
        String nomeMed = scanner.nextLine();

        Medicamento medicamento = null;

        if (!nomeMed.isBlank()) {

            System.out.print("Dosagem: ");
            String dosagem = scanner.nextLine();

            System.out.print("Posologia: ");
            String posologia = scanner.nextLine();

            System.out.print("Fabricante: ");
            String fabricante = scanner.nextLine();

            medicamento = new Medicamento(
                    nomeMed, dosagem, posologia, fabricante
            );
        }

        Acompanhamento acompanhamento = new Acompanhamento(
                data, notasClinicas, resultado, medicamento, paciente
        );

        boolean cadastrado =
                atendimentoFacade.cadastrarAcompanhamento(acompanhamento);

        if (cadastrado) {
            System.out.println("Acompanhamento registrado com sucesso!");
        } else {
            System.out.println("Nao foi possivel registrar o acompanhamento.");
        }
    }

    /**
     * Exibe os acompanhamentos de um paciente.
     */
    private static void visualizarAcompanhamento() {

        System.out.print("\nDigite o CPF do paciente que deseja ver os acompanhamentos: ");
        String cpf = scanner.nextLine();

        Paciente paciente = atendimentoFacade.buscarPaciente(cpf);

        if (paciente == null) {
            System.out.println("Paciente nao encontrado.");
            return;
        }

        List<Acompanhamento> acompanhamentos =
                paciente.getHistoricoAcompanhamentos();

        if (acompanhamentos.isEmpty()) {
            System.out.println("Nenhum acompanhamento registrado para o paciente "
                    + paciente.getNome() + ".");
            return;
        }

        System.out.println("\n-- Historico de Acompanhamentos do Paciente: "
                + paciente.getNome() + " --");

        System.out.println("CPF: " + paciente.getCpf());
        System.out.println("Data de Nascimento: " + paciente.getDataNascimento());
        System.out.println("Endereco: " + paciente.getEndereco());
        System.out.println("Telefone: " + paciente.getTelefone());
        System.out.println("-------------------------------------------------------------");

        for (Acompanhamento a : acompanhamentos) {
            System.out.println("Data: " + a.getData());
            System.out.println("Notas Clinicas: "
                    + (a.getNotasClinicas() == null
                            || a.getNotasClinicas().isEmpty()
                            ? "Nenhuma" : a.getNotasClinicas()));
            System.out.println("Resultado: " + a.getResultado());
            System.out.println("Medicamento: "
                    + (a.getMedicamento() == null
                            ? "Nenhum" : a.getMedicamento().getNome()));
            System.out.println("-------------------------------------------------------------");
        }
    }

    /**
     * Exclui um paciente e seus registros relacionados.
     */
    private static void excluirPaciente() {

        System.out.print("\nDigite o CPF do paciente que sera excluido: ");
        String cpf = scanner.nextLine();

        Paciente paciente = atendimentoFacade.buscarPaciente(cpf);

        if (paciente == null) {
            System.out.println("Paciente nao encontrado.");
            return;
        }

        boolean temVinculos =
                !paciente.getMedicamentos().isEmpty()
                || !paciente.getHistoricoServicos().isEmpty()
                || !paciente.getHistoricoAcompanhamentos().isEmpty();

        if (temVinculos) {
            System.out.println("AVISO! O paciente tem acompanhamentos, servicos ou medicamentos registrados.");
            System.out.print("Deseja realmente excluir o paciente? (S/N): ");

            String confirmacao = scanner.nextLine();

            if (!confirmacao.equalsIgnoreCase("S")) {
                System.out.println("Exclusao cancelada.");
                return;
            }
        }

        boolean excluido = atendimentoFacade.excluirPaciente(paciente);

        if (excluido) {
            System.out.println("Paciente " + paciente.getNome()
                    + " removido com sucesso.");
        } else {
            System.out.println("Nao foi possivel excluir o paciente.");
        }
    }

    /**
     * Edita os dados básicos de um paciente.
     */
    private static void editarPaciente() {

        System.out.print("\nDigite o CPF do paciente que sera editado: ");
        String cpf = scanner.nextLine();

        Paciente paciente = atendimentoFacade.buscarPaciente(cpf);

        if (paciente == null) {
            System.out.println("Paciente com CPF " + cpf + " nao encontrado.");
            return;
        }

        System.out.print("Deseja editar o nome? (S/N): ");
        String opcao = scanner.nextLine().trim().toUpperCase();

        if (opcao.equals("S")) {
            System.out.print("Digite o novo nome: ");
            paciente.setNome(scanner.nextLine());
        }

        System.out.print("Deseja editar o telefone? (S/N): ");
        opcao = scanner.nextLine().trim().toUpperCase();

        if (opcao.equals("S")) {
            System.out.print("Digite o novo telefone: ");
            paciente.setTelefone(scanner.nextLine());
        }

        System.out.print("Deseja editar o endereco? (S/N): ");
        opcao = scanner.nextLine().trim().toUpperCase();

        if (opcao.equals("S")) {
            System.out.print("Digite o novo endereco: ");
            paciente.setEndereco(scanner.nextLine());
        }

        System.out.print("Deseja editar a alergia? (S/N): ");
        opcao = scanner.nextLine().trim().toUpperCase();

        if (opcao.equals("S")) {
            System.out.print("Digite a nova alergia (ou deixe vazio se nao houver): ");
            String novaAlergia = scanner.nextLine().trim();

            paciente.setAlergia(novaAlergia.isEmpty() ? null : novaAlergia);
        }

        boolean atualizado = atendimentoFacade.atualizarPaciente(paciente);

        if (atualizado) {
            System.out.println("Paciente atualizado com sucesso!");
        } else {
            System.out.println("Nao foi possivel atualizar o paciente.");
        }
    }

    /**
     * Cadastra um medicamento para um paciente existente.
     */
    private static void cadastrarMedicamento() {

        System.out.print("Digite o CPF de quem vai usar o medicamento: ");
        String cpf = scanner.nextLine();

        Paciente paciente = atendimentoFacade.buscarPaciente(cpf);

        if (paciente == null) {
            System.out.println("Paciente nao encontrado.");
            return;
        }

        System.out.print("Nome do medicamento: ");
        String nomeMedicamento = scanner.nextLine();

        System.out.print("Dosagem: ");
        String dosagem = scanner.nextLine();

        System.out.print("Posologia: ");
        String posologia = scanner.nextLine();

        System.out.print("Fabricante: ");
        String fabricante = scanner.nextLine();

        Medicamento medicamento = new Medicamento(
                nomeMedicamento, dosagem, posologia, fabricante
        );

        boolean cadastrado = atendimentoFacade.cadastrarMedicamento(cpf, medicamento);

        if (cadastrado) {
            System.out.println("Medicamento cadastrado com sucesso!");
        } else {
            System.out.println("Nao foi possivel cadastrar o medicamento.");
        }
    }

    /**
     * Edita os dados de um medicamento de um paciente.
     */
    private static void editarMedicamento() {

        System.out.print("Digite o CPF do paciente: ");
        String cpf = scanner.nextLine();

        Paciente paciente = atendimentoFacade.buscarPaciente(cpf);

        if (paciente == null) {
            System.out.println("Paciente nao encontrado.");
            return;
        }

        List<Medicamento> medicamentos = paciente.getMedicamentos();

        Function<Medicamento, String> obterNome =
                medicamento -> medicamento.getNome();

        if (medicamentos.isEmpty()) {
            System.out.println("Esse paciente nao possui medicamentos cadastrados.");
            return;
        }

        System.out.println("Medicamentos encontrados:");

        medicamentos.stream().forEach(m -> {
            String nome = obterNome.apply(m);
            System.out.println("  - " + nome
                    + " (" + m.getDosagem()
                    + ", " + m.getPosologia() + ")");
        });

        System.out.print("Digite o nome do medicamento que sera editado: ");
        String nomeMedicamento = scanner.nextLine();

        Medicamento medicamentoParaEditar = null;

        for (Medicamento m : medicamentos) {
            if (m.getNome().equalsIgnoreCase(nomeMedicamento)) {
                medicamentoParaEditar = m;
                break;
            }
        }

        if (medicamentoParaEditar == null) {
            System.out.println("Medicamento nao encontrado.");
            return;
        }

        String nomeAtual = medicamentoParaEditar.getNome();
        String dosagemAtual = medicamentoParaEditar.getDosagem();

        System.out.print("Deseja editar o nome do medicamento? (S/N): ");
        String opcao = scanner.nextLine().trim().toUpperCase();

        String novoNome = nomeAtual;

        if (opcao.equals("S")) {
            System.out.print("Digite o novo nome: ");
            novoNome = scanner.nextLine();
        }

        System.out.print("Deseja editar a dosagem? (S/N): ");
        opcao = scanner.nextLine().trim().toUpperCase();

        String novaDosagem = dosagemAtual;

        if (opcao.equals("S")) {
            System.out.print("Digite a nova dosagem: ");
            novaDosagem = scanner.nextLine();
        }

        System.out.print("Deseja editar a posologia? (S/N): ");
        opcao = scanner.nextLine().trim().toUpperCase();

        String novaPosologia = medicamentoParaEditar.getPosologia();

        if (opcao.equals("S")) {
            System.out.print("Digite a nova posologia: ");
            novaPosologia = scanner.nextLine();
        }

        // Os setters validam os novos valores; o medicamento é localizado pelo id
        medicamentoParaEditar.setNome(novoNome);
        medicamentoParaEditar.setDosagem(novaDosagem);
        medicamentoParaEditar.setPosologia(novaPosologia);

        boolean atualizado = atendimentoFacade.atualizarMedicamento(medicamentoParaEditar);

        if (atualizado) {
            System.out.println("Medicamento atualizado com sucesso.");
        } else {
            System.out.println("Nao foi possivel atualizar o medicamento.");
        }
    }

    /**
     * Busca os dados básicos de um paciente utilizando DTO.
     */
    private static void buscarPacienteDTO() {

        System.out.print("\nDigite o CPF do paciente: ");
        String cpf = scanner.nextLine();

        PacienteDTO paciente = atendimentoFacade.buscarPacienteDTO(cpf);

        if (paciente != null) {

            System.out.println("\n--- Dados do Paciente (DTO) ---");
            System.out.println("CPF: " + paciente.getCpf());
            System.out.println("Nome: " + paciente.getNome());
            System.out.println("Nascimento: " + paciente.getDataNascimento());
            System.out.println("Telefone: " + paciente.getTelefone());

        } else {
            System.out.println("Paciente nao encontrado.");
        }
    }

    /**
     * Demonstra uso de Streams, Lambdas, Predicate e DTO via Facade.
     */
    private static void filtrarPacientesStream() {
        System.out.print("\nDigite um trecho do nome ou da alergia do paciente: ");
        String filtro = scanner.nextLine().trim().toLowerCase();

        List<PacienteDTO> resultados = atendimentoFacade.filtrarPacientesDTO(
                p -> (p.getNome() != null && p.getNome().toLowerCase().contains(filtro))
                  || (p.getAlergia() != null && p.getAlergia().toLowerCase().contains(filtro))
        );

        if (resultados.isEmpty()) {
            System.out.println("Nenhum paciente encontrado com esse filtro.");
        } else {
            System.out.println("\n--- Pacientes Filtrados (Stream + Lambda + DTO) ---");
            resultados.forEach(dto -> System.out.println(
                    "CPF: " + dto.getCpf() + " | Nome: " + dto.getNome()
                    + " | Nascimento: " + dto.getDataNascimento()
                    + " | Telefone: " + dto.getTelefone()
            ));
        }
    }

    /**
     * Conta quantos medicamentos de um paciente têm determinada dosagem (retorna Long).
     */
    private static void contarMedicamentosPorDosagem() {
        System.out.print("\nDigite o CPF do paciente: ");
        String cpf = scanner.nextLine();

        System.out.print("Digite a dosagem (ou parte dela): ");
        String dosagem = scanner.nextLine();

        Long total = atendimentoFacade.contarMedicamentosComDosagem(cpf, dosagem);

        System.out.println("Medicamentos com essa dosagem: " + total);
    }

    /**
     * Exibe os acompanhamentos de um paciente em formato resumido (DTO via Stream).
     */
    private static void listarAcompanhamentosResumidos() {
        System.out.print("\nDigite o CPF do paciente: ");
        String cpf = scanner.nextLine();

        List<AcompanhamentoDTO> acompanhamentos =
                atendimentoFacade.listarAcompanhamentosDTO(cpf);

        if (acompanhamentos.isEmpty()) {
            System.out.println("Nenhum acompanhamento encontrado para esse paciente.");
            return;
        }

        acompanhamentos.forEach(a -> System.out.println(
                a.getData() + " | " + a.getNomePaciente()
                + " | Resultado: " + a.getResultado()
                + " | Medicamento: " + a.getMedicamentoUtilizado()
        ));
    }
}
