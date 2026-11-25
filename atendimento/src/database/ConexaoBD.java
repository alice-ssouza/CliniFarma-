package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fábrica de conexões JDBC com PostgreSQL.
 * Cada chamada cria uma conexão independente, que deve ser fechada pelo
 * consumidor com try-with-resources. Evita compartilhar Connection entre
 * operações/threads e impede que uma operação feche a conexão de outra.
 */
public final class ConexaoBD {
    private ConexaoBD() { }

    public static Connection conectar() throws SQLException {
        String host = System.getenv().getOrDefault("DB_HOST", "localhost");
        String port = System.getenv().getOrDefault("DB_PORT", "5432");
        String db = System.getenv().getOrDefault("DB_NAME", "meubanco");
        String user = System.getenv().getOrDefault("DB_USER", "postgres");
        String pass = System.getenv().getOrDefault("DB_PASS", "12345");
        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, db);
        return DriverManager.getConnection(url, user, pass);
    }
}
