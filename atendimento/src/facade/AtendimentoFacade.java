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
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Classe responsável por reunir as operações do sistema.
 * Implementa o padrão estrutural Facade e utiliza Streams, Lambdas e Interfaces Funcionais.
 *
 * @author Alice
 */
public class AtendimentoFacade {

    private final PacienteDAO pacienteDAO;
    private final MedicamentoDAO medicamentoDAO;
    private final ServicoDAO servicoDAO;
    private final AcompanhamentoDAO acompanhamentoDAO;

    public AtendimentoFacade() {
        pacienteDAO = new PacienteDAO();
        medicamentoDAO = new MedicamentoDAO();
        servicoDAO = new ServicoDAO();
        acompanhamentoDAO = new AcompanhamentoDAO();
    }

    public void cadastrarPaciente(Paciente paciente) {
        pacienteDAO.inserir(paciente);
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
        return medicamentoDAO.registrarMedicamento(cpf, medicamento);
    }

    public boolean atualizarMedicamento(String cpf, String nomeAtual, String dosagemAtual,
                                        String novoNome, String novaDosagem, String novaPosologia) {
        return medicamentoDAO.atualizarMedicamento(cpf, nomeAtual, dosagemAtual, novoNome, novaDosagem, novaPosologia);
    }

    public boolean cadastrarServico(String cpf, Servico servico) {
        return servicoDAO.registrarServico(cpf, servico);
    }

    public boolean cadastrarAcompanhamento(String cpf, Acompanhamento acompanhamento) {
        return acompanhamentoDAO.registrarAcompanhamento(cpf, acompanhamento);
    }

    public PacienteDTO buscarPacienteDTO(String cpf) {
        Paciente paciente = pacienteDAO.buscar(cpf);
        if (paciente != null) {
            return new PacienteDTO(
                    paciente.getCpf(),
                    paciente.getNome(),
                    paciente.getDataNascimento(),
                    paciente.getTelefone()
            );
        }
        return null;
    }

    /**
     * Filtra pacientes em memória utilizando Interface Funcional (Predicate), Lambda, Stream e DTO.
     */
    public List<PacienteDTO> filtrarPacientesDTO(Predicate<Paciente> criterio) {
        return pacienteDAO.listarTodos().stream()
                .filter(criterio)
                .map(p -> new PacienteDTO(
                        p.getCpf(),
                        p.getNome(),
                        p.getDataNascimento(),
                        p.getTelefone()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Retorna acompanhamentos de um paciente convertidos para DTO usando Stream e Lambda.
     */
    public List<AcompanhamentoDTO> listarAcompanhamentosDTO(String cpf) {
        return acompanhamentoDAO.listarAcompanhamentos(cpf).stream()
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
