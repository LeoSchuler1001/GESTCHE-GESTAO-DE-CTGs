package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Lembrete;
import model.Usuario;

public class LembreteDAO {
    //ATRIBUTOS
    private ConexaoBanco conexao;
    private UsuarioDAO usuarioDAO;
    
    //CONTRUTOR
    public LembreteDAO(ConexaoBanco conexao) {
        this.conexao = conexao;
        this.usuarioDAO = new UsuarioDAO(conexao);
    }

    //MÉTODOS
    //busca todos os lembretes que devem aparecer no dia de hoje
    public List<Lembrete> listarLembretesHoje(int idUsuario) throws SQLException {
        String sql = """
                SELECT * FROM lembrete 
                WHERE CURRENT_DATE BETWEEN dataInicioLembrete AND dataFimLembrete
                AND (
                        (periodicidadeLembrete = 'UMA VEZ' AND dataInicioLembrete = CURRENT_DATE)
                        OR (periodicidadeLembrete = 'DIÁRIO')
                        OR (periodicidadeLembrete = 'SEMANAL' AND EXTRACT(DOW FROM CURRENT_DATE) = EXTRACT(DOW FROM dataInicioLembrete))
                        OR (periodicidadeLembrete = 'QUINZENAL' AND (CURRENT_DATE - dataInicioLembrete) % 14 = 0)
                        OR (periodicidadeLembrete = 'MENSAL' AND EXTRACT(DAY FROM CURRENT_DATE) = EXTRACT(DAY FROM dataInicioLembrete))
                        OR (periodicidadeLembrete = 'ANUAL' AND EXTRACT(DAY FROM CURRENT_DATE) = EXTRACT(DAY FROM dataInicioLembrete)
                                                    AND EXTRACT(MONTH FROM CURRENT_DATE) = EXTRACT(MONTH FROM dataInicioLembrete))
                )
                AND fk_idUsuario = ?
                AND pagoLembrete = false;
        """;
        
        List<Lembrete> listaLembretes = new ArrayList<>();

        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            
            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há algum dependente com esse id
                while (rs.next()) {
                    listaLembretes.add(montarObjLembrete(rs));
                }
            }
        }

        return listaLembretes;
    }

    public List<Lembrete> listarLembretesUsuario(Usuario usuario) throws SQLException {
        String sql = "SELECT * FROM lembrete WHERE fk_idUsuario = ? ORDER BY pagoLembrete ASC";

        List<Lembrete> listaLembretes = new ArrayList<>();

        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, usuario.getIdUsuario());
            
            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há algum dependente com esse id
                while (rs.next()) {
                    listaLembretes.add(montarObjLembrete(rs));
                }
            }
        }

        return listaLembretes;
    }

    //exclui um lembrete
    public void excluirLembrete(Lembrete lembrete) throws SQLException {
        String sql = "DELETE FROM lembrete WHERE pk_idLembrete = ?";

        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, lembrete.getIdLembrete());
            stmt.executeUpdate();
        }
    }

    //marca como concluido um lembrete
    public  void marcarConcluido(Lembrete lembrete) throws SQLException {
        String sql = "UPDATE lembrete SET pagoLembrete = TRUE WHERE pk_idLembrete = ?";

        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, lembrete.getIdLembrete());
            stmt.executeUpdate();
        }
    }

    //método auxiliar, que vai montar o objeto lembrete após a consulta sql
    private Lembrete montarObjLembrete(ResultSet rs) throws SQLException {
        //cria o objeto
        Lembrete lembrete = new Lembrete();

        //atribui os valores
        lembrete.setIdLembrete(rs.getInt("pk_idLembrete"));
        lembrete.setNomeLembrete(rs.getString("nomeLembrete"));
        lembrete.setDataInicioLembrete(rs.getDate("dataInicioLembrete"));
        lembrete.setDataFimLembrete(rs.getDate("dataFimLembrete"));
        lembrete.setPeriodicidadeLembrete(rs.getString("periodicidadeLembrete"));
        lembrete.setDescricaoLembrete(rs.getString("descricaoLembrete"));
        lembrete.setHorarioLembrete(rs.getTime("horarioLembrete"));
        lembrete.setPagoLembrete(rs.getBoolean("pagoLembrete"));

        //verifica qual é a chave estrangeira do usuário e atribui o objeto ao socio
        int idUsuario = rs.getInt("fk_idUsuario");
        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        lembrete.setUsuario(usuario);

        //retorna o usuario
        return lembrete;
    }
}
