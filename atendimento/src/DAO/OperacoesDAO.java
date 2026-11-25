package DAO;

/**
 * Contrato DAO legado, especializado como Repository com identificador genérico Object.
 * Mantido para compatibilidade com as chamadas existentes do sistema.
 * @param <T> tipo do objeto persistido
 */
public interface OperacoesDAO<T> extends Repository<T, Object> {
}
