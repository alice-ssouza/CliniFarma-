package DAO;

import database.ConexaoBD;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.Paciente;

/**
 * Classe responsável pelas operações de persistência
 * dos pacientes no banco de dados PostgreSQL.
 *
 * <p>Recebe os demais DAOs pelo construtor (injeção de dependência) para
 * carregar os relacionamentos do paciente (medicamentos, serviços e
 * acompanhamentos). Não fecha a conexão compartilhada de {@link ConexaoBD}.</p>
 *
 * @author Alice
 */
public class PacienteDAO implements OperacoesDAO<Paciente> {

    private final MedicamentoDAO medicamentoDAO;
    private final ServicoDAO servicoDAO;
    private final AcompanhamentoDAO acompanhamentoDAO;

    public PacienteDAO(MedicamentoDAO medicamentoDAO, ServicoDAO servicoDAO,
            AcompanhamentoDAO acompanhamentoDAO) {
        this.medicamentoDAO = Objects.requireNonNull(medicamentoDAO);
        this.servicoDAO = Objects.requireNonNull(servicoDAO);
        this.acompanhamentoDAO = Objects.requireNonNull(acompanhamentoDAO);
    }

    /**
     * Converte a linha atual do ResultSet em um Paciente (sem relacionamentos).
     */
    private Paciente mapear(ResultSet rs) throws SQLException {
        Paciente paciente = new Paciente(
                rs.getString("nome"),
                rs.getString("cpf"),
                rs.getDate("data_nascimento").toLocalDate(),
                rs.getString("endereco"),
                rs.getString("telefone")
        );
        paciente.setAlergia(rs.getString("alergia"));
        return paciente;
    }

    /**
     * Salva, atualiza ou exclui o contato de emergência.
     */
    private void salvarContato(Paciente paciente, Connection con)
            throws SQLException {

        if (paciente.getContatoEmergencia() == null) {

            String sql = "DELETE FROM contato_emergencia "
                    + "WHERE paciente_cpf = ?";

            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, paciente.getCpf());
                stmt.executeUpdate();
            }

