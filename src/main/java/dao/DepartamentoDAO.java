package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Departamento;
import model.Dependente;
import model.Socio;

public class DepartamentoDAO {
    //ATRIBUTOS
    private ConexaoBanco conexao;
    private SocioDAO socioDAO;
    
    //CONSTRUTORES
    public DepartamentoDAO(ConexaoBanco conexao, SocioDAO socioDAO) {
        this.conexao = conexao;
        this.socioDAO = socioDAO;
    }

    public DepartamentoDAO() {
    }

    //MÉTODOS
    //cadastrar um novo departamento
    public void cadastrarDepartamento(Departamento departamento) throws SQLException {
        //cria o comando sql
        String sql = "INSERT INTO departamento (nomeDepartamento, descricaoDepartamento) VALUES (?, ?)";

        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, departamento.getNomeDepartamento());
            stmt.setString(2, departamento.getDescricaoDepartamento());

            //executa o comando sql no banco de dados
            stmt.executeUpdate();

            // Atribui o ID gerado pelo SERIAL de volta ao objeto Usuario
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    departamento.setIdDepartamento(rs.getInt(1));
                }
            }
        }
    }

    //busca um departamento passando o seu id
    public Departamento buscarPorId(int id) throws SQLException {
        //cria o comando sql
        String sql = "SELECT * FROM departamento WHERE pk_idDepartamento = ?";
        
        //verifica a conexão com o banco de dados
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //atribui o idUsuario à consulta sql
            stmt.setInt(1, id);

            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há algum dependente com esse id
                if (rs.next()) {
                    //retorna o objeto departamento que foi encontrado
                    return montarObjDepartamento(rs);
                }
            }
        }

        //retorna null caso não haja nenhum departamento
        return null;
    }

    //lista todos os departamentos
    public List<Departamento> listarTodos() throws SQLException {
        String sql = "SELECT * FROM departamento ORDER BY nomeDepartamento ASC";

        List<Departamento> listaDepartamentos = new ArrayList<>();

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                listaDepartamentos.add(montarObjDepartamento(rs));
            }
        }
        return listaDepartamentos;
    }

    //atualiza um departamento
    public void atualirDepartamento(Departamento departamento) throws SQLException {
        String sql = "UPDATE departamento SET nomeDepartamento = ?, descricaoDepartamento = ? WHERE pk_idDependente = ?";
        
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setString(1, departamento.getNomeDepartamento());
            stmt.setString(2, departamento.getDescricaoDepartamento());
            stmt.setInt(3, departamento.getIdDepartamento());
            
            //executa o comando sql
            stmt.executeUpdate();
        }
    }

    //método auxiliar, que vai montar o objeto departamento após a consulta sql
    private Departamento montarObjDepartamento(ResultSet rs) throws SQLException {
        //cria o objeto
        Departamento departamento = new Departamento();

        //atribui os valores
        departamento.setIdDepartamento(rs.getInt("pk_idDepartamento"));
        departamento.setNomeDepartamento(rs.getString("nomeDepartamento"));
        departamento.setDescricaoDepartamento(rs.getString("descricaoDepartamento"));

        //retorna o usuario
        return departamento;
    }


    
}
