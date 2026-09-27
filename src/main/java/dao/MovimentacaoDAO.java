package dao;

import java.sql.SQLException;
import model.Movimentacao;

public class MovimentacaoDAO {
    //ATRIBUTOS
    private ConexaoBanco conexao;

    //CONSTRUTORES
    public MovimentacaoDAO(ConexaoBanco conexao) {
        this.conexao = conexao;
    }

    public MovimentacaoDAO() {
    }

    //MÉTODOS
    public void cadastrarMovimentacao(Movimentacao movimentacao) throws SQLException {
        
    }
}