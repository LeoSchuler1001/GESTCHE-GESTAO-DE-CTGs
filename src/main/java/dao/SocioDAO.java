package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import model.Endereco;
import model.Socio;
import model.Usuario;
import model.dto.SocioResumoDTO;

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
    //conta a quantidade de sócios
    public int contarSocios() throws SQLException {
        String sql = "SELECT count(cpfSocio) FROM socio";

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há informações
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } 
        }

        return 0;
    }

    //conta a quantidade de sócios
    public int contarDependentesAtivos() throws SQLException {
        String sql = "SELECT count(dependente.cpfDependente) FROM dependente, socio WHERE socio.pk_idSocio = dependente.fk_idSocio AND socio.ativoSocio = TRUE";

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há informações
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } 
        }

        return 0;
    }

    //conta a quantidade de sócios ativos
    public int contarSociosAtivos() throws SQLException {
        String sql = "SELECT count(cpfSocio) FROM socio WHERE ativoSocio = true";

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há informações
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } 
        }

        return 0;
    }

    //conta a quantidade de sócios inativos
    public int contarSociosInativos() throws SQLException {
        String sql = "SELECT count(cpfSocio) FROM socio WHERE ativoSocio = false";

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há informações
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } 
        }

        return 0;
    }

    //conta a quantidade de sócios inadimplentes
    public int contarSociosInadimplentes() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT d.fk_idSocio) FROM debito d JOIN socio s ON d.fk_idSocio = s.pk_idSocio WHERE d.dtPgmtDebito IS NULL AND d.vencimentoDebito < CURRENT_DATE AND s.ativoSocio = TRUE";

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            //cria um ResultSet para armazenar as informações buscadas
            try (ResultSet rs = stmt.executeQuery()) {
                //verifica se há informações
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } 
        }

        return 0;
    }

    //cadastra um novo sócio no banco de dados
    public void cadastrarSocio(Socio socio) throws SQLException {
        String sql = "INSERT INTO socio (cpfSocio, nomeSocio, sexoSocio, corSocio, telefoneSocio, dataNascSocio, emailSocio, ativoSocio, fk_idEndereco, fk_idUsuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = conexao.getConexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, socio.getCpfSocio());
            stmt.setString(2, socio.getNomeSocio());

            stmt.setString(3, socio.getSexoSocio());
            stmt.setString(4, socio.getCorSocio());

            //atribui o telefone se estiver preenchido
            if(socio.getTelefoneSocio() != null && !socio.getTelefoneSocio().isBlank()) {
                stmt.setString(5, socio.getTelefoneSocio());
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }

            stmt.setDate(6, new java.sql.Date(socio.getDataNascSocio().getTime()));
            stmt.setString(7, socio.getEmailSocio());
            stmt.setBoolean(8, true);
            
            //atribui o endereço se estiver preenchido
            if (socio.getEndereco() != null) {
                stmt.setInt(9, socio.getEndereco().getIdEndereco());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }

            stmt.setInt(10, socio.getUsuario().getIdUsuario());

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

    //busca um sócio passando o seu id
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

    //lista todos os sócios em dia
    public List<String> listarSociosEmDia() throws SQLException {
        String sql = """
                SELECT socio.nomeSocio
                FROM socio
                WHERE 
                    ativoSocio = true
                    and NOT EXISTS (
                        SELECT 1 
                        FROM debito 
                        WHERE debito.fk_idSocio = socio.pk_idSocio 
                        AND debito.dtPgmtDebito IS NULL 
                        AND debito.vencimentoDebito < CURRENT_DATE
                    )
                ORDER BY socio.nomeSocio
        """;

        List<String> listaNomeSocios = new ArrayList<>();

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                listaNomeSocios.add(rs.getString("nomeSocio"));
            }
        }

        return listaNomeSocios;
    }

    //lista todos os sócios com pendências
    public List<String> listarSociosPendentes() throws SQLException {
        String sql = """
                SELECT socio.*
                FROM socio
                WHERE 
                    socio.ativoSocio = TRUE
                    AND EXISTS (
                        SELECT 1 
                        FROM debito 
                        WHERE 
                            debito.fk_idSocio = socio.pk_idSocio 
                            AND debito.dtPgmtDebito IS NULL
                            AND debito.vencimentoDebito < CURRENT_DATE
                    )
                ORDER BY socio.nomeSocio
        """;

        List<String> listaNomeSocios = new ArrayList<>();

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                listaNomeSocios.add(rs.getString("nomeSocio"));
            }
        }

        return listaNomeSocios;
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
        String sql = "UPDATE socio SET cpfSocio = ?, nomeSocio = ?, sexoSocio = ?, corSocio = ?, telefoneSocio = ?, dataNascSocio = ?, emailSocio = ?, ativoSocio = ?, fk_idEndereco = ?, fk_idUsuario = ? WHERE pk_idSocio = ?";
        
        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setString(1, socio.getCpfSocio());
            stmt.setString(2, socio.getNomeSocio());

            stmt.setString(3, socio.getSexoSocio());
            stmt.setString(4, socio.getCorSocio());

            //verifica se o socio tem telefone cadastrado
            if (socio.getTelefoneSocio() != null && !socio.getTelefoneSocio().isBlank()) {
                stmt.setString(5, socio.getTelefoneSocio());
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }

            stmt.setDate(6, new java.sql.Date(socio.getDataNascSocio().getTime()));
            stmt.setString(7, socio.getEmailSocio());
            stmt.setBoolean(8, socio.isAtivoSocio());

            // verifica se o socio tem endereço cadastrado
            if (socio.getEndereco() != null) {
                stmt.setInt(9, socio.getEndereco().getIdEndereco());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }

            //verifica qual é a chave estrangeira do usuario que cadastrou
            int idUsuario = socio.getUsuario().getIdUsuario();
            stmt.setInt(10, idUsuario);

            stmt.setInt(11, socio.getIdSocio());

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

    //ativa um socio
    public void ativarSocio(int id) throws SQLException {
        String sql = "UPDATE socio SET ativoSocio = TRUE WHERE pk_idSocio = ?";

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    //cria uma lista com o resumo dos sócios
    public List<SocioResumoDTO> listarResumoSocios() throws SQLException {
        String sql = """
            SELECT
                s.pk_idSocio AS "idSocio", 
                s.nomeSocio AS "nomeSocio",
                s.ativoSocio AS "ativoSocio",
                CASE 
                    WHEN COUNT(d.pk_idDebito) FILTER (WHERE d.dtPgmtDebito IS NULL AND d.vencimentoDebito < CURRENT_DATE) > 0 
                    THEN FALSE 
                    ELSE TRUE 
                END AS "situacaoAdimplente",
                ARRAY_REMOVE(ARRAY_AGG(DISTINCT dep.nomeDependente), NULL) AS "dependentes",
                ARRAY_REMOVE(ARRAY_AGG(DISTINCT depa.nomeDepartamento), NULL) AS "departamentos"
            FROM socio s
            LEFT JOIN debito d ON s.pk_idSocio = d.fk_idSocio
            LEFT JOIN dependente dep ON s.pk_idSocio = dep.fk_idSocio
            LEFT JOIN socio_departamento sd ON s.pk_idSocio = sd.fk_idSocio
            LEFT JOIN departamento depa ON sd.fk_idDepartamento = depa.pk_idDepartamento
            GROUP BY s.pk_idSocio, s.nomeSocio, s.ativoSocio
            ORDER BY 
                CASE 
                    WHEN s.ativoSocio = FALSE THEN 3
                    WHEN COUNT(d.pk_idDebito) FILTER (WHERE d.dtPgmtDebito IS NULL AND d.vencimentoDebito < CURRENT_DATE) > 0 THEN 2
                    ELSE 1
                END ASC,
                s.nomeSocio ASC
        """;

        List<SocioResumoDTO> listaResumo = new ArrayList<>();

        try (PreparedStatement stmt = conexao.getConexao().prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                SocioResumoDTO dto = new SocioResumoDTO();
                
                dto.setIdSocio(rs.getInt("idSocio"));
                dto.setNomeSocio(rs.getString("nomeSocio"));
                dto.setAtivoSocio(rs.getBoolean("ativoSocio"));
                dto.setSituacaoAdimplente(rs.getBoolean("situacaoAdimplente"));

                // Converte o ARRAY de dependentes do Postgres para List<String>
                java.sql.Array arrayDep = rs.getArray("dependentes");
                if (arrayDep != null) {
                    String[] deps = (String[]) arrayDep.getArray();
                    dto.setDependentes(Arrays.asList(deps));
                } else {
                    dto.setDependentes(new ArrayList<>());
                }

                // Converte o ARRAY de departamentos do Postgres para List<String>
                java.sql.Array arrayDepa = rs.getArray("departamentos");
                if (arrayDepa != null) {
                    String[] depas = (String[]) arrayDepa.getArray();
                    dto.setDepartamentos(Arrays.asList(depas));
                } else {
                    dto.setDepartamentos(new ArrayList<>());
                }

                listaResumo.add(dto);
            }
        }

        return listaResumo;
    }

    //função para preencher o gráfico de sócios ativos e inativos
    public ObservableList<PieChart.Data> buscarPorcentagemAtivosInativos() {
        ObservableList<PieChart.Data> dadosAtivosInativos = FXCollections.observableArrayList();
        
        String sql = "SELECT CASE WHEN ativoSocio = TRUE THEN 'Ativos' ELSE 'Inativos' END AS status, COUNT(*) AS quantidade FROM socio GROUP BY ativoSocio";

        try (Connection conexaoBanco = conexao.getConexao();
             PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String status = rs.getString("status");
                double quantidade = rs.getDouble("quantidade");
                dadosAtivosInativos.add(new PieChart.Data(status, quantidade));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dadosAtivosInativos;
    }

    //função para preencher o gráfico dos sócios em dia e dos sócios inadimplentes
    public ObservableList<PieChart.Data> buscarPorcentagemEmdiaInadimplentes() {
        ObservableList<PieChart.Data> dadosEmdiaInadimplentes = FXCollections.observableArrayList();
        
        String sql = "WITH status_socios AS (" +
                    "    SELECT s.pk_idSocio, " +
                    "        CASE " +
                    "            WHEN EXISTS (" +
                    "                SELECT 1 FROM debito d " +
                    "                WHERE d.fk_idSocio = s.pk_idSocio " +
                    "                  AND d.vencimentoDebito < CURRENT_DATE " +
                    "                  AND d.dtPgmtDebito IS NULL" +
                    "            ) THEN 'Inadimplentes' " +
                    "            ELSE 'Em dia' " +
                    "        END AS status_pagamento " +
                    "    FROM socio s " +
                    "    WHERE s.ativoSocio = TRUE" +
                    ") " +
                    "SELECT status_pagamento, COUNT(*) AS quantidade " +
                    "FROM status_socios " +
                    "GROUP BY status_pagamento";

        try (Connection conexaoBanco = conexao.getConexao();
            PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String status = rs.getString("status_pagamento");
                double quantidade = rs.getDouble("quantidade");
                dadosEmdiaInadimplentes.add(new PieChart.Data(status, quantidade));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dadosEmdiaInadimplentes;
    }

    //função para buscar os dados para preencher o gráfico de homens e mulheres
    public ObservableList<PieChart.Data> buscarPorcentagemHomensMulheres() {
        ObservableList<PieChart.Data> dadosHomensMulheres = FXCollections.observableArrayList();
        
        String sql = "SELECT genero, COUNT(*) AS quantidade FROM (" +
                    "    SELECT sexoSocio AS genero FROM socio WHERE ativoSocio = TRUE " +
                    "    UNION ALL " +
                    "    SELECT d.sexoDependente AS genero " +
                    "    FROM dependente d " +
                    "    JOIN socio s ON d.fk_idSocio = s.pk_idSocio " +
                    "    WHERE s.ativoSocio = TRUE" +
                    ") AS todos_membros " +
                    "GROUP BY genero";

        try (Connection conexaoBanco = conexao.getConexao();
            PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String genero = rs.getString("genero");
                double quantidade = rs.getDouble("quantidade");
                
                if (genero != null && !genero.isEmpty()) {
                    dadosHomensMulheres.add(new PieChart.Data(genero, quantidade));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dadosHomensMulheres;
    }

    //função que busca os dados para preencher o gráfico das etnias
    public ObservableList<PieChart.Data> buscarPorcentagemEtnias() {
        ObservableList<PieChart.Data> dadosEtnias = FXCollections.observableArrayList();
        
        // Consulta que une a cor dos Sócios Ativos e a cor dos seus Dependentes
        String sql = "SELECT cor, COUNT(*) AS quantidade FROM (" +
                    "    SELECT corSocio AS cor FROM socio WHERE ativoSocio = TRUE " +
                    "    UNION ALL " +
                    "    SELECT d.corDependente AS cor " +
                    "    FROM dependente d " +
                    "    JOIN socio s ON d.fk_idSocio = s.pk_idSocio " +
                    "    WHERE s.ativoSocio = TRUE" +
                    ") AS todas_cores " +
                    "GROUP BY cor";

        try (Connection conexaoBanco = conexao.getConexao();
            PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String cor = rs.getString("cor");
                double quantidade = rs.getDouble("quantidade");
                
                if (cor != null && !cor.isEmpty()) {
                    dadosEtnias.add(new PieChart.Data(cor, quantidade));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dadosEtnias;
    }

    //função para buscar os dados para preencher o gráfico de faixa etária
    public ObservableList<PieChart.Data> buscarPorcentagemFaixaEtaria() {
        ObservableList<PieChart.Data> dadosFaixaEtaria = FXCollections.observableArrayList();
        
        String sql = "SELECT faixa_etaria, COUNT(*) AS quantidade FROM (" +
                    "    SELECT " +
                    "        CASE " +
                    "            WHEN EXTRACT(YEAR FROM AGE(dataNascSocio)) < 18 THEN '0-17 anos' " +
                    "            WHEN EXTRACT(YEAR FROM AGE(dataNascSocio)) BETWEEN 18 AND 35 THEN '18-35 anos' " +
                    "            WHEN EXTRACT(YEAR FROM AGE(dataNascSocio)) BETWEEN 36 AND 50 THEN '36-50 anos' " +
                    "            WHEN EXTRACT(YEAR FROM AGE(dataNascSocio)) BETWEEN 51 AND 65 THEN '51-65 anos' " +
                    "            ELSE 'Mais de 65 anos' " +
                    "        END AS faixa_etaria " +
                    "    FROM socio WHERE ativoSocio = TRUE " +
                    "    UNION ALL " +
                    "    SELECT " +
                    "        CASE " +
                    "            WHEN EXTRACT(YEAR FROM AGE(d.dataNascDependente)) < 18 THEN '0-17 anos' " +
                    "            WHEN EXTRACT(YEAR FROM AGE(d.dataNascDependente)) BETWEEN 18 AND 35 THEN '18-35 anos' " +
                    "            WHEN EXTRACT(YEAR FROM AGE(d.dataNascDependente)) BETWEEN 36 AND 50 THEN '36-50 anos' " +
                    "            WHEN EXTRACT(YEAR FROM AGE(d.dataNascDependente)) BETWEEN 51 AND 65 THEN '51-65 anos' " +
                    "            ELSE 'Mais de 65 anos' " +
                    "        END AS faixa_etaria " +
                    "    FROM dependente d " +
                    "    JOIN socio s ON d.fk_idSocio = s.pk_idSocio " +
                    "    WHERE s.ativoSocio = TRUE" +
                    ") AS todas_idades " +
                    "GROUP BY faixa_etaria " +
                    "ORDER BY " +
                    "    CASE faixa_etaria " +
                    "        WHEN '0-17 anos' THEN 1 " +
                    "        WHEN '18-35 anos' THEN 2 " +
                    "        WHEN '36-50 anos' THEN 3 " +
                    "        WHEN '51-65 anos' THEN 4 " +
                    "        ELSE 5 " +
                    "    END";

        try (Connection conexaoBanco = conexao.getConexao();
            PreparedStatement stmt = conexaoBanco.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String faixa = rs.getString("faixa_etaria");
                double quantidade = rs.getDouble("quantidade");
                
                if (faixa != null) {
                    dadosFaixaEtaria.add(new PieChart.Data(faixa, quantidade));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dadosFaixaEtaria;
    }

    //método auxiliar, que vai montar o objeto sócio após a consulta sql
    private Socio montarObjSocio(ResultSet rs) throws SQLException {
        //cria o objeto
        Socio socio = new Socio();

        //atribui os valores
        socio.setIdSocio(rs.getInt("pk_idSocio"));
        socio.setCpfSocio(rs.getString("cpfSocio"));
        socio.setNomeSocio(rs.getString("nomeSocio"));
        socio.setSexoSocio(rs.getString("sexoSocio"));
        socio.setCorSocio(rs.getString("corSocio"));
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

        //verifica qual é a chave estrangeira do usuário que cadastrou
        int idUsuario = rs.getInt("fk_idUsuario");
        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        socio.setUsuario(usuario);

        //retorna o usuario
        return socio;
    }
}
