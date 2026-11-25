package DAO;

import database.ConexaoBD;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import model.Acompanhamento;
import model.Medicamento;
import model.Paciente;

/**
 * Classe responsável pelo acesso e manipulação dos acompanhamentos clínicos.
 * Implementa {@link OperacoesDAO}.
 *
 * @author Alice
 */
public class AcompanhamentoDAO implements OperacoesDAO<Acompanhamento> {

    private static final String SELECT_BASE =
            "SELECT a.id, a.data, a.notas_clinicas, a.resultado, a.paciente_cpf, "
            + "a.medicamento_id, m.nome AS med_nome, m.dosagem AS med_dosagem, "
            + "m.posologia AS med_posologia, m.laboratorio AS med_laboratorio "
            + "FROM acompanhamento a "
            + "LEFT JOIN medicamento m ON a.medicamento_id = m.id ";

    /**
     * Monta o Medicamento (ou null) a partir das colunas do LEFT JOIN.
     */
    private Medicamento mapearMedicamento(ResultSet rs, String cpf) throws SQLException {
        if (rs.getObject("medicamento_id") == null) {
            return null;
        }
        Medicamento medicamento = new Medicamento(
                rs.getString("med_nome"),
                rs.getString("med_dosagem"),
                rs.getString("med_posologia"),
                rs.getString("med_laboratorio")
        );
        medicamento.setId(rs.getInt("medicamento_id"));
        medicamento.setCpfPaciente(cpf);
        return medicamento;
    }

    private Acompanhamento mapear(ResultSet rs, Paciente paciente) throws SQLException {
        Acompanhamento acompanhamento = new Acompanhamento(
                rs.getDate("data").toLocalDate(),
                rs.getString("notas_clinicas"),
                rs.getString("resultado"),
                mapearMedicamento(rs, paciente.getCpf()),
                paciente
        );
        acompanhamento.setId(rs.getInt("id"));
        return acompanhamento;
    }

    /**
     * Descobre o id do medicamento do acompanhamento: usa o id, se já existir,
     * ou procura o medicamento do paciente pelos dados informados.
     *
     * @return id encontrado, ou null se o medicamento não existir para o paciente
     */
    private Integer resolverMedicamentoId(String cpf, Medicamento medicamento)
            throws SQLException {

        if (medicamento.getId() != null) {
            return medicamento.getId();
        }

        String sql = "SELECT id FROM medicamento "
                + "WHERE paciente_cpf = ? AND nome = ? AND dosagem = ? "
                + "AND laboratorio IS NOT DISTINCT FROM ?";

        try (PreparedStatement comando = ConexaoBD.conectar().prepareStatement(sql)) {

            comando.setString(1, cpf);
            comando.setString(2, medicamento.getNome());
            comando.setString(3, medicamento.getDosagem());
            comando.setString(4, medicamento.getLaboratorio());

            try (ResultSet rs = comando.executeQuery()) {
                return rs.next() ? rs.getInt("id") : null;
            }
        }
    }

