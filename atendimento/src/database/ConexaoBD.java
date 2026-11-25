package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável por estabelecer a conexão com o PostgreSQL.
 * Aplica o padrão criacional Singleton: existe uma única conexão compartilhada
 * por toda a aplicação, criada sob demanda e reutilizada pelos DAOs.
 *
 * <p>Os DAOs NÃO devem fechar a conexão devolvida por {@link #conectar()}
 * (apenas Statements e ResultSets). O fechamento é feito uma única vez,
 * ao encerrar o sistema, por {@link #fechar()}.</p>
 *
 * <p>Lê as variáveis de ambiente do Docker (DB_HOST, DB_PORT, DB_NAME,
 * DB_USER, DB_PASS), com valores padrão para execução local no NetBeans.</p>
 *
 * @author Alice
 */
public class ConexaoBD {

    private static Connection instancia = null;

    private ConexaoBD() {
    }

    /**
     * Retorna a conexão única, criando-a (ou recriando-a, se estiver fechada).
     *
     * @return conexão com o PostgreSQL
     * @throws SQLException caso ocorra um erro na conexão
     */
    public static synchronized Connection conectar() throws SQLException {
        if (instancia == null || instancia.isClosed()) {
            String host = System.getenv().getOrDefault("DB_HOST", "localhost");
            String port = System.getenv().getOrDefault("DB_PORT", "5432");
            String db   = System.getenv().getOrDefault("DB_NAME", "meubanco");
            String user = System.getenv().getOrDefault("DB_USER", "postgres");
            String pass = System.getenv().getOrDefault("DB_PASS", "12345");

            String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, db);
            instancia = DriverManager.getConnection(url, user, pass);
        }
        return instancia;
    }

    /**
     * Fecha a conexão única. Deve ser chamado ao encerrar o sistema.
     */
    public static synchronized void fechar() {
        try {
            if (instancia != null && !instancia.isClosed()) {
                instancia.close();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao fechar a conexao: " + e.getMessage());
        } finally {
            instancia = null;
        }
    }
}
