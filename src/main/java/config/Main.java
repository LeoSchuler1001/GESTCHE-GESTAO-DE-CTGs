package config;

import java.sql.Connection;

public class Main {
    //MÉTODOS
    public static void main(String[] args) {
        //verifica se a conexão foi feita com sucesso, e mostra uma mensagem no console
        Connection conexao = ConexaoBanco.getConexao();
        if (conexao != null) {
            System.out.println("Conexão com o Supabase realizada com sucesso!");
        } else {
            System.out.println("Falha ao conectar com o banco.");
        }
    }
}