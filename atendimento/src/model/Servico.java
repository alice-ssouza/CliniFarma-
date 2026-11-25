package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa um serviço realizado, contendo informações como data,
 * anotação, resultado e tipo do serviço.
 * 
 * <p>Esta classe implementa {@link Serializable} para permitir que
 * objetos do tipo {@code Servico} sejam serializados.</p>
 * 
 * @author Alice
 */
public class Servico implements Serializable {

    /** Identificador do serviço no banco de dados. */
    private Integer id;

    /** CPF do paciente atendido (chave estrangeira no banco). */
    private String cpfPaciente;

    /** Data em que o serviço foi realizado. */
    private LocalDate data;

    /** Observações ou anotações feitas sobre o serviço. */
    private String anotacao;

    /** Resultado obtido após a realização do serviço. */
    private String resultado;

    /** Tipo do serviço realizado. */
    private TipoServico tipo;

    /**
     * Construtor para criar um novo objeto {@code Servico} com os dados especificados.
     *
     * @param data a data do serviço
     * @param anotacao observações sobre o serviço
     * @param resultado resultado do serviço
     * @param tipo o tipo do serviço
     */
    public Servico(LocalDate data, String anotacao, String resultado, TipoServico tipo) {
        // O construtor reutiliza os setters para aplicar as validações
        setData(data);
        setAnotacao(anotacao);
        setResultado(resultado);
        setTipo(tipo);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCpfPaciente() {
        return cpfPaciente;
    }

    public void setCpfPaciente(String cpfPaciente) {
        this.cpfPaciente = cpfPaciente;
    }

    /**
     * Retorna a data do serviço.
     * 
     * @return a data do serviço
     */
    public LocalDate getData() {
        return data;
    }

    /**
     * Define a data do serviço.
     * 
     * @param data a nova data do serviço
     */
    public void setData(LocalDate data) {
        if (data == null) {
            throw new IllegalArgumentException("A data do servico e obrigatoria.");
        }
        this.data = data;
    }

    /**
     * Retorna a anotação do serviço.
     * 
     * @return a anotação do serviço
     */
    public String getAnotacao() {
        return anotacao;
    }

    /**
     * Define a anotação do serviço.
     * 
     * @param anotacao a nova anotação
     */
    public void setAnotacao(String anotacao) {
        this.anotacao = anotacao;
    }

    /**
     * Retorna o resultado do serviço.
     * 
     * @return o resultado do serviço
     */
    public String getResultado() {
        return resultado;
    }

    /**
     * Define o resultado do serviço.
     * 
     * @param resultado o novo resultado
     */
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    /**
     * Retorna o tipo do serviço.
     * 
     * @return o tipo do serviço
     */
    public TipoServico getTipo() {
        return tipo;
    }

    /**
     * Define o tipo do serviço.
     * 
     * @param tipo o novo tipo do serviço
     */
    public void setTipo(TipoServico tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo do servico e obrigatorio.");
        }
        this.tipo = tipo;
    }

    /**
     * Retorna uma representação textual do objeto {@code Servico}.
     * 
     * @return uma string com os dados do serviço
     */
    @Override
    public String toString() {
        return "Servico: " + tipo +
               " Data: " + data +
               " Resultado: " + resultado +
               " Anotacao: " + anotacao;
    }

    /**
     * Gera o código hash baseado na data e tipo do serviço.
     * 
     * @return o código hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(data, tipo);
    }

    /**
     * Verifica se dois objetos {@code Servico} são iguais, com base na
     * data e no tipo do serviço.
     * 
     * @param obj o objeto a ser comparado
     * @return {@code true} se os objetos forem iguais, {@code false} caso contrário
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Servico)) return false;
        Servico other = (Servico) obj;
        return Objects.equals(this.data, other.data) &&
               Objects.equals(this.tipo, other.tipo);
    }
}
