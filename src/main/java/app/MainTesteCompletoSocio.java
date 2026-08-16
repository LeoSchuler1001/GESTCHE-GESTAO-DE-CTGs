package app; // Ajuste o pacote conforme seu projeto

import dao.ConexaoBanco;
import dao.EnderecoDAO;
import dao.SocioDAO;
import dao.UsuarioDAO;
import model.Endereco;
import model.Socio;
import model.Usuario;

import java.sql.SQLException;
import java.util.List;

public class MainTesteCompletoSocio {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("         TESTE COMPLETO DOS MÉTODOS DO SOCIODAO           ");
        System.out.println("==========================================================");

        ConexaoBanco conexao = new ConexaoBanco();

        try {
            // 1. Instanciando os DAOs
            EnderecoDAO enderecoDAO = new EnderecoDAO(conexao);
            UsuarioDAO usuarioDAO = new UsuarioDAO(conexao);
            SocioDAO socioDAO = new SocioDAO(conexao);

            // -------------------------------------------------------------------
            // ETAPA PREPARATÓRIA 1: Buscar o Usuário ID 1 para o cadastro
            // -------------------------------------------------------------------
            System.out.println("\n[PREPARAÇÃO 1] Buscando o Usuário ID 1 no banco...");
            Usuario usuarioResponsavel = usuarioDAO.buscarPorId(1);

            if (usuarioResponsavel == null) {
                System.err.println("❌ Erro Crítico: O usuário ID 1 não existe no banco. Crie o usuário 1 antes de rodar este teste.");
                return;
            }
            System.out.println(" Usuário encontrado: " + usuarioResponsavel.getNomeUsuario() + " (ID: " + usuarioResponsavel.getIdUsuario() + ")");

            // -------------------------------------------------------------------
            // ETAPA PREPARATÓRIA 2: Cadastrar Endereço para o Sócio
            // -------------------------------------------------------------------
            System.out.println("\n[PREPARAÇÃO 2] Cadastrando endereço de teste para o sócio...");
            Endereco enderecoSocio = new Endereco();
            enderecoSocio.setRua("Rua dos Andradas");
            enderecoSocio.setNumero(789);
            enderecoSocio.setBairro("Centro Histórico");
            enderecoSocio.setCidade("Porto Alegre");
            enderecoSocio.setEstado("RS");
            enderecoSocio.setCep("90020000");

            enderecoDAO.cadastrarEndereco(enderecoSocio);
            System.out.println(" Endereço cadastrado com sucesso! ID: " + enderecoSocio.getIdEndereco());

            // -------------------------------------------------------------------
            // 1. TESTE: cadastrarSocio
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 1] Invocando cadastrarSocio()...");
            
            String cpfTeste = "85476985212"; // 11 dígitos numéricos válidos
            
            Socio novoSocio = new Socio();
            novoSocio.setCpfSocio(cpfTeste);
            novoSocio.setNomeSocio("Mateus Silveira");
            novoSocio.setTelefoneSocio("51981112233");
            
            // Formatando data de nascimento para java.util.Date
            novoSocio.setDataNascSocio(java.sql.Date.valueOf("1995-04-22"));
            
            novoSocio.setEmailSocio("mateus.silveira@email.com");
            novoSocio.setAtivoSocio(true);
            novoSocio.setEndereco(enderecoSocio);
            novoSocio.setUsuario(usuarioResponsavel); // Vinculando ao Usuário 1

            socioDAO.cadastrarSocio(novoSocio);
            System.out.println(" cadastrarSocio() executado com sucesso! ID gerado: " + novoSocio.getIdSocio());

