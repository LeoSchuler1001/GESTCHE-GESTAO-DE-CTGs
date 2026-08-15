package app; // Ajuste o pacote conforme o seu projeto

import dao.ConexaoBanco;
import dao.EnderecoDAO;
import dao.UsuarioDAO;
import model.Endereco;
import model.Usuario;

import java.sql.SQLException;
import java.util.List;

public class MainTesteCompletoUsuario {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("       TESTE COMPLETO DOS MÉTODOS DO USUARIODAO           ");
        System.out.println("==========================================================");

        ConexaoBanco conexao = new ConexaoBanco();

        try {
            EnderecoDAO enderecoDAO = new EnderecoDAO(conexao);
            UsuarioDAO usuarioDAO = new UsuarioDAO(conexao);

            // -------------------------------------------------------------------
            // ETAPA PREPARATÓRIA: Cadastrar um endereço para associar ao teste
            // -------------------------------------------------------------------
            System.out.println("\n[PREPARAÇÃO] Cadastrando endereço de teste...");
            Endereco endereco = new Endereco();
            endereco.setRua("Rua das Acácias");
            endereco.setNumero(250);
            endereco.setBairro("Jardim América");
            endereco.setCidade("Porto Alegre");
            endereco.setEstado("RS");
            endereco.setCep("91000000");
            
            enderecoDAO.cadastrarEndereco(endereco);
            System.out.println(" Endereço criado com ID: " + endereco.getIdEndereco());

            // -------------------------------------------------------------------
            // 1. TESTE: cadastrarUsuario
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 1] Invocando cadastrarUsuario()...");
            
            // Gerando CPF dinâmico simples ou utilize 11 dígitos fixos não cadastrados
            String cpfTeste = "45874859674"; 

            Usuario usuario = new Usuario();
            usuario.setCpfUsuario(cpfTeste);
            usuario.setNomeUsuario("Roberto Santos");
            usuario.setTelefoneUsuario("51999887766");
            usuario.setCargoUsuario("Analista de Suporte");
            usuario.setSenhaHash("$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
            usuario.setRespostaSeguranca("Nome do animal de estimação");
            usuario.setAtivoUsuario(true);
            usuario.setEndereco(endereco);

            usuarioDAO.cadastrarUsuario(usuario);
            System.out.println(" cadastrarUsuario() executado com sucesso! ID gerado: " + usuario.getIdUsuario());

            // -------------------------------------------------------------------
            // 2. TESTE: buscarPorId
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 2] Invocando buscarPorId(" + usuario.getIdUsuario() + ")...");
            Usuario usuarioPorId = usuarioDAO.buscarPorId(usuario.getIdUsuario());

            if (usuarioPorId != null) {
                System.out.println(" buscarPorId() teve sucesso: Encontrou " + usuarioPorId.getNomeUsuario() + 
                                   " | Endereço: " + (usuarioPorId.getEndereco() != null ? usuarioPorId.getEndereco().getRua() : "Sem endereço"));
            } else {
                System.out.println("❌ buscarPorId() falhou: Usuário não retornado.");
            }

            // -------------------------------------------------------------------
            // 3. TESTE: buscaUsuarioPorCpf
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 3] Invocando buscaUsuarioPorCpf('" + cpfTeste + "')...");
            Usuario usuarioPorCpf = usuarioDAO.buscaUsuarioPorCpf(cpfTeste);

            if (usuarioPorCpf != null) {
                System.out.println(" buscaUsuarioPorCpf() teve sucesso: Encontrou " + usuarioPorCpf.getNomeUsuario() + " (ID " + usuarioPorCpf.getIdUsuario() + ")");
            } else {
                System.out.println("❌ buscaUsuarioPorCpf() falhou: CPF não retornado.");
            }

            // -------------------------------------------------------------------
            // 4. TESTE: atualizarUsuario
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 4] Invocando atualizarUsuario()...");
            usuario.setNomeUsuario("Roberto Santos Atualizado");
            usuario.setCargoUsuario("Coordenador de TI");
            usuario.setTelefoneUsuario("51988881111");

            usuarioDAO.atualizarUsuario(usuario);
            
            // Confirmação no banco
            Usuario usuarioAtualizado = usuarioDAO.buscarPorId(usuario.getIdUsuario());
            System.out.println(" atualizarUsuario() executado! Novo Cargo verificado: " + usuarioAtualizado.getCargoUsuario() + " | Nome: " + usuarioAtualizado.getNomeUsuario());

            // -------------------------------------------------------------------
            // 5. TESTE: cadastrarLogUsuario
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 5] Invocando cadastrarLogUsuario()...");
            try {
                usuarioDAO.cadastrarLogUsuario("Alteração de cargo para Coordenador de TI", usuario);
                System.out.println(" cadastrarLogUsuario() executado com sucesso!");
            } catch (Exception e) {
                System.out.println("⚠️ Nota no teste de Log: Certifique-se de que a tabela/classe LogAuditoria esteja configurada.");
                e.printStackTrace();
            }

            // -------------------------------------------------------------------
            // 6. TESTE: listarTodosAtivos
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 6] Invocando listarTodosAtivos()...");
            List<Usuario> listaAtivos = usuarioDAO.listarTodosAtivos();
            System.out.println(" listarTodosAtivos() retornou " + listaAtivos.size() + " usuário(s) ativo(s):");
            for (Usuario u : listaAtivos) {
                System.out.println("   - ID: " + u.getIdUsuario() + " | Nome: " + u.getNomeUsuario() + " | Ativo: " + u.isAtivoUsuario());
            }

            // -------------------------------------------------------------------
            // 7. TESTE: desativarUsuario
            // -------------------------------------------------------------------
            System.out.println("\n[TESTE 7] Invocando desativarUsuario(" + usuario.getIdUsuario() + ")...");
            usuarioDAO.desativarUsuario(usuario.getIdUsuario());

            Usuario usuarioDesativado = usuarioDAO.buscarPorId(usuario.getIdUsuario());
            System.out.println(" desativarUsuario() executado! Status ativo no banco: " + usuarioDesativado.isAtivoUsuario());

            // Conferindo se ele sumiu da lista de ativos
            List<Usuario> novaListaAtivos = usuarioDAO.listarTodosAtivos();
            boolean aindaPresente = novaListaAtivos.stream().anyMatch(u -> u.getIdUsuario() == usuario.getIdUsuario());
            System.out.println(" O usuário desativado ainda aparece na lista de ativos? " + (aindaPresente ? "SIM ❌" : "NÃO (Comportamento esperado)"));

            System.out.println("\n==========================================================");
            System.out.println("          TODOS OS TESTES FORAM FINALIZADOS!              ");
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