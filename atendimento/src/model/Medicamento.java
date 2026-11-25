
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

        this.nome = nome;
        this.dosagem = dosagem;
        this.posologia = posologia;
        this.laboratorio = laboratorio;
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
        this.nome = nome;
    }

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String dosagem) {
        this.dosagem = dosagem;
    }

    public String getPosologia() {
        return posologia;
    }

    public void setPosologia(String posologia) {
        this.posologia = posologia;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
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
