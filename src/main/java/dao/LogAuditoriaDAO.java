package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.LogAuditoria;
import model.Usuario;

public class LogAuditoriaDAO {
    //ATRIBUTOS
    private ConexaoBanco conexao;

    //CONSTRUTORES
    public LogAuditoriaDAO(ConexaoBanco conexao) {
        this.conexao = conexao;
    }

    public LogAuditoriaDAO() {
    }

    //MÉTODOS
    public void cadastrarLog(LogAuditoria logAuditoria, Usuario usuario) throws SQLException {
        //cria o comando sql
        String sql = "INSERT INTO logAuditoria (dataHoraLog, descricaoLog, fk_idUsuario, nomeUsuario) VALUES (?, ?. ?, ?)";

        //verifica a conexão com o banco de dados e atribui os valores ao comando sql
        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            //atribui os valores ao comando sql
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(logAuditoria.getDataHoraLog()));
            stmt.setString(2, logAuditoria.getDescricaoLog());
            stmt.setInt(3, logAuditoria.getUsuario().getIdUsuario());
            stmt.setString(4, logAuditoria.getNomeUsuario());

            //executa o comando sql no banco de dados
            stmt.executeUpdate();

            // Atribui o ID gerado pelo SERIAL de volta ao objeto Usuario
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    logAuditoria.setIdLog(rs.getInt(1));
                }
            }
        }
    }
}
