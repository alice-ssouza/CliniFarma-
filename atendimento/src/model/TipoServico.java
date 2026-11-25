package model;

/**
 * Enumeração que representa os tipos de serviços disponíveis para os pacientes.
 * Cada tipo de serviço possui uma descrição associada.
 * 
 * Exemplos de serviços incluem aferição de pressão, medição de glicose,
 * aplicação de injetáveis para adultos e crianças, e colocação de brincos.
 * 
 * @author Alice
 */
public enum TipoServico {

    /** Serviço de aferição de pressão arterial */
    AFERICAO_PRESSAO("Afericao de Pressao"),

    /** Serviço de medição de glicose no sangue */
    MEDICAO_GLICOSE("Medicao de Glicose"),

    /** Aplicação de injetáveis em pacientes adultos */
    APLICACAO_INJETAVEIS_ADULTO("Aplicacao de Injetaveis - Adulto"),

    /** Aplicação de injetáveis em pacientes crianças */
    APLICACAO_INJETAVEIS_CRIANCA("Aplicacao de Injetaveis - Crianca"),

    /** Colocação de brincos em pacientes adultos */
    COLOCACAO_BRINCOS_ADULTO("Colocacao de Brincos - Adulto"),

    /** Colocação de brincos em pacientes crianças */
    COLOCACAO_BRINCOS_CRIANCA("Colocacao de Brincos - Crianca");

    /** Descrição legível do tipo de serviço */
    private final String descricao;

    /**
     * Construtor do enum que inicializa a descrição associada ao tipo de serviço.
     * 
     * @param descricao Descrição legível do serviço
     */
    TipoServico(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Retorna a descrição do tipo de serviço.
     * 
     * @return Descrição do serviço
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Retorna a descrição quando o enum é convertido para String.
     * 
     * @return Descrição do serviço
     */
    @Override
    public String toString() {
        return descricao;
    }
}
