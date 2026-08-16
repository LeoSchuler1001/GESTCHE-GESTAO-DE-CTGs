package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.Departamento;

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








    
}
