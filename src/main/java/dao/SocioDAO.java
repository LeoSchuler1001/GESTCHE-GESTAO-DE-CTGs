package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import model.Endereco;
import model.Socio;
import model.Usuario;

public class SocioDAO {
    //ATRIBUTOS
    private ConexaoBanco conexao;
    private EnderecoDAO enderecoDAO;
    private UsuarioDAO usuarioDAO;

    //CONSTRUTORES
    public SocioDAO(ConexaoBanco conexao) {
        this.conexao = conexao;
        this.enderecoDAO = new EnderecoDAO(conexao);
        this.usuarioDAO = new UsuarioDAO(conexao);
    }

    public SocioDAO() {
    }

    //MÉTODOS
    //cadastra um novo sócio no banco de dados
    public void cadastrarSocio(Socio socio) throws SQLException {
        String sql = "INSERT INTO socio (cpfSocio, nomeSocio, telefoneSocio, dataNascSocio, emailSocio, ativoSocio, fk_idEndereco, fk_idUsuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, socio.getCpfSocio());
            stmt.setString(2, socio.getNomeSocio());

            //atribui o telefone se estiver preenchido
            if(socio.getTelefoneSocio() != null && !socio.getTelefoneSocio().isBlank()) {
                stmt.setString(3, socio.getTelefoneSocio());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }

            stmt.setDate(4, new java.sql.Date(socio.getDataNascSocio().getTime()));
            stmt.setString(5, socio.getEmailSocio());
            stmt.setBoolean(6, socio.isAtivoSocio());
            
            //atribui o endereço se estiver preenchido
            if (socio.getEndereco() != null) {
                stmt.setInt(7, socio.getEndereco().getIdEndereco());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.setInt(8, socio.getUsuario().getIdUsuario());

            //executa o comando sql no banco de dados
            stmt.executeUpdate();

            // Atribui o ID gerado pelo SERIAL de volta ao objeto Usuario
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    socio.setIdSocio(rs.getInt(1));
                }
            }

        }
    }

    //busca um sócio passando o deu id
    public Socio buscarPorId(int id) throws SQLException {
        //cria o comando sql
        String sql = "SELECT * FROM socio WHERE pk_idSocio = ?";
        
        //verifica a conexão com o banco de dados
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //atribui o idUsuario à consulta sql
            stmt.setInt(1, id);

            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há algum socio com esse id
                if (rs.next()) {
                    //retorna o objeto socio que foi encontrado
                    return montarObjSocio(rs);
                }
            }
        }

        //retorna null caso não haja nenhum usuario
        return null;
    }

    //busca um socio passando o cpf
    public Socio buscaSocioPorCPF (String cpf) throws SQLException {
        //cria o comando sql
        String sql = "SELECT * FROM socio WHERE cpfSocio = ?";

        //verifica a conexão com o banco de dados
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //atribui o idSocio à consulta sql
            stmt.setString(1, cpf);

            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há algum sócio com esse cpf
                if (rs.next()) {
                    //retorna o objeto Usuario que foi encontrado
                    return montarObjSocio(rs);
                }
            }
        }
        
        //retorna null caso não haja nenhum usuario
        return null;
    }

    //lista todos os sócios ativos
    public List<Socio> listarTodosAtivos() throws SQLException {
        String sql = "SELECT * FROM socio WHERE ativoSocio = TRUE ORDER BY nomeSocio ASC";

        List<Socio> listaSocios = new ArrayList<>();

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                listaSocios.add(montarObjSocio(rs));
            }
        }
        return listaSocios;
    }

    //lista todos os sócios - ativos ou inativos
    public List<Socio> listarAtivosInativos() throws SQLException {
        String sql = "SELECT * FROM socio ORDER BY nomeSocio ASC";

        List<Socio> listaSociosAtivosInativos = new ArrayList<>();

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                listaSociosAtivosInativos.add(montarObjSocio(rs));
            }
        }
        return listaSociosAtivosInativos;
    }

    //atualiza um sócio
    public void atualizarSocio(Socio socio) throws SQLException {
        String sql = "UPDATE socio SET cpfSocio = ?, nomeSocio = ?, telefoneSocio = ?, dataNascSocio = ?, emailSocio = ?, ativoSocio = ?, fk_idEndereco = ?, fk_idUsuario = ? WHERE pk_idSocio = ?";
        
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setString(1, socio.getCpfSocio());
            stmt.setString(2, socio.getNomeSocio());

            //verifica se o socio tem telefone cadastrado
            if (socio.getTelefoneSocio() != null && !socio.getTelefoneSocio().isBlank()) {
                stmt.setString(3, socio.getTelefoneSocio());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }

            stmt.setDate(4, new java.sql.Date(socio.getDataNascSocio().getTime()));
            stmt.setString(5, socio.getEmailSocio());
            stmt.setBoolean(6, socio.isAtivoSocio());

            // verifica se o socio tem endereço cadastrado
            if (socio.getEndereco() != null) {
                stmt.setInt(7, socio.getEndereco().getIdEndereco());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            //verifica qual é a chave estrangeira do usuario que cadastrou
            int idUsuario = socio.getUsuario().getIdUsuario();
            stmt.setInt(8, idUsuario);

            stmt.setInt(9, socio.getIdSocio());

            //executa o comando sql
            stmt.executeUpdate();
        }
    }

    //desativa um socio
    public void desativarSocio(int id) throws SQLException {
        String sql = "UPDATE socio SET ativoSocio = FALSE WHERE pk_idSocio = ?";

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }


    //método auxiliar, que vai montar o objeto sócio após a consulta sql
    private Socio montarObjSocio(ResultSet rs) throws SQLException {
        //cria o objeto
        Socio socio = new Socio();

        //atribui os valores
        socio.setIdSocio(rs.getInt("pk_idSocio"));
        socio.setCpfSocio(rs.getString("cpfSocio"));
        socio.setNomeSocio(rs.getString("nomeSocio"));
        socio.setTelefoneSocio(rs.getString("telefoneSocio"));
        socio.setDataNascSocio(rs.getDate("dataNascSocio"));
        socio.setEmailSocio(rs.getString("emailSocio"));
        socio.setAtivoSocio(rs.getBoolean("ativoSocio"));


        // verifica qual é a chave estrangeira do endereço
        int idEndereco = rs.getInt("fk_idEndereco");
        
        //verifica se há algum endereço
        if(!rs.wasNull()) {
            //cria um objeto para armazenar o endereço do usuario
            Endereco endereco = enderecoDAO.buscarPorId(idEndereco);

            //atribui o endereço encontrado ao endereço do usuario
            socio.setEndereco(endereco);
        }

        //verifica qual é a chave estrangeira do usuário e atribui o objeto ao socio
        int idUsuario = rs.getInt("fk_idUsuario");
        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        socio.setUsuario(usuario);

        //retorna o usuario
        return socio;
    }















}
