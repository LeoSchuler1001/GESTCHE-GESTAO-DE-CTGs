package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Conta;

public class ContaDAO {
    //ATRIBUTOS
    private ConexaoBanco conexao;

    //CONSTRUTORES
    public ContaDAO(ConexaoBanco conexao) {
        this.conexao = conexao;
    }

    public ContaDAO() {
    }

    //MÉTODOS
    //cadastrar uma nova conta
    public void cadastrarConta(Conta conta) throws SQLException {
        //cria o comando sql
        String sql = "INSERT INTO conta (nomeConta, saldo, corConta, iconeConta) VALUES (?, ?, ?, ?)";

        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, conta.getNomeConta());
            stmt.setDouble(2, conta.getSaldo());
            stmt.setString(3, conta.getCorConta());
            stmt.setString(4, conta.getIconeConta());

            // Atribui o ID gerado pelo SERIAL de volta ao objeto conta
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    conta.setIdConta(rs.getInt(1));
                }
            }
        }
    }

    //busca uma conta passando o seu id
    public Conta buscarPorId(int id) throws SQLException {
        //cria o comando sql
        String sql = "SELECT * FROM conta WHERE pk_idConta = ?";
        
        //verifica a conexão com o banco de dados
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //atribui o id à consulta sql
            stmt.setInt(1, id);

            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há alguma conta com esse id
                if (rs.next()) {
                    //retorna o objeto conta que foi encontrado
                    return montarObjConta(rs);
                }
            }
        }

        //retorna null caso não haja nenhuma conta
        return null;
    }

    //lista todas as contas
    public List<Conta> listarContas() throws SQLException {
        String sql = "SELECT * FROM conta";

        List<Conta> listaContas = new ArrayList<>();

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listaContas.add(montarObjConta(rs));
                }
            }
        }
        return listaContas;
    }

    //atualiza uma conta
    public void atualizarConta(Conta conta) throws SQLException {
        String sql = "UPDATE conta SET nomeConta = ?, saldo = ?, corConta = ?, iconeConta = ?  WHERE pk_idConta = ?";
        
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setString(1, conta.getNomeConta());
            stmt.setDouble(2, conta.getSaldo());
            stmt.setString(3, conta.getCorConta());
            stmt.setString(4, conta.getIconeConta());
            stmt.setInt(5, conta.getIdConta());
            
            //executa o comando sql
            stmt.executeUpdate();
        }
    }

    //exclui uma conta
    public void excluirConta(Conta conta) throws SQLException {
        String sql = "DELETE FROM conta WHERE pk_idConta = ?";

        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, conta.getIdConta());
            stmt.executeUpdate();
        }
    }

    public Double saldoTotal() throws SQLException {
        String sql = "SELECT SUM(saldo) as saldoTotal FROM conta";

        Double saldoTotal = 0.0;

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    saldoTotal = rs.getDouble("saldoTotal");
                }
            }
        }
        return saldoTotal;
    }

    //método auxiliar, que vai montar o objeto categoria após a consulta sql
    private Conta montarObjConta(ResultSet rs) throws SQLException {
        //cria o objeto
        Conta conta = new Conta();

        //atribui os valores
        conta.setIdConta(rs.getInt("pk_idConta"));
        conta.setSaldo(rs.getDouble("saldo"));
        conta.setNomeConta(rs.getString("nomeConta"));
        conta.setCorConta(rs.getString("corConta"));
        conta.setIconeConta(rs.getString("iconeConta"));

        //retorna a conta
        return conta;
    }
}