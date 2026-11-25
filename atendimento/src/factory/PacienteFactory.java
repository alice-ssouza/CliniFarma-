package factory;

import model.Paciente;
import java.time.LocalDate;

/**
 * Classe responsável por criar objetos Paciente.
 *
 * @author Alice
 */
public class PacienteFactory {

    public Paciente criarPaciente(String cpf, String nome,
            String endereco, String telefone, String alergia,
            LocalDate dataNascimento) {

        Paciente paciente = new Paciente(
                nome,
                cpf,
                dataNascimento,
                endereco,
                telefone
        );

        paciente.setAlergia(alergia);

        return paciente;
    }
}