package DAO;

import database.ConexaoBD;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Medicamento;

/**
 * Classe responsável pelas operações de medicamentos
 * no banco de dados PostgreSQL. Implementa {@link OperacoesDAO}.
 *
 * @author Alice
 */
public class MedicamentoDAO implements OperacoesDAO<Medicamento> {

    private Medicamento mapear(ResultSet rs) throws SQLException {
        Medicamento medicamento = new Medicamento(
                rs.getString("nome"),
                rs.getString("dosagem"),
                rs.getString("posologia"),
                rs.getString("laboratorio")
        );
        medicamento.setId(rs.getInt("id"));
        medicamento.setCpfPaciente(rs.getString("paciente_cpf"));
        return medicamento;
    }

    /**
     * Cadastra um medicamento para o paciente indicado em
     * {@link Medicamento#getCpfPaciente()} e preenche o id gerado.
     */
    @Override
    public boolean inserir(Medicamento medicamento) {

        if (medicamento.getCpfPaciente() == null) {
            System.out.println("Erro ao cadastrar medicamento: paciente nao informado.");
            return false;
        }

        String sql = "INSERT INTO medicamento "
                + "(nome, dosagem, posologia, laboratorio, paciente_cpf) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement comando = ConexaoBD.conectar().prepareStatement(sql)) {

            comando.setString(1, medicamento.getNome());
            comando.setString(2, medicamento.getDosagem());
            comando.setString(3, medicamento.getPosologia());
            comando.setString(4, medicamento.getLaboratorio());
            comando.setString(5, medicamento.getCpfPaciente());

            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next()) {
                    medicamento.setId(rs.getInt(1));
                }
            }

            return true;

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                System.out.println("Esse paciente ja possui esse medicamento com essa dosagem.");
            } else {
                System.out.println("Erro ao cadastrar medicamento: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Atualiza nome, dosagem, posologia e laboratório de um medicamento (pelo id).
     */
    @Override
    public boolean atualizar(Medicamento medicamento) {

        if (medicamento.getId() == null) {
            return false;
        }

        String sql = "UPDATE medicamento SET nome = ?, dosagem = ?, "
                + "posologia = ?, laboratorio = ? WHERE id = ?";

        try (PreparedStatement comando = ConexaoBD.conectar().prepareStatement(sql)) {

            comando.setString(1, medicamento.getNome());
            comando.setString(2, medicamento.getDosagem());
            comando.setString(3, medicamento.getPosologia());
            comando.setString(4, medicamento.getLaboratorio());
            comando.setInt(5, medicamento.getId());

            return comando.executeUpdate() > 0;

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                System.out.println("Esse paciente ja possui esse medicamento com essa dosagem.");
            } else {
                System.out.println("Erro ao atualizar medicamento: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Exclui um medicamento (pelo id).
     */
    @Override
    public boolean excluir(Medicamento medicamento) {

        if (medicamento.getId() == null) {
            return false;
        }

        String sql = "DELETE FROM medicamento WHERE id = ?";

        try (PreparedStatement comando = ConexaoBD.conectar().prepareStatement(sql)) {

            comando.setInt(1, medicamento.getId());
            return comando.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir medicamento: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean pesquisar(Medicamento medicamento) {
        return medicamento.getId() != null && buscar(medicamento.getId()) != null;
    }

    /**
     * Busca um medicamento pelo id.
     */
    @Override
    public Medicamento buscar(Object identificador) {

        if (identificador == null) {
            return null;
        }

        int id;
        try {
            id = Integer.parseInt(identificador.toString());
        } catch (NumberFormatException e) {
            return null;
        }

        String sql = "SELECT * FROM medicamento WHERE id = ?";

        try (PreparedStatement comando = ConexaoBD.conectar().prepareStatement(sql)) {

            comando.setInt(1, id);

            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar medicamento: " + e.getMessage());
        }

        return null;
    }

    /**
     * Lista os medicamentos cadastrados para um paciente.
     */
    public List<Medicamento> listarMedicamentos(String cpf) {

        List<Medicamento> medicamentos = new ArrayList<>();

        String sql = "SELECT * FROM medicamento WHERE paciente_cpf = ? ORDER BY id";

        try (PreparedStatement comando = ConexaoBD.conectar().prepareStatement(sql)) {

            comando.setString(1, cpf);

            try (ResultSet rs = comando.executeQuery()) {
                while (rs.next()) {
                    medicamentos.add(mapear(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar medicamentos: " + e.getMessage());
        }

        return medicamentos;
    }
}
