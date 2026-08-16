package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.Dependente;

public class DependenteDAO {
    //ATRIBUTOS
    private ConexaoBanco conexao;
    private SocioDAO socioDAO;
    
    //CONSTRUTORES
    public DependenteDAO(ConexaoBanco conexao, SocioDAO socioDAO) {
        this.conexao = conexao;
        this.socioDAO = socioDAO;
    }

    public DependenteDAO() {
    }

    //MÉTODOS
    public void cadastrarDependente(Dependente dependente) throws SQLException {
        //cria o comando sql
        String sql = "INSERT INTO dependente (nomeDependente, cpfDependente, dataNascDependente, fk_idSocio) VALUES (?, ?, ?, ?)";

        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, dependente.getNomeDependente());
            stmt.setString(2, dependente.getCpfDependente());
            stmt.setDate(3, new java.sql.Date(dependente.getDataNascDependente().getTime()));
            stmt.setInt(4, dependente.getSocio().getIdSocio());

            //executa o comando sql no banco de dados
            stmt.executeUpdate();

            // Atribui o ID gerado pelo SERIAL de volta ao objeto Usuario
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    dependente.setIdDependente(rs.getInt(1));
                }
            }
        }
    }
}
