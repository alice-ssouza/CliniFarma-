package DAO;

/**
 * Interface genérica que define as operações básicas de
 * persistência de dados (DAO), permitindo inserir, atualizar,
 * excluir, pesquisar e buscar objetos em um repositório de dados.
 *
 * @param <T> tipo do objeto que será manipulado pela interface
 *
 * @author Alice
 */
public interface OperacoesDAO<T> {

    /**
     * Insere um novo objeto no repositório de dados.
     *
     * @param objeto objeto que será inserido
     * @return {@code true} se a inserção for realizada com sucesso;
     *         {@code false} caso contrário (ex.: chave duplicada)
     */
    boolean inserir(T objeto);

    /**
     * Atualiza os dados de um objeto já existente no repositório.
     *
     * @param objeto objeto contendo os dados atualizados
     * @return {@code true} se a atualização for realizada com sucesso;
     *         {@code false} caso o objeto não seja encontrado
     */
    boolean atualizar(T objeto);

    /**
     * Remove um objeto do repositório de dados.
     *
     * @param objeto objeto que será removido
     * @return {@code true} se a exclusão for realizada com sucesso;
     *         {@code false} caso contrário
     */
    boolean excluir(T objeto);

    /**
     * Verifica se determinado objeto existe no repositório de dados.
     *
     * @param objeto objeto que será pesquisado
     * @return {@code true} se o objeto existir;
     *         {@code false} caso contrário
     */
    boolean pesquisar(T objeto);

    /**
     * Busca um objeto no repositório utilizando um identificador.
     *
     * @param identificador valor utilizado para localizar o objeto
     * @return objeto encontrado ou {@code null} caso não exista
     */
    T buscar(Object identificador);
}
