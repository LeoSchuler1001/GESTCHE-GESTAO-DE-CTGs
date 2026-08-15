package dao;

import java.sql.Statement;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Endereco;
import model.Usuario;

public class UsuarioDAO {
    //ATRIBUTOS
    private ConexaoBanco conexao;
    private EnderecoDAO enderecoDAO;

    //CONSTRUTORES
    public UsuarioDAO(ConexaoBanco conexao) {
        this.conexao = conexao;
        this.enderecoDAO = new EnderecoDAO(conexao);
    }

    public UsuarioDAO() {
    }

    //MÉTODOS
    public void cadastrarUsuario(Usuario usuario) throws SQLException {
        //cria o comando sql
        String sql = "INSERT INTO usuario (cpfUsuario, nomeUsuario, telefoneUsuario, cargoUsuario, senhaHash, respostaSeguranca, ativoUsuario, fk_idEndereco) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        //verifica a conexão com o banco de dados e atribui os valores ao comando sql
        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            //atribui nome e cpf
            stmt.setString(1, usuario.getCpfUsuario());
            stmt.setString(2, usuario.getNomeUsuario());

            //atribui o telefone se estiver preenchido
            if(usuario.getTelefoneUsuario() != null && !usuario.getTelefoneUsuario().isBlank()) {
                stmt.setString(3, usuario.getTelefoneUsuario());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }

            //atribui cargo, senha, resposta de segurança e ativo
            stmt.setString(4, usuario.getCargoUsuario());
            stmt.setString(5, usuario.getSenhaHash());
            stmt.setString(6, usuario.getRespostaSeguranca());
            stmt.setBoolean(7, usuario.isAtivoUsuario());

            //atribui o endereço se estiver preenchido
            if (usuario.getEndereco() != null) {
                stmt.setInt(8, usuario.getEndereco().getIdEndereco());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            //executa o comando sql no banco de dados
            stmt.executeUpdate();

            // Atribui o ID gerado pelo SERIAL de volta ao objeto Usuario
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setIdUsuario(rs.getInt(1));
                }
            }

        }
    }

    public Usuario buscarPorId(int id) throws SQLException {
        //cria o comando sql
        String sql = """
            SELECT usuario.*, endereco.*
            FROM usuario
            LEFT JOIN endereco 
            ON endereco.pk_idEndereco = usuario.fk_idEndereco
            WHERE usuario.pk_idUsuario = ?                    
        """;
        
        //verifica a conexão com o banco de dados
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //atribui o idUsuario à consulta sql
            stmt.setInt(1, id);

            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há algum usuário com esse id
                if (rs.next()) {
                    //retorna o objeto Usuario que foi encontrado
                    return montarObjUsuario(rs);
                }
            }
        }

        //retorna null caso não haja nenhum usuario
        return null;
    }

    //método auxiliar, que vai montar o objeto usuário após a consulta sql
    private Usuario montarObjUsuario(ResultSet rs) throws SQLException {
        //cria o objeto
        Usuario usuario = new Usuario();

        //atribui os valores
        usuario.setIdUsuario(rs.getInt("pk_idUsuario"));
        usuario.setCpfUsuario(rs.getString("cpfUsuario"));
        usuario.setNomeUsuario(rs.getString("nomeUsuario"));
        usuario.setTelefoneUsuario(rs.getString("telefoneUsuario"));
        usuario.setCargoUsuario(rs.getString("cargoUsuario"));
        usuario.setSenhaHash(rs.getString("senhaHash"));
        usuario.setRespostaSeguranca(rs.getString("respostaSeguranca"));
        usuario.setAtivoUsuario(rs.getBoolean("ativoUsuario"));

        // verifica qual é a chave estrangeira do endereço
        int idEndereco = rs.getInt("fk_idEndereco");
        
        //verifica se há algum endereço
        if(!rs.wasNull()) {
            //cria um objeto para armazenar o endereço do usuario
            Endereco endereco = enderecoDAO.buscarPorId(idEndereco);

            //atribui o endereço encontrado ao endereço do usuario
            usuario.setEndereco(endereco);
        }

        //retorna o usuario
        return usuario;
    }
}
