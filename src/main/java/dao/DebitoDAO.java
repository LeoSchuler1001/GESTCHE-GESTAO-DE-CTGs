package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.Debito;

public class DebitoDAO {
    //ATRIBUTOS
    private ConexaoBanco conexao;

    //CONSTRUTOR
    public DebitoDAO(ConexaoBanco conexao) {
        this.conexao = conexao;
    }

    public DebitoDAO() {
    }

    //MÉTODOS
    //cadastrar um novo debito
    public void cadastrarDebito(Debito debito) throws SQLException {
        //cria o comando sql
        String sql = "INSERT INTO debito (tipoDebito, valorDebito, vencimentoDebito, dtPgmtDebito, fk_idSocio) VALUES (?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, debito.getTipoDebito());
            stmt.setDouble(2, debito.getValorDebito());
            stmt.setDate(3, new java.sql.Date(debito.getVencimentoDebito().getTime()));
            stmt.setDate(4, new java.sql.Date(debito.getDtPgmtDebito().getTime()));
            stmt.setInt(5, debito.getSocio().getIdSocio());

            //executa o comando sql no banco de dados
            stmt.executeUpdate();

            // Atribui o ID gerado pelo SERIAL de volta ao objeto Usuario
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    debito.setIdDebito(rs.getInt(1));
                }
            }
        }
    }
}
