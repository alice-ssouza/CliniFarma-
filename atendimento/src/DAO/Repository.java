package DAO;

/**
 * Contrato genérico do padrão Repository para acesso a entidades persistidas.
 * A camada de negócio depende desta abstração, não dos detalhes SQL.
 * @param <T> tipo da entidade
 * @param <ID> tipo do identificador
 */
public interface Repository<T, ID> {
    void inserir(T entidade);
    boolean atualizar(T entidade);
    boolean excluir(T entidade);
    boolean pesquisar(T entidade);
    T buscar(ID identificador);
}
