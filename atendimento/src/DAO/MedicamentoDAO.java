
package DAO;

import database.ConexaoBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Medicamento;

/**
 * Classe responsável pelas operações de medicamentos
 * no banco de dados PostgreSQL.
 *
 * @author Alice
 */
public class MedicamentoDAO {

    /**
     * Cadastra um medicamento para um paciente.
     */
    public boolean registrarMedicamento(String cpf, Medicamento medicamento) {

        String sql = "INSERT INTO medicamento "
                + "(nome, dosagem, posologia, laboratorio, paciente_cpf) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexaoBD.conectar();
             PreparedStatement comando = con.prepareStatement(sql)) {

            comando.setString(1, medicamento.getNome());
            comando.setString(2, medicamento.getDosagem());
            comando.setString(3, medicamento.getPosologia());
            comando.setString(4, medicamento.getLaboratorio());
            comando.setString(5, cpf);

            comando.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar medicamento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lista os medicamentos cadastrados para um paciente.
     */
    public List<Medicamento> listarMedicamentos(String cpf) {

        List<Medicamento> medicamentos = new ArrayList<>();

        String sql = "SELECT * FROM medicamento WHERE paciente_cpf = ?";

        try (Connection con = ConexaoBD.conectar();
             PreparedStatement comando = con.prepareStatement(sql)) {

            comando.setString(1, cpf);

            try (ResultSet resultado = comando.executeQuery()) {

                while (resultado.next()) {

                    Medicamento medicamento = new Medicamento(
                            resultado.getString("nome"),
                            resultado.getString("dosagem"),
                            resultado.getString("posologia"),
                            resultado.getString("laboratorio")
                    );
                    medicamento.setId(resultado.getInt("id"));
                    medicamentos.add(medicamento);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar medicamentos: " + e.getMessage());
        }

        return medicamentos;
    }

    /**
     * Atualiza o nome, a dosagem e a posologia de um medicamento.
     */
    public boolean atualizarMedicamento(String cpf, String nomeAtual,
            String dosagemAtual, String novoNome, String novaDosagem,
            String novaPosologia) {

        String sql = "UPDATE medicamento SET nome = ?, dosagem = ?, posologia = ? "
                + "WHERE paciente_cpf = ? "
                + "AND LOWER(nome) = LOWER(?) "
                + "AND LOWER(dosagem) = LOWER(?)";

        try (Connection con = ConexaoBD.conectar();
             PreparedStatement comando = con.prepareStatement(sql)) {

            comando.setString(1, novoNome);
            comando.setString(2, novaDosagem);
            comando.setString(3, novaPosologia);
            comando.setString(4, cpf);
            comando.setString(5, nomeAtual);
            comando.setString(6, dosagemAtual);

            return comando.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar medicamento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exclui um medicamento de um paciente.
     */
    public boolean excluirMedicamento(String cpf, String nome, String dosagem) {

        String sql = "DELETE FROM medicamento "
                + "WHERE paciente_cpf = ? "
                + "AND LOWER(nome) = LOWER(?) "
                + "AND LOWER(dosagem) = LOWER(?)";

        try (Connection con = ConexaoBD.conectar();
             PreparedStatement comando = con.prepareStatement(sql)) {

            comando.setString(1, cpf);
            comando.setString(2, nome);
            comando.setString(3, dosagem);

            return comando.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir medicamento: " + e.getMessage());
            return false;
        }
    }
}
