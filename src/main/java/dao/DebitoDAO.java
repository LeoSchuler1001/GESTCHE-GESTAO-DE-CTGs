package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Debito;
import model.Departamento;
import model.Socio;

public class DebitoDAO {
    //ATRIBUTOS
    private ConexaoBanco conexao;
    private SocioDAO socioDAO;

    //CONSTRUTOR
    public DebitoDAO(ConexaoBanco conexao) {
        this.conexao = conexao;
        this.socioDAO = new SocioDAO(conexao);
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

    //busca um débito passando o seu id
    public Debito buscarPorId(int id) throws SQLException {
        //cria o comando sql
        String sql = "SELECT * FROM debito WHERE pk_idDebito = ?";
        
        //verifica a conexão com o banco de dados
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //atribui o idDebito à consulta sql
            stmt.setInt(1, id);

            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há algum debito com esse id
                if (rs.next()) {
                    //retorna o objeto debito que foi encontrado
                    return montarObjDebito(rs);
                }
            }
        }

        //retorna null caso não haja nenhum departamento
        return null;
    }

    //lista todos os debitos em aberto de um sócio
    public List<Debito> listarDebitosAbertosSocio(int id) throws SQLException {
        String sql = "SELECT DEBITO.* FROM debito, socio WHERE debito.fk_idSocio = socio.pk_idSocio AND debito.dtPgmtDebito IS NULL";

        List<Debito> listaDebitos = new ArrayList<>();

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                listaDebitos.add(montarObjDebito(rs));
            }
        }
        return listaDebitos;
    }

    //método auxiliar, que vai montar o objeto departamento após a consulta sql
    private Debito montarObjDebito(ResultSet rs) throws SQLException {
        //cria o objeto
        Debito debito = new Debito();

        //atribui os valores
        debito.setIdDebito(rs.getInt("pk_idDebito"));
        debito.setTipoDebito(rs.getString("tipoDebito"));
        debito.setValorDebito(rs.getDouble("valorDebito"));
        debito.setVencimentoDebito(rs.getDate("vencimentoDebito"));
        debito.setDtPgmtDebito(rs.getDate("dtPgmtDebito"));
        
        //verifica qual a chave estrangeira do sócio
        int idSocio = rs.getInt("fk_idSocio");
        Socio socio = socioDAO.buscarPorId(idSocio);
        debito.setSocio(socio);

        //retorna o usuario
        return debito;
    }
}
