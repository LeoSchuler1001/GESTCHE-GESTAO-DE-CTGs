package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Dependente;
import model.Endereco;
import model.Socio;
import model.Usuario;

public class DependenteDAO {
    //ATRIBUTOS
    private ConexaoBanco conexao;
    private SocioDAO socioDAO;
    
    //CONSTRUTORES
    public DependenteDAO(ConexaoBanco conexao) {
        this.conexao = conexao;
        this.socioDAO = new SocioDAO(conexao);
    }

    public DependenteDAO() {
    }

    //MÉTODOS
    //cadastrar um novo dependente
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

    //busca todos os dependentes de um socio, passando o id dele 
    public List<Dependente> buscarPorIdSocio(int id) throws SQLException {
        //cria o comando sql
        String sql = "SELECT * FROM dependente WHERE fk_idSocio = ?";
        
        List<Dependente> listaDependentes = new ArrayList<>();

        //verifica a conexão com o banco de dados
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //atribui o idUsuario à consulta sql
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                listaDependentes.add(montarObjDependente(rs));
            }
        }
        
        return listaDependentes;
    }

    //busca um dependente passando o seu id
    public Dependente buscarPorId(int id) throws SQLException {
        //cria o comando sql
        String sql = "SELECT * FROM dependente WHERE pk_idDependente = ?";
        
        //verifica a conexão com o banco de dados
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //atribui o idUsuario à consulta sql
            stmt.setInt(1, id);

            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há algum dependente com esse id
                if (rs.next()) {
                    //retorna o objeto Dependente que foi encontrado
                    return montarObjDependente(rs);
                }
            }
        }

        //retorna null caso não haja nenhum dependente
        return null;
    }

    //busca um dependente passando o cpf
    public Dependente buscaDependentePorCpf (String cpf) throws SQLException {
        //cria o comando sql
        String sql = "SELECT * FROM dependente WHERE cpfDependente = ?";

        //verifica a conexão com o banco de dados
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //atribui o idUsuario à consulta sql
            stmt.setString(1, cpf);

            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há algum dependente com esse cpf
                if (rs.next()) {
                    //retorna o objeto Usuario que foi encontrado
                    return montarObjDependente(rs);
                }
            }
        }
        
        //retorna null caso não haja nenhum dependente
        return null;
    }

    //lista todos os dependentes ativos
    public List<Dependente> listarTodosAtivos() throws SQLException {
        String sql = "SELECT dependente.* FROM dependente, socio WHERE socio.pk_idSocio = dependente.fk_idSocio AND socio.ativoSocio = TRUE ORDER BY dependente.nomeDependente ASC";

        List<Dependente> listaDependentesAtivos = new ArrayList<>();

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                listaDependentesAtivos.add(montarObjDependente(rs));
            }
        }
        return listaDependentesAtivos;
    }

    //atualiza um dependente
    public void atualizarDependente(Dependente dependente) throws SQLException {
        String sql = "UPDATE dependente SET nomeDependente = ?, cpfDependente = ?, dataNascDependente = ?, fk_idSocio = ? WHERE pk_idDependente = ?";
        
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setString(1, dependente.getNomeDependente());
            stmt.setString(2, dependente.getCpfDependente());
            stmt.setDate(3, new java.sql.Date(dependente.getDataNascDependente().getTime()));
            stmt.setInt(4, dependente.getSocio().getIdSocio());
            stmt.setInt(5, dependente.getIdDependente());

            //executa o comando sql
            stmt.executeUpdate();
        }
    }

    //exclui um dependente
    public void excluirDependente(Dependente dependente) throws SQLException {
        String sql = "DELETE FROM dependente WHERE pk_idDependente = ?";

        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, dependente.getIdDependente());
            stmt.executeUpdate();
        }
    }

    //método auxiliar, que vai montar o objeto dependente após a consulta sql
    private Dependente montarObjDependente(ResultSet rs) throws SQLException {
        //cria o objeto
        Dependente dependente = new Dependente();

        //atribui os valores
        dependente.setIdDependente(rs.getInt("pk_idDependente"));
        dependente.setNomeDependente(rs.getString("nomeDependente"));
        dependente.setCpfDependente(rs.getString("cpfDependente"));
        dependente.setDataNascDependente(rs.getDate("dataNascSocio"));

        //verifica qual a chave estrangeira do sócio
        int idSocio = rs.getInt("fk_idSocio");
        Socio socio = socioDAO.buscarPorId(idSocio);
        dependente.setSocio(socio);

        //retorna o usuario
        return dependente;
    }










}
