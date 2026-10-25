package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Categoria;

public class CategoriaDAO {
    //ATRIBUTOS
    private ConexaoBanco conexao;

    //CONSTRUTORES
    public CategoriaDAO(ConexaoBanco conexao) {
        this.conexao = conexao;
    }

    public CategoriaDAO() {
    }

    //MÉTODOS
    //cadastrar uma nova categoria
    public void cadastrarCategoria(Categoria categoria) throws SQLException {
        //cria o comando sql
        String sql = "INSERT INTO categoria (nomeCategoria, tipoCategoria) VALUES (?, ?)";

        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, categoria.getNomeCategoria());
            stmt.setString(2, categoria.getTipoCategoria());
            

            stmt.executeUpdate();

            //atribui o ID gerado pelo SERIAL de volta ao objeto categoria
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    categoria.setIdCategoria(rs.getInt(1));
                }
            }
        }
    }

    //busca uma categoria passando o seu id
    public Categoria buscarPorId(int id) throws SQLException {
        //cria o comando sql
        String sql = "SELECT * FROM categoria WHERE pk_idCategoria = ?";
        
        //verifica a conexão com o banco de dados
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //atribui o id à consulta sql
            stmt.setInt(1, id);

            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há alguma categoria com esse id
                if (rs.next()) {
                    //retorna o objeto conta que foi encontrado
                    return montarObjCategoria(rs);
                }
            }
        }

        //retorna null caso não haja nenhuma categoria
        return null;
    }

    public Categoria buscarPorNome(String nome) throws SQLException {
        //cria o comando sql
        String sql = "SELECT * FROM categoria WHERE nomeCategoria = ?";
        
        //verifica a conexão com o banco de dados
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //atribui o id à consulta sql
            stmt.setString(1, nome);

            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há alguma categoria com esse id
                if (rs.next()) {
                    //retorna o objeto conta que foi encontrado
                    return montarObjCategoria(rs);
                }
            }
        }

        //retorna null caso não haja nenhuma categoria
        return null;
    }

    // Lista apenas as categorias de Receita
    public List<Categoria> listarCategoriasReceita() throws SQLException {
        String sql = "SELECT * FROM categoria WHERE tipoCategoria = ?";

        List<Categoria> listaReceitas = new ArrayList<>();

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            // Define o parâmetro da consulta como "RECEITA"
            stmt.setString(1, "Receita");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listaReceitas.add(montarObjCategoria(rs));
                }
            }
        }
        return listaReceitas;
    }

    // Lista apenas as categorias de Despesa
    public List<Categoria> listarCategoriasDespesa() throws SQLException {
        String sql = "SELECT * FROM categoria WHERE tipoCategoria = ?";

        List<Categoria> listaDespesas = new ArrayList<>();

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            // Define o parâmetro da consulta como "DESPESA"
            stmt.setString(1, "Despesa");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listaDespesas.add(montarObjCategoria(rs));
                }
            }
        }
        return listaDespesas;
    }

    //atualiza uma categoria
    public void atualizarCategoria(Categoria categoria) throws SQLException {
        String sql = "UPDATE categoria SET nomeCategoria = ?, tipoCategoria = ?  WHERE pk_idCategoria = ?";
        
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setString(1, categoria.getNomeCategoria());
            stmt.setString(2, categoria.getTipoCategoria());
            stmt.setInt(3, categoria.getIdCategoria());
            
            //executa o comando sql
            stmt.executeUpdate();
        }
    }

    //exclui uma categoria
    public void excluirCategoria(Categoria categoria) throws SQLException {
        String sql = "DELETE FROM categoria WHERE pk_idCategoria = ?";

        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, categoria.getIdCategoria());
            stmt.executeUpdate();
        }
    }

    public boolean categoriaPossuiMovimentacao(int idCategoria) {
        String sql = "SELECT EXISTS (SELECT 1 FROM movimentacao WHERE fk_idCategoria = ?)";
        
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, idCategoria);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar movimentações da categoria: " + e.getMessage(), e);
        }
        
        return false;
    }

    //método auxiliar, que vai montar o objeto categoria após a consulta sql
    private Categoria montarObjCategoria(ResultSet rs) throws SQLException {
        //cria o objeto
        Categoria categoria = new Categoria();

        //atribui os valores
        categoria.setIdCategoria(rs.getInt("pk_idCategoria"));
        categoria.setNomeCategoria(rs.getString("nomeCategoria"));
        categoria.setTipoCategoria(rs.getString("tipoCategoria"));

        //retorna a conta
        return categoria;
    }
}