package app; // Ajuste o pacote conforme o seu projeto

import dao.ConexaoBanco;
import dao.DebitoDAO;
import dao.SocioDAO;
import model.Debito;
import model.Socio;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class MainTesteCompletoDebito {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("        TESTE COMPLETO DOS MÉTODOS DO DEBITODAO           ");
        System.out.println("==========================================================");

        ConexaoBanco conexao = new ConexaoBanco();

        try {
            // 1. Instanciando os DAOs
            SocioDAO socioDAO = new SocioDAO(conexao);
            DebitoDAO debitoDAO = new DebitoDAO(conexao);

            // -------------------------------------------------------------------
            // ETAPA PREPARATÓRIA: Buscar um Sócio existente (ID 1)
            // -------------------------------------------------------------------
            System.out.println("\n[PREPARAÇÃO] Buscando o Sócio ID 1...");
            Socio socio = socioDAO.buscarPorId(1);

            if (socio == null) {
                System.err.println("❌ Erro Crítico: O Sócio ID 1 não existe no banco de dados.");
                return;
            }
            System.out.println(" Sócio encontrado: " + socio.getNomeSocio() + " (ID: " + socio.getIdSocio() + ")");

            // -------------------------------------------------------------------
            // 1. TESTE: cadastrarDebito (Débito em Aberto)
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 1] Invocando cadastrarDebito()...");
            
            Debito novoDebito = new Debito();
            novoDebito.setTipoDebito("Mensalidade CTG");
            novoDebito.setValorDebito(75.50);
            novoDebito.setVencimentoDebito(Date.valueOf("2026-09-10"));
            novoDebito.setDtPgmtDebito(null); // Em aberto (sem pagamento)
            novoDebito.setSocio(socio);

            debitoDAO.cadastrarDebito(novoDebito);
            System.out.println(" cadastrarDebito() executado com sucesso! ID gerado: " + novoDebito.getIdDebito());

            // -------------------------------------------------------------------
            // 2. TESTE: buscarPorId
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 2] Invocando buscarPorId(" + novoDebito.getIdDebito() + ")...");
            Debito debitoPorId = debitoDAO.buscarPorId(novoDebito.getIdDebito());

            if (debitoPorId != null) {
                System.out.println(" buscarPorId() teve sucesso!");
                System.out.println("   - ID Débito:    " + debitoPorId.getIdDebito());
                System.out.println("   - Tipo:         " + debitoPorId.getTipoDebito());
                System.out.println("   - Valor:        R$ " + debitoPorId.getValorDebito());
                System.out.println("   - Vencimento:   " + debitoPorId.getVencimentoDebito());
                System.out.println("   - Pagamento:    " + (debitoPorId.getDtPgmtDebito() != null ? debitoPorId.getDtPgmtDebito() : "EM ABERTO"));
                System.out.println("   - Sócio:        " + (debitoPorId.getSocio() != null ? debitoPorId.getSocio().getNomeSocio() : "Sem sócio"));
            } else {
                System.out.println("❌ buscarPorId() falhou: Débito não encontrado.");
            }

            // -------------------------------------------------------------------
            // 3. TESTE: listarDebitosAbertosSocio
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 3] Invocando listarDebitosAbertosSocio(" + socio.getIdSocio() + ")...");
            List<Debito> listaAbertos = debitoDAO.listarDebitosAbertosSocio(socio.getIdSocio());

            System.out.println(" listarDebitosAbertosSocio() retornou " + listaAbertos.size() + " débito(s) em aberto:");
            for (Debito d : listaAbertos) {
                System.out.println("   - ID: " + d.getIdDebito() + " | Tipo: " + d.getTipoDebito() + " | Valor: R$ " + d.getValorDebito() + " | Vencimento: " + d.getVencimentoDebito());
            }

            // -------------------------------------------------------------------
            // 4. TESTE: atualizarDebito (Simulando Quitação/Pagamento)
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 4] Invocando atualizarDebito() para registrar o pagamento...");
            novoDebito.setValorDebito(80.00); // Ex: Ajuste com acréscimo
            novoDebito.setDtPgmtDebito(Date.valueOf("2026-08-18")); // Registra a data de hoje como paga

            debitoDAO.atualizarDebito(novoDebito);

            Debito debitoAtualizado = debitoDAO.buscarPorId(novoDebito.getIdDebito());
            System.out.println(" atualizarDebito() executado! Dados atualizados no banco:");
            System.out.println("   - Novo Valor:     R$ " + debitoAtualizado.getValorDebito());
            System.out.println("   - Data Pagamento: " + debitoAtualizado.getDtPgmtDebito());

            // -------------------------------------------------------------------
            // 5. TESTE: excluirDebito
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 5] Invocando excluirDebito(" + novoDebito.getIdDebito() + ")...");
            debitoDAO.excluirDebito(novoDebito);

            Debito debitoExcluido = debitoDAO.buscarPorId(novoDebito.getIdDebito());
            if (debitoExcluido == null) {
                System.out.println(" excluirDebito() executado com sucesso! Registro não consta mais no banco.");
            } else {
                System.out.println("❌ Erro: O débito ainda continua gravado no banco.");
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