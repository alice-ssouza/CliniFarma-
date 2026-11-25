
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

    /**
     * Construtor da classe Paciente.
     */
    public Paciente(String nome, String cpf, LocalDate dataNascimento,
                    String endereco, String telefone) {

        // O construtor reutiliza os setters para aplicar as validações
        setNome(nome);
        setCpf(cpf);
        setDataNascimento(dataNascimento);
        setEndereco(endereco);
        setTelefone(telefone);
    }

    // Getters e Setters

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("O CPF e obrigatorio.");
        }
        if (cpf.trim().length() > 14) {
            throw new IllegalArgumentException("O CPF deve ter no maximo 14 caracteres.");
        }
        this.cpf = cpf.trim();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome e obrigatorio.");
        }
        if (nome.trim().length() > 150) {
            throw new IllegalArgumentException("O nome deve ter no maximo 150 caracteres.");
        }
        this.nome = nome.trim();
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new IllegalArgumentException("A data de nascimento e obrigatoria.");
        }
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de nascimento nao pode estar no futuro.");
        }
        this.dataNascimento = dataNascimento;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        if (endereco != null && endereco.length() > 255) {
            throw new IllegalArgumentException("O endereco deve ter no maximo 255 caracteres.");
        }
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        if (telefone != null && telefone.length() > 30) {
            throw new IllegalArgumentException("O telefone deve ter no maximo 30 caracteres.");
        }
        this.telefone = telefone;
    }

    public String getAlergia() {
        return alergia;
    }

    public void setAlergia(String alergia) {
        if (alergia != null && alergia.length() > 150) {
            throw new IllegalArgumentException("A alergia deve ter no maximo 150 caracteres.");
        }
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
            setNome(nome);
            setTelefone(telefone);
        }

        public String getNome() {
            return nome;
        }

        public String getTelefone() {
            return telefone;
        }

        public void setNome(String nome) {
            if (nome == null) {
                throw new IllegalArgumentException("O nome do contato de emergencia e obrigatorio.");
            }
            this.nome = nome;
        }

        public void setTelefone(String telefone) {
            if (telefone == null) {
                throw new IllegalArgumentException("O telefone do contato de emergencia e obrigatorio.");
            }
            this.telefone = telefone;
        }

        @Override
        public String toString() {
            return "Nome: " + nome + " - Telefone: " + telefone;
        }
    }
}
