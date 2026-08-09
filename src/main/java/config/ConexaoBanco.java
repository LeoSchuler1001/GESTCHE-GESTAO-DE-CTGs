package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {
    //ATRIBUTOS
    private static final String URL = "jdbc:postgresql://aws-0-sa-east-1.pooler.supabase.com:6543/postgres?prepareThreshold=0";
    private static final String USER = "postgres.rqolgvzsbbbdxwxdcwzg";
    private static final String PASSWORD = "Tentenovamente1001@";

    //MÉTODOS
    //cria a conexão com o banco de dados
    public static Connection getConexao() {
        try {
            Class.forName("org.postgresql.Driver"); 
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver do PostgreSQL não encontrado no classpath!");
            return null;
        } catch (SQLException e) {
            System.err.println("Erro ao conectar com o Supabase: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}