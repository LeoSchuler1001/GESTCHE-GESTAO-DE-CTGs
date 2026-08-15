package dao;

import model.Endereco;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnderecoDAO {
    //ATRIBUTOS
    private ConexaoBanco conexao;

    //CONSTRUTORES
    public EnderecoDAO(ConexaoBanco conexao) {
        this.conexao = conexao;
    }

    //MÉTODOS
    public void cadastrarEndereco(Endereco endereco) throws SQLException {
        //cria o comando sql
        String sql = "INSERT INTO endereco (rua, numero, cep, bairro, cidade, estado) VALUES (?, ?, ?, ?, ?, ?)";

        //preenche as informações no comando sql
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, endereco.getRua());
            stmt.setInt(2, endereco.getNumero());
            stmt.setString(3, endereco.getCep());
            stmt.setString(4, endereco.getBairro());
            stmt.setString(5, endereco.getCidade());
            stmt.setString(6, endereco.getEstado());

            //executa o comando sql
            stmt.executeUpdate();

            //verifica se o banco criou o endereço e atribui o valor do id para o objeto
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    endereco.setIdEndereco(rs.getInt(1));
                }
            }
        }
    }

    public Endereco buscarPorId(int idEndereco) throws SQLException {
        //cria o comando sql
        String sql = "SELECT * FROM endereco WHERE pk_idEndereco = ?";

        //verifica a conexão com o banco de dados e atribui os valores ao comando sql
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, idEndereco);

            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Endereco endereco = new Endereco();
                    endereco.setIdEndereco(rs.getInt("pk_idEndereco"));
                    endereco.setRua(rs.getString("rua"));
                    endereco.setNumero(rs.getInt("numero"));
                    endereco.setCep(rs.getString("cep"));
                    endereco.setBairro(rs.getString("bairro"));
                    endereco.setCidade(rs.getString("cidade"));
                    endereco.setEstado(rs.getString("estado"));

                    //retorna o objeto endereço
                    return endereco;
                }
            }
        }

        //retorna null se não tiver um endereço com esse id
        return null;
    }
}