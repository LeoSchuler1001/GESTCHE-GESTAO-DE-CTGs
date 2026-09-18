package dao;

import java.sql.Statement;
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
                SELECT *
                FROM lembrete
                WHERE fk_idUsuario = ?
                AND ativoLembrete = TRUE
                AND dataInicioLembrete <= (NOW() AT TIME ZONE 'America/Sao_Paulo')::date
                AND (
                        (periodicidadeLembrete = 'UMA VEZ' AND dataInicioLembrete = (NOW() AT TIME ZONE 'America/Sao_Paulo')::date)
                    OR (periodicidadeLembrete = 'DIÁRIO')
                    OR (periodicidadeLembrete = 'SEMANAL' AND ((NOW() AT TIME ZONE 'America/Sao_Paulo')::date - dataInicioLembrete) % 7 = 0)
                    OR (periodicidadeLembrete = 'QUINZENAL' AND ((NOW() AT TIME ZONE 'America/Sao_Paulo')::date - dataInicioLembrete) % 15 = 0)
                    OR (periodicidadeLembrete = 'MENSAL' AND EXTRACT(DAY FROM (NOW() AT TIME ZONE 'America/Sao_Paulo')::date) = EXTRACT(DAY FROM dataInicioLembrete))
                    OR (periodicidadeLembrete = 'ANUAL' AND EXTRACT(MONTH FROM (NOW() AT TIME ZONE 'America/Sao_Paulo')::date) = EXTRACT(MONTH FROM dataInicioLembrete) 
                                                        AND EXTRACT(DAY FROM (NOW() AT TIME ZONE 'America/Sao_Paulo')::date) = EXTRACT(DAY FROM dataInicioLembrete))
                );
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
        String sql = "SELECT * FROM lembrete WHERE fk_idUsuario = ? ORDER BY ativoLembrete ASC";

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

    //inativa um lembrete
    public void inativarLembrete(Lembrete lembrete) throws SQLException {
        String sql = "UPDATE lembrete SET ativoLembrete = FALSE WHERE pk_idLembrete = ?";

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, lembrete.getIdLembrete());
            stmt.executeUpdate();
        }
    }

    //ativa um lembrete
    public void ativarLembrete(Lembrete lembrete) throws SQLException {
        String sql = "UPDATE lembrete SET ativoLembrete = TRUE WHERE pk_idLembrete = ?";

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, lembrete.getIdLembrete());
            stmt.executeUpdate();
        }
    }

    //cadastra um lembrete
    public void cadastrarLembrete(Lembrete lembrete) throws SQLException {
        String sql = "INSERT INTO lembrete (nomeLembrete, dataInicioLembrete, periodicidadeLembrete, descricaoLembrete, horarioLembrete, fk_idUsuario) VALUES (?, ?, ?, ?, ?, ?)";
    
        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, lembrete.getNomeLembrete());
            stmt.setDate(2, new java.sql.Date(lembrete.getDataInicioLembrete().getTime()));
            stmt.setString(3, lembrete.getPeriodicidadeLembrete());
            stmt.setString(4, lembrete.getDescricaoLembrete());
            stmt.setTime(5, lembrete.getHorarioLembrete());
            stmt.setInt(6, lembrete.getUsuario().getIdUsuario());


            //executa o comando sql no banco de dados
            stmt.executeUpdate();

            // Atribui o ID gerado pelo SERIAL de volta ao objeto Usuario
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    lembrete.setIdLembrete(rs.getInt(1));
                }
            }
        }
    }

    //atualiza um lembrete
    public void atualizarLembrete(Lembrete lembrete) throws SQLException {
        String sql = "UPDATE lembrete SET nomeLembrete = ?, dataInicioLembrete = ?, periodicidadeLembrete = ?, descricaoLembrete = ?, horarioLembrete = ?, fk_idUsuario = ? WHERE pk_idLembrete = ?";
    
        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, lembrete.getNomeLembrete());
            stmt.setDate(2, new java.sql.Date(lembrete.getDataInicioLembrete().getTime()));
            stmt.setString(3, lembrete.getPeriodicidadeLembrete());
            stmt.setString(4, lembrete.getDescricaoLembrete());
            stmt.setTime(5, lembrete.getHorarioLembrete());
            stmt.setInt(6, lembrete.getUsuario().getIdUsuario());

            stmt.setInt(7, lembrete.getIdLembrete());
            
            //executa o comando sql no banco de dados
            stmt.executeUpdate();

            // Atribui o ID gerado pelo SERIAL de volta ao objeto Usuario
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    lembrete.setIdLembrete(rs.getInt(1));
                }
            }
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
        lembrete.setPeriodicidadeLembrete(rs.getString("periodicidadeLembrete"));
        lembrete.setDescricaoLembrete(rs.getString("descricaoLembrete"));
        lembrete.setHorarioLembrete(rs.getTime("horarioLembrete"));
        lembrete.setAtivoLembrete(rs.getBoolean("ativoLembrete"));

        //verifica qual é a chave estrangeira do usuário e atribui o objeto ao socio
        int idUsuario = rs.getInt("fk_idUsuario");
        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        lembrete.setUsuario(usuario);

        //retorna o usuario
        return lembrete;
    }
}
