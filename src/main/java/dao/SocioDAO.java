package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

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

    //método auxiliar, que vai montar o objeto usuário após a consulta sql
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