            // -------------------------------------------------------------------
            // 2. TESTE: buscarPorId
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 2] Invocando buscarPorId(" + novoSocio.getIdSocio() + ")...");
            Socio socioPorId = socioDAO.buscarPorId(novoSocio.getIdSocio());

            if (socioPorId != null) {
                System.out.println(" buscarPorId() teve sucesso!");
                System.out.println("   - Nome Sócio:  " + socioPorId.getNomeSocio());
                System.out.println("   - Data Nasc:   " + socioPorId.getDataNascSocio());
                System.out.println("   - Endereço:    " + (socioPorId.getEndereco() != null ? socioPorId.getEndereco().getRua() : "Sem endereço"));
                System.out.println("   - Cadastrado por: " + (socioPorId.getUsuario() != null ? socioPorId.getUsuario().getNomeUsuario() : "Não informado"));
            } else {
                System.out.println("❌ buscarPorId() falhou: Sócio não retornado.");
            }

            // -------------------------------------------------------------------
            // 3. TESTE: buscaSocioPorCPF
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 3] Invocando buscaSocioPorCPF('" + cpfTeste + "')...");
            Socio socioPorCpf = socioDAO.buscaSocioPorCPF(cpfTeste);

            if (socioPorCpf != null) {
                System.out.println(" buscaSocioPorCPF() teve sucesso: Encontrou " + socioPorCpf.getNomeSocio() + " (ID " + socioPorCpf.getIdSocio() + ")");
            } else {
                System.out.println("❌ buscaSocioPorCPF() falhou: CPF não retornado.");
            }

            // -------------------------------------------------------------------
            // 4. TESTE: atualizarSocio
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 4] Invocando atualizarSocio()...");
            novoSocio.setNomeSocio("Mateus Silveira Atualizado");
            novoSocio.setTelefoneSocio("51999990000");
            novoSocio.setEmailSocio("mateus.novoemail@email.com");

            socioDAO.atualizarSocio(novoSocio);

            // Validação no banco
            Socio socioAtualizado = socioDAO.buscarPorId(novoSocio.getIdSocio());
            System.out.println(" atualizarSocio() executado! Novos dados verificados:");
            System.out.println("   - Nome:  " + socioAtualizado.getNomeSocio());
            System.out.println("   - Tel:   " + socioAtualizado.getTelefoneSocio());
            System.out.println("   - Email: " + socioAtualizado.getEmailSocio());

            // -------------------------------------------------------------------
            // 5. TESTE: listarTodosAtivos
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 5] Invocando listarTodosAtivos()...");
            List<Socio> listaAtivos = socioDAO.listarTodosAtivos();
            System.out.println(" listarTodosAtivos() retornou " + listaAtivos.size() + " sócio(s) ativo(s):");
            for (Socio s : listaAtivos) {
                System.out.println("   - ID: " + s.getIdSocio() + " | Nome: " + s.getNomeSocio() + " | Ativo: " + s.isAtivoSocio());
            }

            // -------------------------------------------------------------------
            // 6. TESTE: desativarSocio
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 6] Invocando desativarSocio(" + novoSocio.getIdSocio() + ")...");
            socioDAO.desativarSocio(novoSocio.getIdSocio());

            Socio socioDesativado = socioDAO.buscarPorId(novoSocio.getIdSocio());
            System.out.println(" desativarSocio() executado! Status ativo no banco: " + socioDesativado.isAtivoSocio());

            // -------------------------------------------------------------------
            // 7. TESTE: listarAtivosInativos
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 7] Invocando listarAtivosInativos()...");
            List<Socio> listaTodos = socioDAO.listarAtivosInativos();
            System.out.println(" listarAtivosInativos() retornou " + listaTodos.size() + " sócio(s) no total:");
            for (Socio s : listaTodos) {
                System.out.println("   - ID: " + s.getIdSocio() + " | Nome: " + s.getNomeSocio() + " | Ativo: " + s.isAtivoSocio());
            }

            // Validando se o sócio desativado sumiu da lista de ativos mas continua na lista geral
            List<Socio> novaListaAtivos = socioDAO.listarTodosAtivos();
            boolean aindaEmAtivos = novaListaAtivos.stream().anyMatch(s -> s.getIdSocio() == novoSocio.getIdSocio());
            boolean presenteNaGeral = listaTodos.stream().anyMatch(s -> s.getIdSocio() == novoSocio.getIdSocio());

            System.out.println("\n[VALIDAÇÃO FINAL]");
            System.out.println(" -> Sócio desativado sumiu da lista de ativos? " + (!aindaEmAtivos ? "SIM (Correto)" : "NÃO ❌"));
            System.out.println(" -> Sócio continua presente na lista geral?     " + (presenteNaGeral ? "SIM (Correto)" : "NÃO ❌"));

            System.out.println("\n==========================================================");
            System.out.println("         TODOS OS TESTES FORAM FINALIZADOS!               ");
            System.out.println("==========================================================");

        } catch (Exception e) {
            System.err.println("\n❌ Erro durante a execução dos testes:");
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