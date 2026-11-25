
package DAO;

import database.ConexaoBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import model.Servico;
import model.TipoServico;

/**
 * Classe responsável por gerenciar os serviços prestados aos pacientes.
 *
 * @author Alice
 */
public class ServicoDAO {

    /**
     * Registra um novo serviço para o paciente identificado pelo CPF.
     *
     * @param cpf CPF do paciente
     * @param servico Serviço a ser registrado
     * @return true se o serviço foi registrado; false caso contrário
     */
    public boolean registrarServico(String cpf, Servico servico) {
        String sql = "INSERT INTO servico "
                + "(data, anotacao, resultado, tipo, paciente_cpf) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexaoBD.conectar();
             PreparedStatement comando = con.prepareStatement(sql)) {

            comando.setDate(1, java.sql.Date.valueOf(servico.getData()));
            comando.setString(2, servico.getAnotacao());
            comando.setString(3, servico.getResultado());
            comando.setString(4, servico.getTipo().name());
            comando.setString(5, cpf);

            comando.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao registrar serviço: " + e.getMessage());
            return false;
        }
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
     * Atualiza as informações de um serviço do paciente.
     *
     * @param cpf CPF do paciente
     * @param data Data do serviço
     * @param tipo Tipo do serviço
     * @param novaAnotacao Nova anotação
     * @param novoResultado Novo resultado
     * @return true se a atualização foi realizada; false caso contrário
     */
    public boolean atualizarServico(String cpf, LocalDate data,
            TipoServico tipo, String novaAnotacao, String novoResultado) {

        String sql = "UPDATE servico SET anotacao = ?, resultado = ? "
                + "WHERE paciente_cpf = ? AND data = ? AND tipo = ?";

        try (Connection con = ConexaoBD.conectar();
             PreparedStatement comando = con.prepareStatement(sql)) {

            comando.setString(1, novaAnotacao);
            comando.setString(2, novoResultado);
            comando.setString(3, cpf);
            comando.setDate(4, java.sql.Date.valueOf(data));
            comando.setString(5, tipo.name());

            return comando.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar serviço: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exclui um serviço do histórico do paciente.
     *
     * @param cpf CPF do paciente
     * @param data Data do serviço
     * @param tipo Tipo do serviço
     * @return true se o serviço foi excluído; false caso contrário
     */
    public boolean excluirServico(String cpf, LocalDate data, TipoServico tipo) {
        String sql = "DELETE FROM servico "
                + "WHERE paciente_cpf = ? AND data = ? AND tipo = ?";

        try (Connection con = ConexaoBD.conectar();
             PreparedStatement comando = con.prepareStatement(sql)) {

            comando.setString(1, cpf);
            comando.setDate(2, java.sql.Date.valueOf(data));
            comando.setString(3, tipo.name());

            return comando.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir serviço: " + e.getMessage());
            return false;
        }
    }
    
    public List<Servico> listarServicos(String cpf) {

        List<Servico> servicos = new java.util.ArrayList<>();

        String sql = "SELECT * FROM servico WHERE paciente_cpf = ?";

        try (Connection con = ConexaoBD.conectar();
             PreparedStatement comando = con.prepareStatement(sql)) {

            comando.setString(1, cpf);

            try (java.sql.ResultSet resultado = comando.executeQuery()) {

                while (resultado.next()) {

                    Servico servico = new Servico(
                        resultado.getDate("data").toLocalDate(),
                        resultado.getString("anotacao"),
                        resultado.getString("resultado"),
                        TipoServico.valueOf(resultado.getString("tipo"))
                    );

                    servicos.add(servico);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar serviços: " + e.getMessage());
        }

        return servicos;
    }

}