            return;
        }

        String sql = "INSERT INTO contato_emergencia "
                + "(paciente_cpf, nome, telefone) VALUES (?, ?, ?) "
                + "ON CONFLICT (paciente_cpf) DO UPDATE SET "
                + "nome = EXCLUDED.nome, telefone = EXCLUDED.telefone";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, paciente.getCpf());
            stmt.setString(2, paciente.getContatoEmergencia().getNome());
            stmt.setString(3, paciente.getContatoEmergencia().getTelefone());

            stmt.executeUpdate();
        }
    }

    /**
     * Carrega o contato de emergência do paciente.
     */
    private void carregarContato(Paciente paciente) {

        String sql = "SELECT nome, telefone FROM contato_emergencia "
                + "WHERE paciente_cpf = ?";

        try (PreparedStatement stmt = ConexaoBD.conectar().prepareStatement(sql)) {

            stmt.setString(1, paciente.getCpf());

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    paciente.setContatoEmergencia(
                            paciente.new ContatoEmergencia(
                                    rs.getString("nome"),
                                    rs.getString("telefone")));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar contato de emergencia: "
                    + e.getMessage());
        }
    }

    /**
     * Carrega os medicamentos, serviços e acompanhamentos do paciente
     * usando os DAOs injetados.
     */
    private void carregarRelacionamentos(Paciente paciente) {
        String cpf = paciente.getCpf();

        medicamentoDAO.listarMedicamentos(cpf)
                .forEach(paciente::adicionarMedicamento);

        servicoDAO.listarServicos(cpf)
                .forEach(paciente::adicionarServico);

        acompanhamentoDAO.listarAcompanhamentos(cpf, paciente)
                .forEach(paciente::adicionarAcompanhamento);
    }

    private void carregarDetalhes(Paciente paciente) {
        carregarContato(paciente);
        carregarRelacionamentos(paciente);
    }

    private static void desfazer(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException ignorada) {
                // nada a fazer: a transação já foi perdida
            }
        }
    }

    private static void restaurarAutoCommit(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ignorada) {
                // conexão possivelmente fechada; será recriada pelo Singleton
            }
        }
    }

    /**
     * Insere um paciente (e seu contato de emergência) em uma transação.
     *
     * @return true se inseriu; false se houve erro (ex.: CPF já cadastrado)
     */
    @Override
    public boolean inserir(Paciente paciente) {

        String sql = "INSERT INTO paciente "
                + "(cpf, nome, data_nascimento, endereco, telefone, alergia) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        Connection con = null;

        try {
            con = ConexaoBD.conectar();
            con.setAutoCommit(false);

            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, paciente.getCpf());
                stmt.setString(2, paciente.getNome());
                stmt.setDate(3, Date.valueOf(paciente.getDataNascimento()));
                stmt.setString(4, paciente.getEndereco());
                stmt.setString(5, paciente.getTelefone());
                stmt.setString(6, paciente.getAlergia());
                stmt.executeUpdate();
            }

            salvarContato(paciente, con);
            con.commit();
            return true;

        } catch (SQLException e) {
            desfazer(con);
            if ("23505".equals(e.getSQLState())) {
                System.out.println("Ja existe um paciente cadastrado com esse CPF.");
            } else {
                System.out.println("Erro ao cadastrar paciente: " + e.getMessage());
            }
            return false;

        } finally {
            restaurarAutoCommit(con);
        }
    }

    /**
     * Lista todos os pacientes cadastrados.
     */
    public List<Paciente> listarTodos() {

        List<Paciente> pacientes = new ArrayList<>();

        String sql = "SELECT * FROM paciente ORDER BY nome";

        // Primeiro lê todos os pacientes e fecha o ResultSet...
        try (PreparedStatement stmt = ConexaoBD.conectar().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                pacientes.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar pacientes: " + e.getMessage());
        }

        // ...e só depois carrega os relacionamentos de cada um.
        pacientes.forEach(this::carregarDetalhes);

        return pacientes;
    }

    /**
     * Busca um paciente pelo CPF.
     */
    @Override
    public Paciente buscar(Object cpf) {

        if (cpf == null) {
            return null;
        }

        String sql = "SELECT * FROM paciente WHERE cpf = ?";
        Paciente paciente = null;

        try (PreparedStatement stmt = ConexaoBD.conectar().prepareStatement(sql)) {

            stmt.setString(1, cpf.toString().trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    paciente = mapear(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar paciente: " + e.getMessage());
        }

        if (paciente != null) {
            carregarDetalhes(paciente);
        }

        return paciente;
    }

    /**
     * Atualiza os dados de um paciente e seu contato em uma transação.
     */
    @Override
    public boolean atualizar(Paciente paciente) {

        String sql = "UPDATE paciente SET nome = ?, "
                + "data_nascimento = ?, endereco = ?, "
                + "telefone = ?, alergia = ? WHERE cpf = ?";

        Connection con = null;

        try {
            con = ConexaoBD.conectar();
            con.setAutoCommit(false);

            boolean atualizado;

            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, paciente.getNome());
                stmt.setDate(2, Date.valueOf(paciente.getDataNascimento()));
                stmt.setString(3, paciente.getEndereco());
                stmt.setString(4, paciente.getTelefone());
                stmt.setString(5, paciente.getAlergia());
                stmt.setString(6, paciente.getCpf());
                atualizado = stmt.executeUpdate() > 0;
            }

            if (!atualizado) {
                con.rollback();
                return false;
            }

            salvarContato(paciente, con);
            con.commit();
            return true;

        } catch (SQLException e) {
            desfazer(con);
            System.out.println("Erro ao atualizar paciente: " + e.getMessage());
            return false;

        } finally {
            restaurarAutoCommit(con);
        }
    }

    /**
     * Exclui um paciente pelo CPF (os registros filhos saem por ON DELETE CASCADE).
     */
    @Override
    public boolean excluir(Paciente paciente) {

        String sql = "DELETE FROM paciente WHERE cpf = ?";

        try (PreparedStatement stmt = ConexaoBD.conectar().prepareStatement(sql)) {

            stmt.setString(1, paciente.getCpf());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir paciente: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se um paciente está cadastrado.
     */
    @Override
    public boolean pesquisar(Paciente paciente) {
        return buscar(paciente.getCpf()) != null;
    }
}
