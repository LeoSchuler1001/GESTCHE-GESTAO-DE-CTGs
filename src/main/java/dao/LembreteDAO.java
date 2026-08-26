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
    public List<Lembrete> listarLembretesHoje() throws SQLException {
        String sql = """
                SELECT * FROM lembrete WHERE 
                    CURRENT_DATE BETWEEN dataInicioLembrete AND dataFimLembrete
                    AND (
                        (LOWER(periodicidadeLembrete) IN ('unico', 'único', 'uma vez') 
                        AND dataInicioLembrete = CURRENT_DATE)
                        OR LOWER(periodicidadeLembrete) IN ('diario', 'diário')
                        OR (LOWER(periodicidadeLembrete) = 'semanal' 
                            AND EXTRACT(DOW FROM CURRENT_DATE) = EXTRACT(DOW FROM dataInicioLembrete))
                        OR (LOWER(periodicidadeLembrete) = 'quinzenal' 
                            AND (CURRENT_DATE - dataInicioLembrete) % 14 = 0)
                        OR (LOWER(periodicidadeLembrete) = 'mensal' 
                            AND EXTRACT(DAY FROM CURRENT_DATE) = EXTRACT(DAY FROM dataInicioLembrete))
                        OR (LOWER(periodicidadeLembrete) = 'anual' 
                            AND EXTRACT(DAY FROM CURRENT_DATE) = EXTRACT(DAY FROM dataInicioLembrete)
                            AND EXTRACT(MONTH FROM CURRENT_DATE) = EXTRACT(MONTH FROM dataInicioLembrete))
                    );
                """;
        
        List<Lembrete> listaLembretes = new ArrayList<>();

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                listaLembretes.add(montarObjLembrete(rs));
            }
        }

        return listaLembretes;
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
