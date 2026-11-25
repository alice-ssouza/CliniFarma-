
package DAO;

import database.ConexaoBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import model.Paciente;
import model.Medicamento;
import model.Servico;
import model.Acompanhamento;

/**
 * Classe responsável pelas operações de persistência
 * dos pacientes no banco de dados PostgreSQL.
 *
 * @author Alice
 */
public class PacienteDAO implements OperacoesDAO<Paciente> {
    // Implementa Repository<Paciente, Object> por meio do contrato OperacoesDAO.

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

        try (Connection con = ConexaoBD.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, paciente.getCpf());

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    Paciente.ContatoEmergencia contato =
                            paciente.new ContatoEmergencia(
                                    rs.getString("nome"),
                                    rs.getString("telefone")
                            );

                    paciente.setContatoEmergencia(contato);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar contato de emergencia: "
                    + e.getMessage());
        }
    }

    /**
     * Carrega os medicamentos, serviços e acompanhamentos do paciente.
     */
    private void carregarRelacionamentos(Paciente paciente) {

        MedicamentoDAO medicamentoDAO = new MedicamentoDAO();
        ServicoDAO servicoDAO = new ServicoDAO();
        AcompanhamentoDAO acompanhamentoDAO = new AcompanhamentoDAO();

        List<Medicamento> medicamentos =
                medicamentoDAO.listarMedicamentos(paciente.getCpf());

        for (Medicamento medicamento : medicamentos) {
            paciente.adicionarMedicamento(medicamento);
        }

        List<Servico> servicos =
                servicoDAO.listarServicos(paciente.getCpf());

        for (Servico servico : servicos) {
            paciente.adicionarServico(servico);
        }

        List<Acompanhamento> acompanhamentos =
                acompanhamentoDAO.listarAcompanhamentos(
                        paciente.getCpf(), paciente);

        for (Acompanhamento acompanhamento : acompanhamentos) {
            paciente.adicionarAcompanhamento(acompanhamento);
        }
    }

    /**
     * Insere um paciente no banco de dados.
     */
    @Override
    public void inserir(Paciente paciente) {

        String sql = "INSERT INTO paciente "
                + "(cpf, nome, data_nascimento, endereco, telefone, alergia) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexaoBD.conectar()) {

            con.setAutoCommit(false);

            try (PreparedStatement stmt = con.prepareStatement(sql)) {

                stmt.setString(1, paciente.getCpf());
                stmt.setString(2, paciente.getNome());
                stmt.setDate(3, Date.valueOf(paciente.getDataNascimento()));
                stmt.setString(4, paciente.getEndereco());
                stmt.setString(5, paciente.getTelefone());
                stmt.setString(6, paciente.getAlergia());

                stmt.executeUpdate();

                salvarContato(paciente, con);

                con.commit();

                System.out.println("Paciente cadastrado com sucesso!");

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar paciente: "
                    + e.getMessage());
        }
    }

    /**
     * Lista todos os pacientes cadastrados.
     */
    public List<Paciente> listarTodos() {

        List<Paciente> pacientes = new ArrayList<>();

        String sql = "SELECT * FROM paciente";

        try (Connection con = ConexaoBD.conectar();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Paciente paciente = new Paciente(
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getDate("data_nascimento").toLocalDate(),
                        rs.getString("endereco"),
                        rs.getString("telefone")
                );

                paciente.setAlergia(rs.getString("alergia"));

                carregarContato(paciente);
                carregarRelacionamentos(paciente);
                pacientes.add(paciente);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar pacientes: "
                    + e.getMessage());
        }

        return pacientes;
    }

    /**
     * Busca um paciente pelo CPF.
     */
    @Override
    public Paciente buscar(Object cpf) {

        String sql = "SELECT * FROM paciente WHERE cpf = ?";

        try (Connection con = ConexaoBD.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, cpf.toString());

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    Paciente paciente = new Paciente(
                            rs.getString("nome"),
                            rs.getString("cpf"),
                            rs.getDate("data_nascimento").toLocalDate(),
                            rs.getString("endereco"),
                            rs.getString("telefone")
                    );

                    paciente.setAlergia(rs.getString("alergia"));

                    carregarContato(paciente);

                    carregarRelacionamentos(paciente);

                    return paciente;
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar paciente: "
                    + e.getMessage());
        }

        return null;
    }

    /**
     * Atualiza os dados de um paciente e seu contato.
     */
    @Override
    public boolean atualizar(Paciente paciente) {

        String sql = "UPDATE paciente SET nome = ?, "
                + "data_nascimento = ?, endereco = ?, "
                + "telefone = ?, alergia = ? WHERE cpf = ?";

        try (Connection con = ConexaoBD.conectar()) {

            con.setAutoCommit(false);

            try (PreparedStatement stmt = con.prepareStatement(sql)) {

                stmt.setString(1, paciente.getNome());
                stmt.setDate(2, Date.valueOf(paciente.getDataNascimento()));
                stmt.setString(3, paciente.getEndereco());
                stmt.setString(4, paciente.getTelefone());
                stmt.setString(5, paciente.getAlergia());
                stmt.setString(6, paciente.getCpf());

                boolean atualizado = stmt.executeUpdate() > 0;

                if (!atualizado) {
                    con.rollback();
                    return false;
                }

                salvarContato(paciente, con);

                con.commit();

                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar paciente: "
                    + e.getMessage());
            return false;
        }
    }

    /**
     * Exclui um paciente pelo CPF.
     */
    @Override
    public boolean excluir(Paciente paciente) {

        String sql = "DELETE FROM paciente WHERE cpf = ?";

        try (Connection con = ConexaoBD.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, paciente.getCpf());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir paciente: "
                    + e.getMessage());
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
