package app; // Ajuste o pacote conforme a sua estrutura de pastas

import dao.ConexaoBanco;
import dao.DependenteDAO;
import dao.SocioDAO;
import model.Dependente;
import model.Socio;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class MainTesteCompletoDependente {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("       TESTE COMPLETO DOS MÉTODOS DO DEPENDENTEDAO        ");
        System.out.println("==========================================================");

        ConexaoBanco conexao = new ConexaoBanco();

        try {
            // 1. Instanciação dos DAOs
            SocioDAO socioDAO = new SocioDAO(conexao);
            DependenteDAO dependenteDAO = new DependenteDAO(conexao);

            // -------------------------------------------------------------------
            // ETAPA PREPARATÓRIA: Buscar um Sócio existente (ID 1)
            // -------------------------------------------------------------------
            System.out.println("\n[PREPARAÇÃO] Buscando um Sócio existente para ser o titular...");
            Socio socioTitular = socioDAO.buscarPorId(1);

            if (socioTitular == null) {
                System.err.println("❌ Erro Crítico: O Sócio ID 1 não foi encontrado no banco. Cadastre um sócio antes de rodar este teste.");
                return;
            }
            System.out.println(" Sócio Titular encontrado: " + socioTitular.getNomeSocio() + " (ID: " + socioTitular.getIdSocio() + ")");

            // -------------------------------------------------------------------
            // 1. TESTE: cadastrarDependente
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 1] Invocando cadastrarDependente()...");
            
            String cpfDependente = "87459874265"; // Informe um CPF válido de 11 dígitos

            Dependente novoDependente = new Dependente();
            novoDependente.setNomeDependente("Lucas Silveira");
            novoDependente.setCpfDependente(cpfDependente);
            novoDependente.setDataNascDependente(Date.valueOf("2015-08-10"));
            novoDependente.setSocio(socioTitular);

            dependenteDAO.cadastrarDependente(novoDependente);
            System.out.println(" cadastrarDependente() executado com sucesso! ID gerado: " + novoDependente.getIdDependente());

            // -------------------------------------------------------------------
            // 2. TESTE: buscarPorId
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 2] Invocando buscarPorId(" + novoDependente.getIdDependente() + ")...");
            Dependente dependentePorId = dependenteDAO.buscarPorId(novoDependente.getIdDependente());

            if (dependentePorId != null) {
                System.out.println(" buscarPorId() teve sucesso!");
                System.out.println("   - Nome:       " + dependentePorId.getNomeDependente());
                System.out.println("   - Data Nasc:  " + dependentePorId.getDataNascDependente());
                System.out.println("   - Titular:    " + (dependentePorId.getSocio() != null ? dependentePorId.getSocio().getNomeSocio() : "Sem sócio associado"));
            } else {
                System.out.println("❌ buscarPorId() falhou: Dependente não retornado.");
            }

            // -------------------------------------------------------------------
            // 3. TESTE: buscaDependentePorCpf
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 3] Invocando buscaDependentePorCpf('" + cpfDependente + "')...");
            Dependente dependentePorCpf = dependenteDAO.buscaDependentePorCpf(cpfDependente);

            if (dependentePorCpf != null) {
                System.out.println(" buscaDependentePorCpf() teve sucesso: Encontrou " + dependentePorCpf.getNomeDependente() + " (ID: " + dependentePorCpf.getIdDependente() + ")");
            } else {
                System.out.println("❌ buscaDependentePorCpf() falhou: CPF não retornado.");
            }

            // -------------------------------------------------------------------
            // 4. TESTE: buscarPorIdSocio
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 4] Invocando buscarPorIdSocio(" + socioTitular.getIdSocio() + ")...");
            List<Dependente> dependentesDoSocio = dependenteDAO.buscarPorIdSocio(socioTitular.getIdSocio());

            System.out.println(" buscarPorIdSocio() retornou " + dependentesDoSocio.size() + " dependente(s) associado(s):");
            for (Dependente dep : dependentesDoSocio) {
                System.out.println("   - ID: " + dep.getIdDependente() + " | Nome: " + dep.getNomeDependente() + " | CPF: " + dep.getCpfDependente());
            }

            // -------------------------------------------------------------------
            // 5. TESTE: atualizarDependente
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 5] Invocando atualizarDependente()...");
            novoDependente.setNomeDependente("Lucas Silveira Atualizado");
            novoDependente.setDataNascDependente(Date.valueOf("2015-08-15"));

            dependenteDAO.atualizarDependente(novoDependente);

            Dependente dependenteAtualizado = dependenteDAO.buscarPorId(novoDependente.getIdDependente());
            System.out.println(" atualizarDependente() executado! Dados no banco:");
            System.out.println("   - Novo Nome:      " + dependenteAtualizado.getNomeDependente());
            System.out.println("   - Nova Data Nasc: " + dependenteAtualizado.getDataNascDependente());

            // -------------------------------------------------------------------
            // 6. TESTE: listarTodosAtivos
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 6] Invocando listarTodosAtivos()...");
            List<Dependente> listaAtivos = dependenteDAO.listarTodosAtivos();

            System.out.println(" listarTodosAtivos() retornou " + listaAtivos.size() + " dependente(s) com titulares ativos:");
            for (Dependente dep : listaAtivos) {
                System.out.println("   - ID: " + dep.getIdDependente() + " | Nome: " + dep.getNomeDependente() + " | Titular: " + dep.getSocio().getNomeSocio());
            }

            // -------------------------------------------------------------------
            // 7. TESTE: excluirDependente
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 7] Invocando excluirDependente(" + novoDependente.getIdDependente() + ")...");
            dependenteDAO.excluirDependente(novoDependente);

            Dependente dependenteExcluido = dependenteDAO.buscarPorId(novoDependente.getIdDependente());
            if (dependenteExcluido == null) {
                System.out.println(" excluirDependente() executado com sucesso! Registro não existe mais no banco.");
            } else {
                System.out.println("❌ Erro: Registro ainda foi encontrado no banco.");
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