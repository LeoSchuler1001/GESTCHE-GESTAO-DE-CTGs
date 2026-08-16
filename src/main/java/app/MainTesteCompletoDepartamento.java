package app; // Ajuste o pacote conforme o seu projeto

import dao.ConexaoBanco;
import dao.DepartamentoDAO;
import model.Departamento;

import java.sql.SQLException;
import java.util.List;

public class MainTesteCompletoDepartamento {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("     TESTE COMPLETO DOS MÉTODOS DO DEPARTAMENTODAO        ");
        System.out.println("==========================================================");

        ConexaoBanco conexao = new ConexaoBanco();

        try {
            DepartamentoDAO departamentoDAO = new DepartamentoDAO(conexao);

            // -------------------------------------------------------------------
            // 1. TESTE: cadastrarDepartamento
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 1] Invocando cadastrarDepartamento()...");
            
            Departamento novoDepartamento = new Departamento();
            novoDepartamento.setNomeDepartamento("Departamento Cultural");
            novoDepartamento.setDescricaoDepartamento("Responsável pelas atividades culturais e tradicionalistas do CTG.");

            departamentoDAO.cadastrarDepartamento(novoDepartamento);
            System.out.println(" cadastrarDepartamento() executado com sucesso! ID gerado: " + novoDepartamento.getIdDepartamento());

            // -------------------------------------------------------------------
            // 2. TESTE: buscarPorId
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 2] Invocando buscarPorId(" + novoDepartamento.getIdDepartamento() + ")...");
            Departamento deptoPorId = departamentoDAO.buscarPorId(novoDepartamento.getIdDepartamento());

            if (deptoPorId != null) {
                System.out.println(" buscarPorId() teve sucesso!");
                System.out.println("   - ID:        " + deptoPorId.getIdDepartamento());
                System.out.println("   - Nome:      " + deptoPorId.getNomeDepartamento());
                System.out.println("   - Descrição: " + deptoPorId.getDescricaoDepartamento());
            } else {
                System.out.println("❌ buscarPorId() falhou: Departamento não encontrado.");
            }

            // -------------------------------------------------------------------
            // 3. TESTE: atualirDepartamento (ou atualizarDepartamento)
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 3] Invocando atualização do departamento...");
            novoDepartamento.setNomeDepartamento("Departamento Artístico e Cultural");
            novoDepartamento.setDescricaoDepartamento("Responsável por danças tradicionais, declamação e eventos artísticos.");

            departamentoDAO.atualizarDepartamento(novoDepartamento);

            Departamento deptoAtualizado = departamentoDAO.buscarPorId(novoDepartamento.getIdDepartamento());
            System.out.println(" Atualização executada! Novos dados verificados no banco:");
            System.out.println("   - Novo Nome:      " + deptoAtualizado.getNomeDepartamento());
            System.out.println("   - Nova Descrição: " + deptoAtualizado.getDescricaoDepartamento());

            // -------------------------------------------------------------------
            // 4. TESTE: listarTodos
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 4] Invocando listarTodos()...");
            List<Departamento> listaDeptos = departamentoDAO.listarTodos();

            System.out.println(" listarTodos() retornou " + listaDeptos.size() + " departamento(s):");
            for (Departamento d : listaDeptos) {
                System.out.println("   - ID: " + d.getIdDepartamento() + " | Nome: " + d.getNomeDepartamento());
            }

            // -------------------------------------------------------------------
            // 5. TESTE: excluirDepartamento
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 5] Invocando excluirDepartamento(" + novoDepartamento.getIdDepartamento() + ")...");
            departamentoDAO.excluirDepartamento(novoDepartamento);

            Departamento deptoExcluido = departamentoDAO.buscarPorId(novoDepartamento.getIdDepartamento());
            if (deptoExcluido == null) {
                System.out.println(" excluirDepartamento() executado com sucesso! Registro não existe mais no banco.");
            } else {
                System.out.println("❌ Erro: O departamento ainda consta no banco de dados.");
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