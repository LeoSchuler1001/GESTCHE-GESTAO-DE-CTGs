package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
            } else {
                stmt.setNull(8, java.sql.Types.INTEGER);
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
             "    c.pk_idConta, c.nomeConta, c.saldo, " +
             "    cat.pk_idCategoria, cat.nomeCategoria, " +
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

    //busca as movimentações receitas de um determinado ano
    public List<Movimentacao> buscarReceitasAno(int ano) throws SQLException {
        List<Movimentacao> receitasAno = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    m.pk_idMovimentacao, m.valorMovimentacao, m.dataMovimentacao, " +
                     "    m.comentarioMovimentacao, m.tipoMovimentacao, " +
                     "    u.pk_idUsuario, u.cpfUsuario, u.nomeUsuario, u.cargoUsuario, " +
                     "    c.pk_idConta, c.nomeConta, c.saldo, " +
                     "    cat.pk_idCategoria, cat.nomeCategoria, " +
                     "    l.pk_idLembrete, l.nomeLembrete, l.dataInicioLembrete, l.periodicidadeLembrete, " +
                     "    l.descricaoLembrete, l.horarioLembrete, l.ativoLembrete " +
                     "FROM movimentacao m " +
                     "LEFT JOIN usuario u ON m.fk_idUsuario = u.pk_idUsuario " +
                     "LEFT JOIN conta c ON m.fk_idConta = c.pk_idConta " +
                     "LEFT JOIN categoria cat ON m.fk_idCategoria = cat.pk_idCategoria " +
                     "LEFT JOIN lembrete l ON m.fk_idLembrete = l.pk_idLembrete " +
                     "WHERE EXTRACT(YEAR FROM m.dataMovimentacao) = ? " +
                     "AND LOWER(m.tipoMovimentacao) = 'receita' " +
                     "ORDER BY m.dataMovimentacao DESC";
        
        //verifica a conexão com o banco de dados
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //atribui o ano ao comando sql
            stmt.setInt(1, ano);

            //cria um result set para armazenr as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    receitasAno.add(montarObjMovimentacao(rs));
                }
            }
        }

        //retorna a lista com as movimentações
        return receitasAno;
    }

    //busca as movimentações despesas de um determinado ano
    public List<Movimentacao> buscarDespesasAno(int ano) throws SQLException {
        List<Movimentacao> despesasAno = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    m.pk_idMovimentacao, m.valorMovimentacao, m.dataMovimentacao, " +
                     "    m.comentarioMovimentacao, m.tipoMovimentacao, " +
                     "    u.pk_idUsuario, u.cpfUsuario, u.nomeUsuario, u.cargoUsuario, " +
                     "    c.pk_idConta, c.nomeConta, c.saldo, " +
                     "    cat.pk_idCategoria, cat.nomeCategoria, " +
                     "    l.pk_idLembrete, l.nomeLembrete, l.dataInicioLembrete, l.periodicidadeLembrete, " +
                     "    l.descricaoLembrete, l.horarioLembrete, l.ativoLembrete " +
                     "FROM movimentacao m " +
                     "LEFT JOIN usuario u ON m.fk_idUsuario = u.pk_idUsuario " +
                     "LEFT JOIN conta c ON m.fk_idConta = c.pk_idConta " +
                     "LEFT JOIN categoria cat ON m.fk_idCategoria = cat.pk_idCategoria " +
                     "LEFT JOIN lembrete l ON m.fk_idLembrete = l.pk_idLembrete " +
                     "WHERE EXTRACT(YEAR FROM m.dataMovimentacao) = ? " +
                     "AND LOWER(m.tipoMovimentacao) = 'despesa' " +
                     "ORDER BY m.dataMovimentacao DESC";
        
        //verifica a conexão com o banco de dados
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //atribui o ano ao comando sql
            stmt.setInt(1, ano);

            //cria um result set para armazenr as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    despesasAno.add(montarObjMovimentacao(rs));
                }
            }
        }

        //retorna a lista com as movimentações
        return despesasAno;
    }

    public List<Movimentacao> buscarReceitasPorDia(LocalDate dataSelecionada) {
        List<Movimentacao> receitas = new ArrayList<>();
        
        String sql = "SELECT " +
                     "    m.pk_idMovimentacao, m.valorMovimentacao, m.dataMovimentacao, " +
                     "    m.comentarioMovimentacao, m.tipoMovimentacao, " +
                     "    u.pk_idUsuario, u.cpfUsuario, u.nomeUsuario, u.cargoUsuario, " +
                     "    c.pk_idConta, c.nomeConta, c.saldo, " +
                     "    cat.pk_idCategoria, cat.nomeCategoria, " +
                     "    l.pk_idLembrete, l.nomeLembrete, l.dataInicioLembrete, l.periodicidadeLembrete, " +
                     "    l.descricaoLembrete, l.horarioLembrete, l.ativoLembrete " +
                     "FROM movimentacao m " +
                     "LEFT JOIN usuario u ON m.fk_idUsuario = u.pk_idUsuario " +
                     "LEFT JOIN conta c ON m.fk_idConta = c.pk_idConta " +
                     "LEFT JOIN categoria cat ON m.fk_idCategoria = cat.pk_idCategoria " +
                     "LEFT JOIN lembrete l ON m.fk_idLembrete = l.pk_idLembrete " +
                     "WHERE m.dataMovimentacao = ? " +
                     "AND LOWER(m.tipoMovimentacao) = 'receita' " +
                     "ORDER BY m.dataMovimentacao DESC";
        try (Connection conn = conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(dataSelecionada));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    receitas.add(montarObjMovimentacao(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return receitas;
    }

    public List<Movimentacao> buscarDespesasPorDia(LocalDate dataSelecionada) {
        List<Movimentacao> despesas = new ArrayList<>();

        String sql = "SELECT " +
                     "    m.pk_idMovimentacao, m.valorMovimentacao, m.dataMovimentacao, " +
                     "    m.comentarioMovimentacao, m.tipoMovimentacao, " +
                     "    u.pk_idUsuario, u.cpfUsuario, u.nomeUsuario, u.cargoUsuario, " +
                     "    c.pk_idConta, c.nomeConta, c.saldo, " +
                     "    cat.pk_idCategoria, cat.nomeCategoria, " +
                     "    l.pk_idLembrete, l.nomeLembrete, l.dataInicioLembrete, l.periodicidadeLembrete, " +
                     "    l.descricaoLembrete, l.horarioLembrete, l.ativoLembrete " +
                     "FROM movimentacao m " +
                     "LEFT JOIN usuario u ON m.fk_idUsuario = u.pk_idUsuario " +
                     "LEFT JOIN conta c ON m.fk_idConta = c.pk_idConta " +
                     "LEFT JOIN categoria cat ON m.fk_idCategoria = cat.pk_idCategoria " +
                     "LEFT JOIN lembrete l ON m.fk_idLembrete = l.pk_idLembrete " +
                     "WHERE m.dataMovimentacao = ? " +
                     "AND LOWER(m.tipoMovimentacao) = 'despesa' " +
                     "ORDER BY m.dataMovimentacao DESC";
        try (Connection conn = conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(dataSelecionada));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    despesas.add(montarObjMovimentacao(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return despesas;
    }

    //método para buscar as receitas por mês
    public List<Movimentacao> buscarReceitasMes(String mesAnoStr) {
        return buscarMovimentacoesPorMes(mesAnoStr, "receita");
    }

    //método para buscar as despesas por mês
    public List<Movimentacao> buscarDespesasMes(String mesAnoStr) {
        return buscarMovimentacoesPorMes(mesAnoStr, "despesa");
    }

    //método auxiliar para buscar as movimentacoes do mês
    private List<Movimentacao> buscarMovimentacoesPorMes(String mesAnoStr, String tipoMovimentacao) {
        List<Movimentacao> movimentacoes = new ArrayList<>();

        try {
            //converte a string para data
            DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("MMMM/yyyy", Locale.of("pt", "BR"));
            YearMonth anoMes = YearMonth.parse(mesAnoStr.toLowerCase(), formatoData);

            //define o primeiro e último dia do mês
            LocalDate inicioMes = anoMes.atDay(1);
            LocalDate fimMes = anoMes.atEndOfMonth();

            //monta o sql utilizando as datas
            String sql = "SELECT " +
                        "    m.pk_idMovimentacao, m.valorMovimentacao, m.dataMovimentacao, " +
                        "    m.comentarioMovimentacao, m.tipoMovimentacao, " +
                        "    u.pk_idUsuario, u.cpfUsuario, u.nomeUsuario, u.cargoUsuario, " +
                        "    c.pk_idConta, c.nomeConta, c.saldo, " +
                        "    cat.pk_idCategoria, cat.nomeCategoria, " +
                        "    l.pk_idLembrete, l.nomeLembrete, l.dataInicioLembrete, l.periodicidadeLembrete, " +
                        "    l.descricaoLembrete, l.horarioLembrete, l.ativoLembrete " +
                        "FROM movimentacao m " +
                        "LEFT JOIN usuario u ON m.fk_idUsuario = u.pk_idUsuario " +
                        "LEFT JOIN conta c ON m.fk_idConta = c.pk_idConta " +
                        "LEFT JOIN categoria cat ON m.fk_idCategoria = cat.pk_idCategoria " +
                        "LEFT JOIN lembrete l ON m.fk_idLembrete = l.pk_idLembrete " +
                        "WHERE m.dataMovimentacao BETWEEN ? AND ? " +
                        "AND LOWER(m.tipoMovimentacao) = ? " +
                        "ORDER BY m.dataMovimentacao DESC";

            try (Connection conn = conexao.getConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setDate(1, Date.valueOf(inicioMes));
                stmt.setDate(2, Date.valueOf(fimMes));
                stmt.setString(3, tipoMovimentacao.toLowerCase());

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        movimentacoes.add(montarObjMovimentacao(rs));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movimentacoes;
    }

    public List<Movimentacao> buscarReceitasSemana(String semana) {
        List<Movimentacao> receitas = new ArrayList<>();
        
        String[] partes = semana.split(" até ");
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        LocalDate dataInicio = LocalDate.parse(partes[0].trim(), formatoData);
        LocalDate dataFim = LocalDate.parse(partes[1].trim(), formatoData);

        String sql = "SELECT " +
                        "    m.pk_idMovimentacao, m.valorMovimentacao, m.dataMovimentacao, " +
                        "    m.comentarioMovimentacao, m.tipoMovimentacao, " +
                        "    u.pk_idUsuario, u.cpfUsuario, u.nomeUsuario, u.cargoUsuario, " +
                        "    c.pk_idConta, c.nomeConta, c.saldo,  " +
                        "    cat.pk_idCategoria, cat.nomeCategoria,  " +
                        "    l.pk_idLembrete, l.nomeLembrete, l.dataInicioLembrete, l.periodicidadeLembrete, " +
                        "    l.descricaoLembrete, l.horarioLembrete, l.ativoLembrete " +
                        "FROM movimentacao m " +
                        "LEFT JOIN usuario u ON m.fk_idUsuario = u.pk_idUsuario " +
                        "LEFT JOIN conta c ON m.fk_idConta = c.pk_idConta " +
                        "LEFT JOIN categoria cat ON m.fk_idCategoria = cat.pk_idCategoria " +
                        "LEFT JOIN lembrete l ON m.fk_idLembrete = l.pk_idLembrete " +
                        "WHERE m.dataMovimentacao BETWEEN ? AND ? " +
                        "AND LOWER(m.tipoMovimentacao) = 'receita' " +
                        "ORDER BY m.dataMovimentacao DESC";

        try (Connection conn = conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, dataInicio);
            stmt.setObject(2, dataFim);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    receitas.add(montarObjMovimentacao(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return receitas;
    }

    public List<Movimentacao> buscarDespesasSemana(String semana) {
        List<Movimentacao> despesas = new ArrayList<>();
        
        String[] partes = semana.split(" até ");
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        LocalDate dataInicio = LocalDate.parse(partes[0].trim(), formatoData);
        LocalDate dataFim = LocalDate.parse(partes[1].trim(), formatoData);

        String sql = "SELECT " +
                        "    m.pk_idMovimentacao, m.valorMovimentacao, m.dataMovimentacao, " +
                        "    m.comentarioMovimentacao, m.tipoMovimentacao, " +
                        "    u.pk_idUsuario, u.cpfUsuario, u.nomeUsuario, u.cargoUsuario, " +
                        "    c.pk_idConta, c.nomeConta, c.saldo, " +
                        "    cat.pk_idCategoria, cat.nomeCategoria, " +
                        "    l.pk_idLembrete, l.nomeLembrete, l.dataInicioLembrete, l.periodicidadeLembrete, " +
                        "    l.descricaoLembrete, l.horarioLembrete, l.ativoLembrete " +
                        "FROM movimentacao m " +
                        "LEFT JOIN usuario u ON m.fk_idUsuario = u.pk_idUsuario " +
                        "LEFT JOIN conta c ON m.fk_idConta = c.pk_idConta " +
                        "LEFT JOIN categoria cat ON m.fk_idCategoria = cat.pk_idCategoria " +
                        "LEFT JOIN lembrete l ON m.fk_idLembrete = l.pk_idLembrete " +
                        "WHERE m.dataMovimentacao BETWEEN ? AND ? " +
                        "AND LOWER(m.tipoMovimentacao) = 'despesa' " +
                        "ORDER BY m.dataMovimentacao DESC";

        try (Connection conn = conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, dataInicio);
            stmt.setObject(2, dataFim);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    despesas.add(montarObjMovimentacao(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return despesas;
    }

    public List<Movimentacao> buscarReceitasPeriodo(String periodo) {
        List<Movimentacao> receitas = new ArrayList<>();
        
        String[] partes = periodo.split(" até ");
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        LocalDate dataInicio = LocalDate.parse(partes[0].trim(), formatoData);
        LocalDate dataFim = LocalDate.parse(partes[1].trim(), formatoData);

        String sql = "SELECT " +
                        "    m.pk_idMovimentacao, m.valorMovimentacao, m.dataMovimentacao, " +
                        "    m.comentarioMovimentacao, m.tipoMovimentacao, " +
                        "    u.pk_idUsuario, u.cpfUsuario, u.nomeUsuario, u.cargoUsuario, " +
                        "    c.pk_idConta, c.nomeConta, c.saldo, " +
                        "    cat.pk_idCategoria, cat.nomeCategoria,  " +
                        "    l.pk_idLembrete, l.nomeLembrete, l.dataInicioLembrete, l.periodicidadeLembrete, " +
                        "    l.descricaoLembrete, l.horarioLembrete, l.ativoLembrete " +
                        "FROM movimentacao m " +
                        "LEFT JOIN usuario u ON m.fk_idUsuario = u.pk_idUsuario " +
                        "LEFT JOIN conta c ON m.fk_idConta = c.pk_idConta " +
                        "LEFT JOIN categoria cat ON m.fk_idCategoria = cat.pk_idCategoria " +
                        "LEFT JOIN lembrete l ON m.fk_idLembrete = l.pk_idLembrete " +
                        "WHERE m.dataMovimentacao BETWEEN ? AND ? " +
                        "AND LOWER(m.tipoMovimentacao) = 'receita' " +
                        "ORDER BY m.dataMovimentacao DESC";

        try (Connection conn = conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, dataInicio);
            stmt.setObject(2, dataFim);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    receitas.add(montarObjMovimentacao(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return receitas;
    }

    public List<Movimentacao> buscarDespesasPeriodo(String periodo) {
        List<Movimentacao> despesas = new ArrayList<>();
        
        String[] partes = periodo.split(" até ");
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        LocalDate dataInicio = LocalDate.parse(partes[0].trim(), formatoData);
        LocalDate dataFim = LocalDate.parse(partes[1].trim(), formatoData);

        String sql = "SELECT " +
                        "    m.pk_idMovimentacao, m.valorMovimentacao, m.dataMovimentacao, " +
                        "    m.comentarioMovimentacao, m.tipoMovimentacao, " +
                        "    u.pk_idUsuario, u.cpfUsuario, u.nomeUsuario, u.cargoUsuario, " +
                        "    c.pk_idConta, c.nomeConta, c.saldo, " +
                        "    cat.pk_idCategoria, cat.nomeCategoria, " +
                        "    l.pk_idLembrete, l.nomeLembrete, l.dataInicioLembrete, l.periodicidadeLembrete, " +
                        "    l.descricaoLembrete, l.horarioLembrete, l.ativoLembrete " +
                        "FROM movimentacao m " +
                        "LEFT JOIN usuario u ON m.fk_idUsuario = u.pk_idUsuario " +
                        "LEFT JOIN conta c ON m.fk_idConta = c.pk_idConta " +
                        "LEFT JOIN categoria cat ON m.fk_idCategoria = cat.pk_idCategoria " +
                        "LEFT JOIN lembrete l ON m.fk_idLembrete = l.pk_idLembrete " +
                        "WHERE m.dataMovimentacao BETWEEN ? AND ? " +
                        "AND LOWER(m.tipoMovimentacao) = 'despesa' " +
                        "ORDER BY m.dataMovimentacao DESC";

        try (Connection conn = conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, dataInicio);
            stmt.setObject(2, dataFim);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    despesas.add(montarObjMovimentacao(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return despesas;
    }

    //busca todas as movimentacoes de um determinado periodo
    public List<Movimentacao> buscarPorPeriodo(Date dataInicio, Date dataFim) throws SQLException {
        List<Movimentacao> listaMovimentacoes = new ArrayList<>();
        
        String sql = "SELECT " +
                    "    m.pk_idMovimentacao, m.valorMovimentacao, m.dataMovimentacao, " +
                    "    m.comentarioMovimentacao, m.tipoMovimentacao, " +
                    "    u.pk_idUsuario, u.cpfUsuario, u.nomeUsuario, u.cargoUsuario, " +
                    "    c.pk_idConta, c.nomeConta, c.saldo, " +
                    "    cat.pk_idCategoria, cat.nomeCategoria, " +
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

    //retorna uma lista com todos os anos que possuem movimentações cadastradas
    public List<Integer> buscarAnosComMovimentacoes() throws SQLException {
        List<Integer> anos = new ArrayList<>();

        String sql = "SELECT DISTINCT EXTRACT(YEAR FROM dataMovimentacao) AS ano " +
                     "FROM movimentacao " +
                     "WHERE dataMovimentacao IS NOT NULL " +
                     "ORDER BY ano DESC";

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                anos.add(rs.getInt("ano"));
            }
        }
        
        return anos;
    }

    //retorna uma lista com os meses que possuem movimentação
    public List<String> buscarMesesComMovimentacao() {
        List<String> meses = new ArrayList<>();
        
        // Agrupa de forma segura pelo ano e mês da data, filtrando apenas o usuário ID 20 (se aplicável)
        // Se quiser para todos os usuários, remova o "AND fk_idusuario = 20"
        String sql = "SELECT DISTINCT EXTRACT(YEAR FROM datamovimentacao) AS ano, " +
                    "                EXTRACT(MONTH FROM datamovimentacao) AS mes " +
                    "FROM movimentacao " +
                    "WHERE datamovimentacao IS NOT NULL " +
                    "AND fk_idusuario = 20 " + 
                    "ORDER BY ano DESC, mes DESC";

        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("MMMM/yyyy", Locale.of("pt", "BR"));

        try (Connection conn = conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int ano = rs.getInt("ano");
                int mes = rs.getInt("mes");
                
                // Cria um LocalDate usando o dia 1 do ano e mês exatos retornados pelo banco
                LocalDate data = LocalDate.of(ano, mes, 1);
                
                String mesFormatado = data.format(formatoData);
                meses.add(mesFormatado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return meses;
    }

    //retorna uma lista com as semanas que possuem movimentações
    public List<String> buscarSemanasComMovimentacao() {
        List<String> semanas = new ArrayList<>();
        
        //agrupa por semana, iniciando na segunda feira
        String sql = "SELECT DISTINCT DATE_TRUNC('week', datamovimentacao)::date AS inicio_semana, " +
                    "                (DATE_TRUNC('week', datamovimentacao) + INTERVAL '6 days')::date AS fim_semana " +
                    "FROM movimentacao " +
                    "WHERE datamovimentacao IS NOT NULL " +
                    "AND fk_idusuario = 20 " + 
                    "ORDER BY inicio_semana DESC";

        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.of("pt", "BR"));

        try (Connection conn = conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                LocalDate inicio = rs.getObject("inicio_semana", LocalDate.class);
                LocalDate fim = rs.getObject("fim_semana", LocalDate.class);
                
                if (inicio != null && fim != null) {
                    String semanaFormatada = inicio.format(formatoData) + " até " + fim.format(formatoData);
                    semanas.add(semanaFormatada);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return semanas;
    }

    //retorna uma lista com todos os dias com movimentações cadastradas
    public List<LocalDate> buscarDiasComMovimentacoes() {
        List<LocalDate> diasComMovimentacao = new ArrayList<>();
        
        String sql = "SELECT DISTINCT dataMovimentacao FROM movimentacao ORDER BY dataMovimentacao DESC";

        try (Connection conn = conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                //converte para LocalDate
                java.sql.Date sqlDate = rs.getDate("dataMovimentacao");
                if (sqlDate != null) {
                    LocalDate data = sqlDate.toLocalDate();
                    diasComMovimentacao.add(data);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return diasComMovimentacao;
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
        }

        //atribui os valores ao objeto conta, caso houver
        if(rs.getInt("pk_idConta") != 0 || rs.getObject("pk_idConta") != null) {
            conta = new Conta();

            conta.setIdConta(rs.getInt("pk_idConta"));
            conta.setNomeConta(rs.getString("nomeConta"));
            conta.setSaldo(rs.getDouble("saldo"));
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