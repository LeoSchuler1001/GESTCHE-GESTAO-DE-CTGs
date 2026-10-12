package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.dto.RecebimentoPendenteDTO;

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

    public List<RecebimentoPendenteDTO> listarPendentes() {
        String sql = "SELECT rp.pk_idrecebimento, rp.datarecebimento, rp.statusrecebimento, " +
                    "       d.pk_iddebito, d.tipodebito, d.valordebito, d.vencimentodebito, " +
                    "       u.pk_idusuario, u.nomeusuario, " +
                    "       s.nomesocio " +
                    "FROM recebimento_pendente rp " +
                    "JOIN debito d ON rp.fk_iddebito = d.pk_iddebito " +
                    "JOIN usuario u ON rp.fk_idsecretario = u.pk_idusuario " +
                    "JOIN socio s ON d.fk_idSocio = s.pk_idsocio " +
                    "WHERE rp.statusrecebimento = 'PENDENTE' " +
                    "ORDER BY rp.datarecebimento ASC";

        List<RecebimentoPendenteDTO> listaPendentes = new ArrayList<>();

        try (PreparedStatement pstmt = conexao.getConexao().prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                RecebimentoPendenteDTO dto = new RecebimentoPendenteDTO();
                
                dto.setIdRecebimento(rs.getInt("pk_idrecebimento"));
                dto.setDataRecebimento(rs.getTimestamp("datarecebimento").toLocalDateTime());
                dto.setStatusRecebimento(rs.getString("statusrecebimento"));
                dto.setIdDebito(rs.getInt("pk_iddebito"));
                dto.setTipoDebito(rs.getString("tipodebito"));
                dto.setValorDebito(rs.getDouble("valordebito"));
                
                if (rs.getDate("vencimentodebito") != null) {
                    dto.setVencimentoDebito(rs.getDate("vencimentodebito").toLocalDate());
                }
                
                dto.setIdSecretario(rs.getInt("pk_idusuario"));
                dto.setNomeSecretario(rs.getString("nomeusuario"));
                
                // Atribuindo o nome do sócio recuperado do banco
                dto.setNomeSocio(rs.getString("nomesocio"));

                listaPendentes.add(dto);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar pagamentos pendentes com sócio: " + e.getMessage());
            e.printStackTrace();
        }

        return listaPendentes;
    }

    public boolean atualizarStatusRecebimento(int idRecebimento, String novoStatus, int idTesoureiro, Integer idMovimentacao) {
        String sql = "UPDATE recebimento_pendente " +
                     "SET statusRecebimento = ?, " +
                     "    fk_idTesoureiro = ?, " +
                     "    dataConfirmacao = ?, " +
                     "    fk_idMovimentacao = ? " +
                     "WHERE pk_idRecebimento = ?";

        try (PreparedStatement pstmt = conexao.getConexao().prepareStatement(sql)) {
            
            pstmt.setString(1, novoStatus);
            pstmt.setInt(2, idTesoureiro);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            
            //se o pagamento for rejeitado, a movimentação vai ser nula
            if (idMovimentacao != null) {
                pstmt.setInt(4, idMovimentacao);
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            pstmt.setInt(5, idRecebimento);

            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status do recebimento pendente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
