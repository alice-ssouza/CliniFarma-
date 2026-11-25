package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * Representa um acompanhamento clínico de um paciente,
 * contendo informações como data, notas clínicas, resultado e medicamento utilizado.
 * 
 * Implementa {@link Serializable} para permitir a persistência em arquivos.
 * 
 * @author Alice
 */
public class Acompanhamento implements Serializable {

    /** Data em que o acompanhamento foi realizado */
    private final LocalDate data;

    /** Notas clínicas registradas durante o acompanhamento */
    private String notasClinicas;

    /** Resultado obtido após o acompanhamento */
    private String resultado;

    /** Medicamento relacionado ao acompanhamento */
    private Medicamento medicamento;

    /** Paciente ao qual o acompanhamento está associado */
    private final Paciente paciente;

    /**
     * Construtor para inicializar todos os atributos de um acompanhamento.
     *
     * @param data Data do acompanhamento
     * @param notasClinicas Notas clínicas registradas
     * @param resultado Resultado obtido
     * @param medicamento Medicamento utilizado (pode ser {@code null})
     * @param paciente Paciente relacionado ao acompanhamento
     */
    public Acompanhamento(LocalDate data, String notasClinicas, String resultado, Medicamento medicamento, Paciente paciente) {
        this.data = data;
        this.notasClinicas = notasClinicas;
        this.resultado = resultado;
        this.medicamento = medicamento;
        this.paciente = paciente;
    }

    /**
     * Retorna a data do acompanhamento.
     *
     * @return Data do acompanhamento
     */
    public LocalDate getData() {
        return data;
    }

    /**
     * Retorna as notas clínicas do acompanhamento.
     *
     * @return Notas clínicas
     */
    public String getNotasClinicas() {
        return notasClinicas;
    }

    /**
     * Atualiza as notas clínicas do acompanhamento.
     *
     * @param notasClinicas Novas notas clínicas
     */
    public void setNotasClinicas(String notasClinicas) {
        this.notasClinicas = notasClinicas;
    }

    /**
     * Retorna o resultado do acompanhamento.
     *
     * @return Resultado
     */
    public String getResultado() {
        return resultado;
    }

    /**
     * Atualiza o resultado do acompanhamento.
     *
     * @param resultado Novo resultado
     */
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    /**
     * Retorna o medicamento associado ao acompanhamento.
     *
     * @return Medicamento, ou {@code null} se não houver
     */
    public Medicamento getMedicamento() {
        return medicamento;
    }

    /**
     * Atualiza o medicamento associado ao acompanhamento.
     *
     * @param medicamento Novo medicamento
     */
    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    /**
     * Retorna o paciente relacionado a este acompanhamento.
     *
     * @return Paciente
     */
    public Paciente getPaciente() {
        return paciente;
    }

    /**
     * Retorna uma representação textual do acompanhamento.
     *
     * @return String com as informações do acompanhamento
     */
    @Override
    public String toString() {
        return "Acompanhamento{" +
                "\ndata=" + data +
                "\n notasClinicas='" + notasClinicas + '\'' +
                "\n resultado='" + resultado + '\'' +
                "\n medicamento=" + (medicamento != null ? medicamento.toString() : "Nenhum") +
                "\n paciente=" + paciente +
                '}';
    }

    /**
     * Gera um código hash baseado na data e no paciente.
     *
     * @return Código hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(data, paciente);
    }

    /**
     * Verifica se dois acompanhamentos são iguais com base na data e no paciente.
     *
     * @param obj Objeto a ser comparado
     * @return {@code true} se forem iguais; {@code false} caso contrário
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Acompanhamento)) return false;
        Acompanhamento other = (Acompanhamento) obj;
        return Objects.equals(this.data, other.data) &&
               Objects.equals(this.paciente, other.paciente);
    }
}
