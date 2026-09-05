package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Endereco;
import model.Socio;
import model.Usuario;

public class Socio_DepartamentoDAO {
    //ATRIBUTOS
    private ConexaoBanco conexao;
    private EnderecoDAO enderecoDAO;
    private UsuarioDAO usuarioDAO;

    //CONSTRUTOR
    public Socio_DepartamentoDAO(ConexaoBanco conexao) {
        this.conexao = conexao;
        this.enderecoDAO = new EnderecoDAO(conexao);
        this.usuarioDAO = new UsuarioDAO(conexao);
    }

    public Socio_DepartamentoDAO() {
    }

    //MÉTODOS
    //busca todos os departamentos vinculados a um sócio passando o seu id
    public List<String> buscarDepartamentosSocio(int idSocio) throws SQLException {
        String sql = "SELECT departamento.nomeDepartamento FROM departamento, socio_departamento WHERE departamento.pk_idDepartamento = socio_departamento.fk_idDepartamento AND socio_departamento.fk_idSocio = ?";

        List<String> listaDepartamentosSocio = new ArrayList<>();

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, idSocio);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                listaDepartamentosSocio.add(rs.getString("nomeDepartamento"));
            }
        }

        return listaDepartamentosSocio;
    }

    // Vincula um sócio a um departamento
    public void vincularSocioDepartamento(int idSocio, int idDepartamento) throws SQLException {
        String sql = "INSERT INTO socio_departamento (fk_idSocio, fk_idDepartamento) VALUES (?, ?)";

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, idSocio);
            stmt.setInt(2, idDepartamento);
            stmt.executeUpdate();
        }
    }

    //busca todos os sócios de um departamento pelo seu id
    public List<Socio> buscarSociosDepartamento(int id) throws SQLException {
        //cria o comando sql
        String sql = "SELECT socio.* FROM socio, socio_departamento WHERE socio.pk_idSocio = socio_departamento.fk_idSocio AND socio_departamento.fk_idDepartamento = ?";
        
        List<Socio> listaSociosDepartamento = new ArrayList<>();

        //verifica a conexão com o banco de dados
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //atribui o idDepartamento à consulta sql
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                listaSociosDepartamento.add(montarObjSocio(rs));
            }
        }
        
        return listaSociosDepartamento;
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
