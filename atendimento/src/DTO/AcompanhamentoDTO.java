package DTO;

import java.time.LocalDate;

/**
 * Objeto de Transferência de Dados (DTO) para exibição resumida de Acompanhamentos.
 */
public class AcompanhamentoDTO {

    private final String nomePaciente;
    private final String cpfPaciente;
    private final LocalDate data;
    private final String resultado;
    private final String medicamentoUtilizado;

    public AcompanhamentoDTO(String nomePaciente, String cpfPaciente,
                             LocalDate data, String resultado, String medicamentoUtilizado) {
        this.nomePaciente = nomePaciente;
        this.cpfPaciente = cpfPaciente;
        this.data = data;
        this.resultado = resultado;
        this.medicamentoUtilizado = medicamentoUtilizado;
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public String getCpfPaciente() {
        return cpfPaciente;
    }

    public LocalDate getData() {
        return data;
    }

    public String getResultado() {
        return resultado;
    }

    public String getMedicamentoUtilizado() {
        return medicamentoUtilizado;
    }

    @Override
    public String toString() {
        return "Paciente: " + nomePaciente + " (" + cpfPaciente + ") | Data: " + data
                + " | Resultado: " + resultado + " | Medicamento: " + medicamentoUtilizado;
    }
}
