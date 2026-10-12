package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Recebimento_pendenteDAO {
    //ATRIBUTOS
    private ConexaoBanco conexao;

    //CONSTRUTORES
    public Recebimento_pendenteDAO(ConexaoBanco conexao) {
        this.conexao = conexao;
    }

    public Recebimento_pendenteDAO() {
    }

    //MÉTODOS
    public boolean registrarPagamentoPendente(int idDebito, int idSecretario) {
        String sql = "INSERT INTO recebimento_pendente (fk_idDebito, fk_idSecretario) VALUES (?, ?)";

        try (PreparedStatement pstmt = conexao.getConexao().prepareStatement(sql)) {
            
            pstmt.setInt(1, idDebito);
            pstmt.setInt(2, idSecretario);

            int linhasAfetadas = pstmt.executeUpdate();
            
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao registrar pagamento pendente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean debitoPossuiRecebimentoPendente(int idDebito) {
        String sql = "SELECT COUNT(*) FROM recebimento_pendente WHERE fk_idDebito = ? AND statusRecebimento = 'PENDENTE'";

        try (PreparedStatement pstmt = conexao.getConexao().prepareStatement(sql)) {
            
            pstmt.setInt(1, idDebito);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int quantidade = rs.getInt(1);
                    return quantidade > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar débito pendente: " + e.getMessage());
            e.printStackTrace();
        }

        return false; 
    }
}
