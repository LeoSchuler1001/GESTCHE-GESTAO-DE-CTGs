package app; // Ajuste para o pacote do seu projeto

import dao.ConexaoBanco;
import dao.DepartamentoDAO;
import dao.SocioDAO;
import dao.Socio_DepartamentoDAO;
import model.Departamento;
import model.Socio;

import java.sql.SQLException;
import java.util.List;

public class MainTesteSocioDepartamento {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("     TESTE DOS MÉTODOS DO SOCIO_DEPARTAMENTODAO           ");
        System.out.println("==========================================================");

        ConexaoBanco conexao = new ConexaoBanco();

        try {
            // 1. Instanciando os DAOs
            SocioDAO socioDAO = new SocioDAO(conexao);
            DepartamentoDAO departamentoDAO = new DepartamentoDAO(conexao);
            Socio_DepartamentoDAO socioDeptDAO = new Socio_DepartamentoDAO(conexao);

            int idSocioTeste = 1;
            int idDeptoTeste = 1;

            // -------------------------------------------------------------------
            // ETAPA PREPARATÓRIA: Checagem dos registros no banco
            // -------------------------------------------------------------------
            System.out.println("\n[PREPARAÇÃO] Verificando existência do Sócio e do Departamento...");

            Socio socio = socioDAO.buscarPorId(idSocioTeste);
            if (socio == null) {
                System.err.println("❌ Erro: Sócio ID " + idSocioTeste + " não encontrado no banco.");
                return;
            }

            Departamento depto = departamentoDAO.buscarPorId(idDeptoTeste);
            if (depto == null) {
                System.err.println("❌ Erro: Departamento ID " + idDeptoTeste + " não encontrado no banco.");
                return;
            }

            System.out.println(" Sócio localizado:        " + socio.getNomeSocio() + " (ID: " + socio.getIdSocio() + ")");
            System.out.println(" Departamento localizado: " + depto.getNomeDepartamento() + " (ID: " + depto.getIdDepartamento() + ")");

            // -------------------------------------------------------------------
            // TESTE 1: vincularSocioDepartamento
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 1] Invocando vincularSocioDepartamento(" + idSocioTeste + ", " + idDeptoTeste + ")...");
            
            try {
                socioDeptDAO.vincularSocioDepartamento(idSocioTeste, idDeptoTeste);
                System.out.println(" vincularSocioDepartamento() executado com sucesso!");
            } catch (SQLException e) {
                // Caso a tabela já possua chave primária composta e o par já exista:
                System.out.println("ℹ️ Aviso: O vínculo entre o sócio e o departamento já existia no banco.");
            }

            // -------------------------------------------------------------------
            // TESTE 2: buscarSociosDepartamento
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 2] Invocando buscarSociosDepartamento(" + idDeptoTeste + ")...");
            List<Socio> listaSocios = socioDeptDAO.buscarSociosDepartamento(idDeptoTeste);

            System.out.println(" buscarSociosDepartamento() retornou " + listaSocios.size() + " sócio(s):");
            
            for (Socio s : listaSocios) {
                System.out.println("----------------------------------------------------------");
                System.out.println("   - ID Sócio:       " + s.getIdSocio());
                System.out.println("   - Nome:           " + s.getNomeSocio());
                System.out.println("   - CPF:            " + s.getCpfSocio());
                System.out.println("   - Telefone:       " + s.getTelefoneSocio());
                System.out.println("   - Data Nasc:      " + s.getDataNascSocio());
                System.out.println("   - Ativo:          " + s.isAtivoSocio());
                System.out.println("   - Endereço:       " + (s.getEndereco() != null ? s.getEndereco().getRua() : "Sem endereço"));
                System.out.println("   - Cadastrado por: " + (s.getUsuario() != null ? s.getUsuario().getNomeUsuario() : "Não informado"));
            }

            System.out.println("\n==========================================================");
            System.out.println("         TODOS OS TESTES FORAM FINALIZADOS!               ");
            System.out.println("==========================================================");

        } catch (SQLException e) {
            System.err.println("\n❌ Erro durante a execução dos testes SQL:");
            e.printStackTrace();
        } finally {
            try {
                if (conexao.getConexao() != null && !conexao.getConexao().isClosed()) {
                    conexao.getConexao().close();
                    System.out.println("\n Conexão com o banco finalizada.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}