    /**
     * Registra um acompanhamento clínico para o paciente do próprio objeto
     * e preenche o id gerado.
     */
    @Override
    public boolean inserir(Acompanhamento acompanhamento) {

        String cpf = acompanhamento.getPaciente().getCpf();

        String sql = "INSERT INTO acompanhamento "
                + "(data, notas_clinicas, resultado, paciente_cpf, medicamento_id) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING id";

        try {
            Integer medicamentoId = null;

            if (acompanhamento.getMedicamento() != null) {
                medicamentoId = resolverMedicamentoId(cpf, acompanhamento.getMedicamento());

                if (medicamentoId == null) {
                    System.out.println("Medicamento nao encontrado para esse paciente.");
                    return false;
                }
            }

            try (PreparedStatement comando = ConexaoBD.conectar().prepareStatement(sql)) {

                comando.setDate(1, Date.valueOf(acompanhamento.getData()));
                comando.setString(2, acompanhamento.getNotasClinicas());
                comando.setString(3, acompanhamento.getResultado());
                comando.setString(4, cpf);

                if (medicamentoId != null) {
                    comando.setInt(5, medicamentoId);
                } else {
                    comando.setNull(5, Types.INTEGER);
                }

                try (ResultSet rs = comando.executeQuery()) {
                    if (rs.next()) {
                        acompanhamento.setId(rs.getInt(1));
                    }
                }

                return true;
            }

        } catch (SQLException e) {
            System.out.println("Erro ao registrar acompanhamento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza as notas e o resultado de um acompanhamento (pelo id).
     */
    @Override
    public boolean atualizar(Acompanhamento acompanhamento) {

        if (acompanhamento.getId() == null) {
            return false;
        }

        String sql = "UPDATE acompanhamento "
                + "SET notas_clinicas = ?, resultado = ? WHERE id = ?";

        try (PreparedStatement comando = ConexaoBD.conectar().prepareStatement(sql)) {

            comando.setString(1, acompanhamento.getNotasClinicas());
            comando.setString(2, acompanhamento.getResultado());
            comando.setInt(3, acompanhamento.getId());

            return comando.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar acompanhamento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exclui um acompanhamento (pelo id).
     */
    @Override
    public boolean excluir(Acompanhamento acompanhamento) {

        if (acompanhamento.getId() == null) {
            return false;
        }

        String sql = "DELETE FROM acompanhamento WHERE id = ?";

        try (PreparedStatement comando = ConexaoBD.conectar().prepareStatement(sql)) {

            comando.setInt(1, acompanhamento.getId());
            return comando.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir acompanhamento: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean pesquisar(Acompanhamento acompanhamento) {
        return acompanhamento.getId() != null && buscar(acompanhamento.getId()) != null;
    }

    /**
     * Busca um acompanhamento pelo id (o paciente vem com os dados básicos).
     */
    @Override
    public Acompanhamento buscar(Object identificador) {

        if (identificador == null) {
            return null;
        }

        int id;
        try {
            id = Integer.parseInt(identificador.toString());
        } catch (NumberFormatException e) {
            return null;
        }

        String sql = SELECT_BASE + "WHERE a.id = ?";
        String sqlPaciente = "SELECT * FROM paciente WHERE cpf = ?";

        try (PreparedStatement comando = ConexaoBD.conectar().prepareStatement(sql)) {

            comando.setInt(1, id);

            try (ResultSet rs = comando.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                String cpf = rs.getString("paciente_cpf");

                try (PreparedStatement cp = ConexaoBD.conectar().prepareStatement(sqlPaciente)) {
                    cp.setString(1, cpf);
                    try (ResultSet rp = cp.executeQuery()) {
                        if (!rp.next()) {
                            return null;
                        }
                        Paciente paciente = new Paciente(
                                rp.getString("nome"),
                                rp.getString("cpf"),
                                rp.getDate("data_nascimento").toLocalDate(),
                                rp.getString("endereco"),
                                rp.getString("telefone"));
                        return mapear(rs, paciente);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar acompanhamento: " + e.getMessage());
        }

        return null;
    }

    /**
     * Lista os acompanhamentos de um paciente já carregado.
     *
     * @param cpf CPF do paciente
     * @param paciente paciente dono dos acompanhamentos
     * @return lista de acompanhamentos
     */
    public List<Acompanhamento> listarAcompanhamentos(String cpf, Paciente paciente) {

        List<Acompanhamento> acompanhamentos = new ArrayList<>();

        String sql = SELECT_BASE + "WHERE a.paciente_cpf = ? ORDER BY a.data, a.id";

        try (PreparedStatement comando = ConexaoBD.conectar().prepareStatement(sql)) {

            comando.setString(1, cpf);

            try (ResultSet rs = comando.executeQuery()) {
                while (rs.next()) {
                    acompanhamentos.add(mapear(rs, paciente));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar acompanhamentos: " + e.getMessage());
        }

        return acompanhamentos;
    }
}
