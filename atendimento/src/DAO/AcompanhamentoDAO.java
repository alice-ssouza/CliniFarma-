
package DAO;

import database.ConexaoBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Acompanhamento;
import model.Medicamento;
import model.Paciente;

/**
 * Classe responsável pelo acesso e manipulação dos acompanhamentos clínicos.
 *
 * @author Alice
 */
public class AcompanhamentoDAO {

    private final PacienteDAO pacienteDAO = new PacienteDAO();

    /**
     * Registra um acompanhamento clínico para o paciente.
     *
     * @param cpf CPF do paciente
     * @param acompanhamento acompanhamento a ser registrado
     * @return true se foi registrado; false caso contrário
     */
    public boolean registrarAcompanhamento(String cpf, Acompanhamento acompanhamento) {

        String sql = "INSERT INTO acompanhamento "
                + "(data, notas_clinicas, resultado, paciente_cpf, medicamento_id) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexaoBD.conectar()) {

            Integer medicamentoId = null;

            if (acompanhamento.getMedicamento() != null) {

                String sqlMedicamento = "SELECT id FROM medicamento "
                        + "WHERE paciente_cpf = ? AND nome = ? "
                        + "AND dosagem = ? AND laboratorio = ?";

                try (PreparedStatement comando = con.prepareStatement(sqlMedicamento)) {

                    comando.setString(1, cpf);
                    comando.setString(2, acompanhamento.getMedicamento().getNome());
                    comando.setString(3, acompanhamento.getMedicamento().getDosagem());
                    comando.setString(4, acompanhamento.getMedicamento().getLaboratorio());

                    try (ResultSet resultado = comando.executeQuery()) {
                        if (resultado.next()) {
                            medicamentoId = resultado.getInt("id");
                        } else {
                            System.out.println("Medicamento não encontrado para esse paciente.");
                            return false;
                        }
                    }
                }
            }

            try (PreparedStatement comando = con.prepareStatement(sql)) {

                comando.setDate(1, java.sql.Date.valueOf(acompanhamento.getData()));
                comando.setString(2, acompanhamento.getNotasClinicas());
                comando.setString(3, acompanhamento.getResultado());
                comando.setString(4, cpf);

                if (medicamentoId != null) {
                    comando.setInt(5, medicamentoId);
                } else {
                    comando.setNull(5, Types.INTEGER);
                }

                comando.executeUpdate();
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Erro ao registrar acompanhamento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lista os acompanhamentos clínicos de um paciente.
     *
     * @param cpf CPF do paciente
     * @return lista de acompanhamentos
     */
    public List<Acompanhamento> listarAcompanhamentos(String cpf) {

        Paciente paciente = pacienteDAO.buscar(cpf);

        if (paciente == null) {
            return new ArrayList<>();
        }

        return listarAcompanhamentos(cpf, paciente);
    }

    /**
     * Lista os acompanhamentos usando um paciente já carregado.
     *
     * @param cpf CPF do paciente
     * @param paciente paciente já encontrado
     * @return lista de acompanhamentos
     */
    public List<Acompanhamento> listarAcompanhamentos(String cpf, Paciente paciente) {

        List<Acompanhamento> acompanhamentos = new ArrayList<>();

        String sql = "SELECT a.data, a.notas_clinicas, a.resultado, "
                + "a.medicamento_id, m.nome, m.dosagem, m.posologia, m.laboratorio "
                + "FROM acompanhamento a "
                + "LEFT JOIN medicamento m ON a.medicamento_id = m.id "
                + "WHERE a.paciente_cpf = ?";

        try (Connection con = ConexaoBD.conectar();
             PreparedStatement comando = con.prepareStatement(sql)) {

            comando.setString(1, cpf);

            try (ResultSet resultado = comando.executeQuery()) {

                while (resultado.next()) {

                    Medicamento medicamento = null;

                    if (resultado.getObject("medicamento_id") != null) {
                        medicamento = new Medicamento(
                                resultado.getString("nome"),
                                resultado.getString("dosagem"),
                                resultado.getString("posologia"),
                                resultado.getString("laboratorio")
                        );
                    }

                    Acompanhamento acompanhamento = new Acompanhamento(
                            resultado.getDate("data").toLocalDate(),
                            resultado.getString("notas_clinicas"),
                            resultado.getString("resultado"),
                            medicamento,
                            paciente
                    );

                    acompanhamentos.add(acompanhamento);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar acompanhamentos: " + e.getMessage());
        }

        return acompanhamentos;
    }

    /**
     * Atualiza as notas e o resultado de um acompanhamento.
     *
     * @param cpf CPF do paciente
     * @param data data do acompanhamento
     * @param novasNotas novas notas clínicas
     * @param novoResultado novo resultado
     * @return true se foi atualizado; false caso contrário
     */
    public boolean atualizarAcompanhamento(String cpf, LocalDate data,
            String novasNotas, String novoResultado) {

        String sql = "UPDATE acompanhamento "
                + "SET notas_clinicas = ?, resultado = ? "
                + "WHERE paciente_cpf = ? AND data = ?";

        try (Connection con = ConexaoBD.conectar();
             PreparedStatement comando = con.prepareStatement(sql)) {

            comando.setString(1, novasNotas);
            comando.setString(2, novoResultado);
            comando.setString(3, cpf);
            comando.setDate(4, java.sql.Date.valueOf(data));

            return comando.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar acompanhamento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exclui um acompanhamento pela data e CPF do paciente.
     *
     * @param cpf CPF do paciente
     * @param data data do acompanhamento
     * @return true se foi excluído; false caso contrário
     */
    public boolean excluirAcompanhamento(String cpf, LocalDate data) {

        String sql = "DELETE FROM acompanhamento "
                + "WHERE paciente_cpf = ? AND data = ?";

        try (Connection con = ConexaoBD.conectar();
             PreparedStatement comando = con.prepareStatement(sql)) {

            comando.setString(1, cpf);
            comando.setDate(2, java.sql.Date.valueOf(data));

            return comando.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir acompanhamento: " + e.getMessage());
            return false;
        }
    }
}
