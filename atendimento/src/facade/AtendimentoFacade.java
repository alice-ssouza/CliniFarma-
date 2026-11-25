package facade;

import DAO.PacienteDAO;
import DAO.MedicamentoDAO;
import DAO.ServicoDAO;
import DAO.AcompanhamentoDAO;
import model.Paciente;
import model.Medicamento;
import model.Servico;
import model.Acompanhamento;
import DTO.PacienteDTO;
import DTO.AcompanhamentoDTO;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Classe responsável por reunir as operações do sistema.
 * Implementa o padrão estrutural Facade e utiliza Streams, Lambdas e Interfaces Funcionais.
 *
 * <p>Os DAOs são recebidos pelo construtor (injeção de dependência); quem monta
 * o sistema (a classe Main) decide quais implementações entregar.</p>
 *
 * @author Alice
 */
public class AtendimentoFacade {

    private final PacienteDAO pacienteDAO;
    private final MedicamentoDAO medicamentoDAO;
    private final ServicoDAO servicoDAO;
    private final AcompanhamentoDAO acompanhamentoDAO;

    public AtendimentoFacade(PacienteDAO pacienteDAO, MedicamentoDAO medicamentoDAO,
                             ServicoDAO servicoDAO, AcompanhamentoDAO acompanhamentoDAO) {
        this.pacienteDAO = Objects.requireNonNull(pacienteDAO);
        this.medicamentoDAO = Objects.requireNonNull(medicamentoDAO);
        this.servicoDAO = Objects.requireNonNull(servicoDAO);
        this.acompanhamentoDAO = Objects.requireNonNull(acompanhamentoDAO);
    }

    public boolean cadastrarPaciente(Paciente paciente) {
        return pacienteDAO.inserir(paciente);
    }

    public Paciente buscarPaciente(String cpf) {
        return pacienteDAO.buscar(cpf);
    }

    public List<Paciente> listarTodosPacientes() {
        return pacienteDAO.listarTodos();
    }

    public boolean atualizarPaciente(Paciente paciente) {
        return pacienteDAO.atualizar(paciente);
    }

    public boolean excluirPaciente(Paciente paciente) {
        return pacienteDAO.excluir(paciente);
    }

    public boolean cadastrarMedicamento(String cpf, Medicamento medicamento) {
        medicamento.setCpfPaciente(cpf);
        return medicamentoDAO.inserir(medicamento);
    }

    public boolean atualizarMedicamento(Medicamento medicamento) {
        return medicamentoDAO.atualizar(medicamento);
    }

    public boolean cadastrarServico(String cpf, Servico servico) {
        servico.setCpfPaciente(cpf);
        return servicoDAO.inserir(servico);
    }

    public boolean cadastrarAcompanhamento(Acompanhamento acompanhamento) {
        return acompanhamentoDAO.inserir(acompanhamento);
    }

    /**
     * Converte um Paciente em DTO (usado como referência de método nos streams).
     */
    private PacienteDTO paraDTO(Paciente paciente) {
        return new PacienteDTO(
                paciente.getCpf(),
                paciente.getNome(),
                paciente.getDataNascimento(),
                paciente.getTelefone()
        );
    }

    public PacienteDTO buscarPacienteDTO(String cpf) {
        Paciente paciente = pacienteDAO.buscar(cpf);
        return paciente != null ? paraDTO(paciente) : null;
    }

    /**
     * Filtra pacientes em memória utilizando Interface Funcional (Predicate), Lambda, Stream e DTO.
     */
    public List<PacienteDTO> filtrarPacientesDTO(Predicate<Paciente> criterio) {
        return pacienteDAO.listarTodos().stream()
                .filter(criterio)
                .map(this::paraDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retorna acompanhamentos de um paciente convertidos para DTO usando Stream e Lambda.
     */
    public List<AcompanhamentoDTO> listarAcompanhamentosDTO(String cpf) {
        Paciente paciente = pacienteDAO.buscar(cpf);
        if (paciente == null) {
            return List.of();
        }
        return paciente.getHistoricoAcompanhamentos().stream()
                .map(a -> new AcompanhamentoDTO(
                        a.getPaciente().getNome(),
                        a.getPaciente().getCpf(),
                        a.getData(),
                        a.getResultado(),
                        a.getMedicamento() != null ? a.getMedicamento().getNome() : "Nenhum"
                ))
                .collect(Collectors.toList());
    }

    /**
     * Conta total de medicamentos registrados de um paciente retornando Wrapper Class (Long).
     */
    public Long contarMedicamentosComDosagem(String cpf, String termoDosagem) {
        Paciente paciente = pacienteDAO.buscar(cpf);
        if (paciente == null) {
            return 0L;
        }
        return paciente.getMedicamentos().stream()
                .filter(m -> m.getDosagem() != null && m.getDosagem().toLowerCase().contains(termoDosagem.toLowerCase()))
                .count();
    }
}
