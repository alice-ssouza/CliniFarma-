package DAO;

import database.ConexaoBD;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Servico;
import model.TipoServico;

/**
 * Classe responsável por gerenciar os serviços prestados aos pacientes.
 * Implementa {@link OperacoesDAO}.
 *
 * @author Alice
 */
public class ServicoDAO implements OperacoesDAO<Servico> {

    private Servico mapear(ResultSet rs) throws SQLException {
        Servico servico = new Servico(
                rs.getDate("data").toLocalDate(),
                rs.getString("anotacao"),
                rs.getString("resultado"),
                TipoServico.valueOf(rs.getString("tipo"))
        );
        servico.setId(rs.getInt("id"));
        servico.setCpfPaciente(rs.getString("paciente_cpf"));
        return servico;
    }

    /**
     * Registra um novo serviço para o paciente indicado em
     * {@link Servico#getCpfPaciente()} e preenche o id gerado.
     */
    @Override
    public boolean inserir(Servico servico) {

        if (servico.getCpfPaciente() == null) {
            System.out.println("Erro ao registrar servico: paciente nao informado.");
            return false;
        }

        String sql = "INSERT INTO servico "
                + "(data, anotacao, resultado, tipo, paciente_cpf) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement comando = ConexaoBD.conectar().prepareStatement(sql)) {

            comando.setDate(1, Date.valueOf(servico.getData()));
            comando.setString(2, servico.getAnotacao());
            comando.setString(3, servico.getResultado());
            comando.setString(4, servico.getTipo().name());
            comando.setString(5, servico.getCpfPaciente());

            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next()) {
                    servico.setId(rs.getInt(1));
                }
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao registrar servico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza data, anotação, resultado e tipo de um serviço (pelo id).
     */
    @Override
    public boolean atualizar(Servico servico) {

        if (servico.getId() == null) {
            return false;
        }

        String sql = "UPDATE servico SET data = ?, anotacao = ?, "
                + "resultado = ?, tipo = ? WHERE id = ?";

        try (PreparedStatement comando = ConexaoBD.conectar().prepareStatement(sql)) {

            comando.setDate(1, Date.valueOf(servico.getData()));
            comando.setString(2, servico.getAnotacao());
            comando.setString(3, servico.getResultado());
            comando.setString(4, servico.getTipo().name());
            comando.setInt(5, servico.getId());

            return comando.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar servico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exclui um serviço do histórico do paciente (pelo id).
     */
    @Override
    public boolean excluir(Servico servico) {

        if (servico.getId() == null) {
            return false;
        }

        String sql = "DELETE FROM servico WHERE id = ?";

        try (PreparedStatement comando = ConexaoBD.conectar().prepareStatement(sql)) {

            comando.setInt(1, servico.getId());
            return comando.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir servico: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean pesquisar(Servico servico) {
        return servico.getId() != null && buscar(servico.getId()) != null;
    }

    /**
     * Busca um serviço pelo id.
     */
    @Override
    public Servico buscar(Object identificador) {

        if (identificador == null) {
            return null;
        }

        int id;
        try {
            id = Integer.parseInt(identificador.toString());
        } catch (NumberFormatException e) {
            return null;
        }

        String sql = "SELECT * FROM servico WHERE id = ?";

        try (PreparedStatement comando = ConexaoBD.conectar().prepareStatement(sql)) {

            comando.setInt(1, id);

            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar servico: " + e.getMessage());
        }

        return null;
    }

    /**
     * Retorna os tipos de serviços disponíveis.
     *
     * @return Lista de tipos de serviço
     */
    public List<TipoServico> listarServicosDisponiveis() {
        return List.of(TipoServico.values());
    }

    /**
     * Lista os serviços de um paciente.
     */
    public List<Servico> listarServicos(String cpf) {

        List<Servico> servicos = new ArrayList<>();

        String sql = "SELECT * FROM servico WHERE paciente_cpf = ? ORDER BY data, id";

        try (PreparedStatement comando = ConexaoBD.conectar().prepareStatement(sql)) {

            comando.setString(1, cpf);

            try (ResultSet rs = comando.executeQuery()) {
                while (rs.next()) {
                    servicos.add(mapear(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar servicos: " + e.getMessage());
        }

        return servicos;
    }
}
