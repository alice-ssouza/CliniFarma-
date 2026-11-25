
package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa um medicamento com informações como nome,
 * dosagem, posologia e laboratório.
 *
 * @author Alice
 */
public class Medicamento implements Serializable {

    /** Identificador do medicamento no banco de dados */
    private Integer id;

    /** Nome do medicamento */
    private String nome;

    /** Dosagem do medicamento (ex: 500mg) */
    private String dosagem;

    /** Posologia (modo de uso) do medicamento */
    private String posologia;

    /** Nome do laboratório fabricante */
    private String laboratorio;

    /** CPF do paciente dono do medicamento (chave estrangeira no banco) */
    private String cpfPaciente;

    /**
     * Construtor para inicializar os atributos do medicamento.
     *
     * @param nome Nome do medicamento
     * @param dosagem Dosagem do medicamento
     * @param posologia Posologia do medicamento
     * @param laboratorio Nome do laboratório fabricante
     */
    public Medicamento(String nome, String dosagem,
            String posologia, String laboratorio) {

        // O construtor reutiliza os setters para aplicar as validações
        setNome(nome);
        setDosagem(dosagem);
        setPosologia(posologia);
        setLaboratorio(laboratorio);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do medicamento e obrigatorio.");
        }
        if (nome.trim().length() > 150) {
            throw new IllegalArgumentException("O nome do medicamento deve ter no maximo 150 caracteres.");
        }
        this.nome = nome.trim();
    }

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String dosagem) {
        if (dosagem == null || dosagem.isBlank()) {
            throw new IllegalArgumentException("A dosagem e obrigatoria.");
        }
        if (dosagem.trim().length() > 80) {
            throw new IllegalArgumentException("A dosagem deve ter no maximo 80 caracteres.");
        }
        this.dosagem = dosagem.trim();
    }

    public String getPosologia() {
        return posologia;
    }

    public void setPosologia(String posologia) {
        if (posologia != null && posologia.length() > 150) {
            throw new IllegalArgumentException("A posologia deve ter no maximo 150 caracteres.");
        }
        this.posologia = posologia;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        if (laboratorio != null && laboratorio.length() > 120) {
            throw new IllegalArgumentException("O laboratorio deve ter no maximo 120 caracteres.");
        }
        this.laboratorio = laboratorio;
    }

    public String getCpfPaciente() {
        return cpfPaciente;
    }

    public void setCpfPaciente(String cpfPaciente) {
        this.cpfPaciente = cpfPaciente;
    }

    @Override
    public String toString() {
        return nome + " - " + dosagem
                + " (" + posologia + "), " + laboratorio;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, dosagem);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Medicamento)) {
            return false;
        }

        Medicamento other = (Medicamento) obj;

        return Objects.equals(this.nome, other.nome)
                && Objects.equals(this.dosagem, other.dosagem);
    }
}
