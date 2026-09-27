package dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Categoria;
import model.Conta;
import model.Lembrete;
import model.Movimentacao;
import model.Usuario;

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
    //cadastrar uma nova movimentação
    public void cadastrarMovimentacao(Movimentacao movimentacao) throws SQLException {
        //cria o comando sql
        String sql = "INSERT INTO movimentacao (valorMovimentacao, dataMovimentacao, comentarioMovimentacao, tipoMovimentacao, fk_idUsuario, fk_idCategoria, fk_idConta, fk_idLembrete) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, movimentacao.getValorMovimentacao());
            stmt.setDate(2, new java.sql.Date(movimentacao.getDataMovimentacao().getTime()));
            stmt.setString(3, movimentacao.getComentarioMovimentacao());
            stmt.setString(4, movimentacao.getTipoMovimentacao());

            stmt.setInt(5, movimentacao.getUsuario().getIdUsuario());
            stmt.setInt(6, movimentacao.getCategoria().getIdCategoria());
            stmt.setInt(7, movimentacao.getConta().getIdConta());

            if(movimentacao.getLembrete() != null) {
                stmt.setInt(8, movimentacao.getUsuario().getIdUsuario());
            }

            //executa o comando sql no banco de dados
            stmt.executeUpdate();

            // Atribui o ID gerado pelo SERIAL de volta ao objeto Usuario
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    movimentacao.setIdMovimentacao(rs.getInt(1));
                }
            }
        }
    }

    //busca uma movimentação passando o seu id
    public Movimentacao buscarPorId(int id) throws SQLException {
        //cria o comando sql
        String sql = "SELECT " +
             "    m.pk_idMovimentacao, m.valorMovimentacao, m.dataMovimentacao, " +
             "    m.comentarioMovimentacao, m.tipoMovimentacao, " +
             "    u.pk_idUsuario, u.cpfUsuario, u.nomeUsuario, u.cargoUsuario, " +
             "    c.pk_idConta, c.nomeConta, c.corConta, c.iconeConta, " +
             "    cat.pk_idCategoria, cat.nomeCategoria, cat.corCategoria, cat.iconeCategoria, " +
             "    l.pk_idLembrete, l.nomeLembrete, l.dataInicioLembrete, l.periodicidadeLembrete, " +
             "    l.descricaoLembrete, l.horarioLembrete, l.ativoLembrete " +
             "FROM movimentacao m " +
             "LEFT JOIN usuario u ON m.fk_idUsuario = u.pk_idUsuario " +
             "LEFT JOIN conta c ON m.fk_idConta = c.pk_idConta " +
             "LEFT JOIN categoria cat ON m.fk_idCategoria = cat.pk_idCategoria " +
             "LEFT JOIN lembrete l ON m.fk_idLembrete = l.pk_idLembrete " +
             "WHERE m.pk_idMovimentacao = ?";
        
        //verifica a conexão com o banco de dados
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //atribui o idDebito à consulta sql
            stmt.setInt(1, id);

            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há alguma movimentacao com esse id
                if (rs.next()) {
                    //retorna o objeto movimentacao que foi encontrado
                    return montarObjMovimentacao(rs);
                }
            }
        }

        //retorna null caso não haja nenhuma movimentacao
        return null;
    }

    //busca todas as movimentacoes de um determinado periodo
    public List<Movimentacao> buscarPorPeriodo(Date dataInicio, Date dataFim) throws SQLException {
        List<Movimentacao> listaMovimentacoes = new ArrayList<>();
        
        String sql = "SELECT " +
                    "    m.pk_idMovimentacao, m.valorMovimentacao, m.dataMovimentacao, " +
                    "    m.comentarioMovimentacao, m.tipoMovimentacao, " +
                    "    u.pk_idUsuario, u.cpfUsuario, u.nomeUsuario, u.cargoUsuario, " +
                    "    c.pk_idConta, c.nomeConta, c.corConta, c.iconeConta, " +
                    "    cat.pk_idCategoria, cat.nomeCategoria, cat.corCategoria, cat.iconeCategoria, " +
                    "    l.pk_idLembrete, l.nomeLembrete, l.dataInicioLembrete, l.periodicidadeLembrete, " +
                    "    l.descricaoLembrete, l.horarioLembrete, l.ativoLembrete " +
                    "FROM movimentacao m " +
                    "LEFT JOIN usuario u ON m.fk_idUsuario = u.pk_idUsuario " +
                    "LEFT JOIN conta c ON m.fk_idConta = c.pk_idConta " +
                    "LEFT JOIN categoria cat ON m.fk_idCategoria = cat.pk_idCategoria " +
                    "LEFT JOIN lembrete l ON m.fk_idLembrete = l.pk_idLembrete " +
                    "WHERE m.dataMovimentacao BETWEEN ? AND ? " +
                    "ORDER BY m.dataMovimentacao ASC";
        
        //verifica a conexão com o banco de dados
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //atribui as datas de inicio e fim ao comando sql
            stmt.setDate(1, dataInicio);
            stmt.setDate(2, dataFim);

            //cria um result set para armazenr as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listaMovimentacoes.add(montarObjMovimentacao(rs));
                }
            }
        }

        //retorna a lista com as movimentações
        return listaMovimentacoes;
    }

    //atualiza uma movimentação
    public void atualizarMovimentacao(Movimentacao movimentacao) throws SQLException {
        String sql = "UPDATE movimentacao SET valorMovimentacao = ?, dataMovimentacao = ?, comentarioMovimentacao = ?, tipoMovimentacao = ?, fk_idUsuario = ?, fk_idCategoria = ?, fk_idConta = ?, fk_idLembrete = ? WHERE pk_idMovimentacao = ?";
        
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setDouble(1, movimentacao.getValorMovimentacao());
            stmt.setDate(2, movimentacao.getDataMovimentacao());
            stmt.setString(3, movimentacao.getComentarioMovimentacao());
            stmt.setString(4, movimentacao.getTipoMovimentacao());

            //verifica se há algum usuário relacionado
            if(movimentacao.getUsuario() != null) {
                stmt.setInt(5, movimentacao.getUsuario().getIdUsuario());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            //verifica se há alguma categoria relacionada
            if(movimentacao.getCategoria() != null) {
                stmt.setInt(6, movimentacao.getCategoria().getIdCategoria());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            //verifica se há alguma conta relacionada
            if(movimentacao.getConta() != null) {
                stmt.setInt(7, movimentacao.getConta().getIdConta());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }

            //verifica se há algum lembrete relacionado
            if(movimentacao.getLembrete() != null) {
                stmt.setInt(8, movimentacao.getLembrete().getIdLembrete());
            } else {
                stmt.setNull(8, java.sql.Types.INTEGER);
            }

            stmt.setInt(9, movimentacao.getIdMovimentacao());

            //executa o comando sql
            stmt.executeUpdate();
        }
    }

    //exclui uma movimentação
    public void excluirMovimentacao(Movimentacao movimentacao) throws SQLException {
        String sql = "DELETE FROM movimentacao WHERE pk_idMovimentacao = ?";

        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, movimentacao.getIdMovimentacao());
            stmt.executeUpdate();
        }
    }

    //método auxiliar, que vai montar o objeto departamento após a consulta sql
    private Movimentacao montarObjMovimentacao(ResultSet rs) throws SQLException {
        //cria o movimentacao
        Movimentacao movimentacao = new Movimentacao();

        //atribui os valores
        movimentacao.setIdMovimentacao(rs.getInt("pk_idMovimentacao"));
        movimentacao.setValorMovimentacao(rs.getDouble("valorMovimentacao"));
        movimentacao.setDataMovimentacao(rs.getDate("dataMovimentacao"));
        movimentacao.setComentarioMovimentacao(rs.getString("comentarioMovimentacao"));
        movimentacao.setTipoMovimentacao(rs.getString("tipoMovimentacao"));
        
        Usuario usuario = null;
        Categoria categoria = null;
        Conta conta = null;
        Lembrete lembrete = null;

        //atribui os valores ao objeto usuario, caso houver
        if(rs.getInt("pk_idUsuario") != 0 || rs.getObject("pk_idUsuario") != null) {
            usuario = new Usuario();

            usuario.setIdUsuario(rs.getInt("pk_idUsuario"));
            usuario.setCpfUsuario(rs.getString("cpfUsuario"));
            usuario.setNomeUsuario(rs.getString("nomeUsuario"));
            usuario.setCargoUsuario(rs.getString("cargoUsuario"));
        }

        //atribui os valores ao objeto categoria, caso houver
        if(rs.getInt("pk_idCategoria") != 0 || rs.getObject("pk_idCategoria") != null) {
            categoria = new Categoria();

            categoria.setIdCategoria(rs.getInt("pk_idCategoria"));
            categoria.setNomeCategoria(rs.getString("nomeCategoria"));
            categoria.setCorCategoria(rs.getString("corCategoria"));
            categoria.setIconeCategoria(rs.getString("iconeCategoria"));
        }

        //atribui os valores ao objeto conta, caso houver
        if(rs.getInt("pk_idConta") != 0 || rs.getObject("pk_idConta") != null) {
            conta = new Conta();

            conta.setIdConta(rs.getInt("pk_idConta"));
            conta.setNomeConta(rs.getString("nomeConta"));
            conta.setCorConta(rs.getString("corConta"));
            conta.setIconeConta(rs.getString("iconeConta"));
        }

        //atribui os valores ao objeto lembrete, caso houver
        if(rs.getInt("pk_idLembrete") != 0 || rs.getObject("pk_idLembrete") != null) {
            lembrete = new Lembrete();

            lembrete.setIdLembrete(rs.getInt("pk_idLembrete"));
            lembrete.setNomeLembrete(rs.getString("nomeLembrete"));
            lembrete.setDataInicioLembrete(rs.getDate("dataInicioLembrete"));
            lembrete.setPeriodicidadeLembrete(rs.getString("periodicidadeLembrete"));
            lembrete.setDescricaoLembrete(rs.getString("descricaoLembrete"));
            lembrete.setHorarioLembrete(rs.getTime("horarioLembrete"));
            lembrete.setAtivoLembrete(rs.getBoolean("ativoLembrete"));
        }

        movimentacao.setUsuario(usuario);
        movimentacao.setCategoria(categoria);
        movimentacao.setConta(conta);
        movimentacao.setLembrete(lembrete);

        //retorna a movimentacao
        return movimentacao;
    }
}