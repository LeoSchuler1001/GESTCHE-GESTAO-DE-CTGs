package dao;

import util.ConfigBanco;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {
    //cria uma conexão única para utilização
    private static Connection conexaoUnica;

    public Connection getConexao() throws SQLException {
        //verifica se há uma conexão, senão abre uma nova
        if (conexaoUnica == null || conexaoUnica.isClosed()) {
            String url = ConfigBanco.getUrl();
            String usuario = ConfigBanco.getUser();
            String senha = ConfigBanco.getPassword();

            conexaoUnica = DriverManager.getConnection(url, usuario, senha);
        }
        return conexaoUnica;
    }
}