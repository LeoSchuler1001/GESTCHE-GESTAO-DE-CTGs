package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
        String sql = "INSERT INTO logAuditoria (dataHoraLog, descricaoLog, fk_idUsuario, nomeUsuario) VALUES (?, ?, ?, ?)";

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

    public List<LogAuditoria> listarLogs() throws SQLException {
        //cria o comando sql
        String sql = "SELECT * FROM logAuditoria ORDER BY dataHoraLog DESC";

        List<LogAuditoria> listaLogs = new ArrayList<>();

        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listaLogs.add(montarObjLogs(rs));
                }
            }
        }

        return listaLogs;
    }

    //método auxiliar, que vai montar o objeto logAuditoria após a consulta sql
    private LogAuditoria montarObjLogs(ResultSet rs) throws SQLException {
        //cria o objeto
        LogAuditoria logAuditoria = new LogAuditoria();

        //atribui os valores
        logAuditoria.setIdLog(rs.getInt("pk_idLog"));
        logAuditoria.setDataHoraLog(rs.getTimestamp("dataHoraLog").toLocalDateTime());
        logAuditoria.setDescricaoLog(rs.getString("descricaoLog"));
        logAuditoria.setNomeUsuario(rs.getString("nomeUsuario"));

        //retorna o usuario
        return logAuditoria;
    }
}
