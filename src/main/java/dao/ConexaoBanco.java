package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {
    //MÉTODOS
    //cria a conexão com o banco de dados
    public Connection getConexao() {
        try {
            Class.forName("org.postgresql.Driver"); 
            return DriverManager.getConnection(CredenciaisBancoDados.getUrl(), CredenciaisBancoDados.getUser(), CredenciaisBancoDados.getPassword());
        } catch (ClassNotFoundException e) {
            System.err.println("Não foi possível conectar ao banco de dados!");
            return null;
        } catch (SQLException e) {
            System.err.println("Erro ao conectar com o banco de dados: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}