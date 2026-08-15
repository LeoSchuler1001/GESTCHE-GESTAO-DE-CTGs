package app; // Ajuste o pacote conforme o seu projeto

import dao.ConexaoBanco;
import dao.EnderecoDAO;
import dao.UsuarioDAO;
import model.Endereco;
import model.Usuario;

import java.sql.SQLException;

public class TesteUsuarioEndereco {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  TESTE COMPLETO: CADASTRAR ENDEREÇO E USUÁRIO   ");
        System.out.println("=================================================");

        ConexaoBanco conexao = new ConexaoBanco();

        try {
            // 1. Instanciando os DAOs
            EnderecoDAO enderecoDAO = new EnderecoDAO(conexao);
            UsuarioDAO usuarioDAO = new UsuarioDAO(conexao);

            // ==========================================
            // ETAPA 1: Cadastrar o Endereço
            // ==========================================
            System.out.println("\n[1] Cadastrando o endereço...");

            Endereco novoEndereco = new Endereco();
            novoEndereco.setRua("Avenida Brasil");
            novoEndereco.setNumero(1500);
            novoEndereco.setBairro("Centro");
            novoEndereco.setCidade("Porto Alegre");
            novoEndereco.setEstado("RS");
            novoEndereco.setCep("90000000");

            enderecoDAO.cadastrarEndereco(novoEndereco);
            System.out.println(" Endereço cadastrado com sucesso! ID gerado: " + novoEndereco.getIdEndereco());

            // ==========================================
            // ETAPA 2: Cadastrar o Usuário com o Endereço
            // ==========================================
            System.out.println("\n[2] Cadastrando o usuário e associando o endereço...");

            Usuario novoUsuario = new Usuario();
            novoUsuario.setCpfUsuario("58496327951"); // 11 dígitos para o CHECK do banco
            novoUsuario.setNomeUsuario("Ana Paula Ferreira");
            novoUsuario.setTelefoneUsuario("51988887777"); // 11 dígitos
            novoUsuario.setCargoUsuario("Gerente Financeiro");
            novoUsuario.setSenhaHash("$2a$12$K1d8VqN/89Q.F2G.bW9tveIkgDdf.qO7jG3jT5h8L8m7n6b5v4c3");
            novoUsuario.setRespostaSeguranca("Nome do animal de estimação");
            novoUsuario.setAtivoUsuario(true);

            // Associa o objeto endereço recém-criado (com ID já preenchido)
            novoUsuario.setEndereco(novoEndereco);

            usuarioDAO.cadastrarUsuario(novoUsuario);
            System.out.println(" Usuário cadastrado com sucesso! ID gerado: " + novoUsuario.getIdUsuario());

            // ==========================================
            // ETAPA 3: Consultar Usuário e Validar Associação
            // ==========================================
            System.out.println("\n[3] Buscando usuário pelo ID para validar dados e vínculo...");

            Usuario usuarioConsultado = usuarioDAO.buscaUsuarioPorCpf(novoUsuario.getCpfUsuario());

            if (usuarioConsultado != null) {
                System.out.println("\n=================================================");
                System.out.println("           DADOS DO USUÁRIO CARREGADO            ");
                System.out.println("=================================================");
                System.out.println("ID Usuário: " + usuarioConsultado.getIdUsuario());
                System.out.println("Nome:       " + usuarioConsultado.getNomeUsuario());
                System.out.println("CPF:        " + usuarioConsultado.getCpfUsuario());
                System.out.println("Telefone:   " + usuarioConsultado.getTelefoneUsuario());
                System.out.println("Cargo:      " + usuarioConsultado.getCargoUsuario());
                System.out.println("Ativo:      " + usuarioConsultado.isAtivoUsuario());

                // Dados vindos do EnderecoDAO através do UsuarioDAO
                if (usuarioConsultado.getEndereco() != null) {
                    Endereco end = usuarioConsultado.getEndereco();
                    System.out.println("\n-------------- ENDEREÇO VINCULADO --------------");
                    System.out.println("ID Endereço: " + end.getIdEndereco());
                    System.out.println("Rua:         " + end.getRua() + ", Nº " + end.getNumero());
                    System.out.println("Bairro:      " + end.getBairro());
                    System.out.println("Cidade/UF:   " + end.getCidade() + " / " + end.getEstado());
                    System.out.println("CEP:         " + end.getCep());
                    System.out.println("------------------------------------------------");
                } else {
                    System.out.println("\n❌ Erro: O endereço não foi carregado junto com o usuário.");
                }
            } else {
                System.out.println("❌ Usuário não foi encontrado no banco de dados.");
            }

        } catch (SQLException e) {
            System.err.println("\n❌ Ocorreu um erro no Banco de Dados:");
            e.printStackTrace();
        } finally {
            try {
                if (conexao.getConexao() != null && !conexao.getConexao().isClosed()) {
                    conexao.getConexao().close();
                    System.out.println("\n Conexão com o banco encerrada.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}