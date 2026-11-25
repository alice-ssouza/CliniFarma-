
package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa um paciente com seus dados pessoais,
 * histórico de serviços, acompanhamentos e medicamentos.
 *
 * @author Alice
 */
public class Paciente implements Serializable {

    /** CPF do paciente */
    private String cpf;

    /** Nome completo do paciente */
    private String nome;

    /** Data de nascimento */
    private LocalDate dataNascimento;

    /** Endereço residencial */
    private String endereco;

    /** Telefone para contato */
    private String telefone;

    /** Informações sobre alergias */
    private String alergia;

    /** Contato de emergência do paciente */
    private ContatoEmergencia contatoEmergencia;

    /** Histórico de serviços realizados */
    private List<Servico> historicoServicos = new ArrayList<>();

    /** Histórico de acompanhamentos */
    private List<Acompanhamento> historicoAcompanhamentos = new ArrayList<>();

    /** Medicamentos utilizados pelo paciente */
    private List<Medicamento> medicamentos = new ArrayList<>();

    /** Medicamentos aos quais o paciente tem alergia */
    private List<Medicamento> alergiasMedicamentos = new ArrayList<>();

    /**
     * Construtor da classe Paciente.
     */
    public Paciente(String nome, String cpf, LocalDate dataNascimento,
                    String endereco, String telefone) {

        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.endereco = endereco;
        this.telefone = telefone;
    }

    // Getters e Setters

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getAlergia() {
        return alergia;
    }

    public void setAlergia(String alergia) {
        this.alergia = alergia;
    }

    // Contato de emergência

    public ContatoEmergencia getContatoEmergencia() {
        return contatoEmergencia;
    }

    public void setContatoEmergencia(ContatoEmergencia contatoEmergencia) {
        this.contatoEmergencia = contatoEmergencia;
    }

    // Histórico de serviços

    public void adicionarServico(Servico servico) {
        historicoServicos.add(servico);
    }

    public List<Servico> getHistoricoServicos() {
        return historicoServicos;
    }

    // Histórico de acompanhamentos

    public void adicionarAcompanhamento(Acompanhamento acompanhamento) {
        historicoAcompanhamentos.add(acompanhamento);
    }

    public List<Acompanhamento> getHistoricoAcompanhamentos() {
        return historicoAcompanhamentos;
    }

    // Medicamentos

    public void adicionarMedicamento(Medicamento medicamento) {
        medicamentos.add(medicamento);
    }

    public List<Medicamento> getMedicamentos() {
        return medicamentos;
    }

    // Alergias a medicamentos

    public void adicionarAlergiaMedicamento(Medicamento medicamento) {
        alergiasMedicamentos.add(medicamento);
    }

    public List<Medicamento> getAlergiasMedicamentos() {
        return alergiasMedicamentos;
    }

    /**
     * Retorna os dados básicos do paciente.
     */
    @Override
    public String toString() {
        return "Paciente{" +
                "cpf='" + cpf + '\'' +
                ", nome='" + nome + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", endereco='" + endereco + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }

    /**
     * Gera o código hash utilizando o CPF.
     */
    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }

    /**
     * Compara pacientes utilizando o CPF.
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Paciente outro = (Paciente) obj;

        return Objects.equals(cpf, outro.cpf);
    }

    /**
     * Classe interna que representa o contato de emergência do paciente.
     */
    public class ContatoEmergencia {

        private String nome;
        private String telefone;

        public ContatoEmergencia(String nome, String telefone) {
            this.nome = nome;
            this.telefone = telefone;
        }

        public String getNome() {
            return nome;
        }

        public String getTelefone() {
            return telefone;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public void setTelefone(String telefone) {
            this.telefone = telefone;
        }

        @Override
        public String toString() {
            return "Nome: " + nome + " - Telefone: " + telefone;
        }
    }
}
