package app; // Ajuste o pacote se necessário

import dao.ConexaoBanco;
import dao.UsuarioDAO;
import model.Endereco;
import model.Usuario;

import java.sql.SQLException;

public class TesteUsuario {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO TESTE DO BANCO DE DADOS ===");

        // 1. Instancia a conexão
        ConexaoBanco conexao = new ConexaoBanco();

        try {
            // 2. Instancia o DAO de Usuário
            UsuarioDAO usuarioDAO = new UsuarioDAO(conexao);

            // ==========================================
            // TESTE 1: Cadastrar Usuário
            // ==========================================
            System.out.println("\n[1] Criando e cadastrando um novo usuário...");

            Usuario novoUsuario = new Usuario();
            
            // ATENÇÃO: Assegure-se de que o CPF tenha 11 dígitos numéricos para passar no CHECK do banco
            novoUsuario.setCpfUsuario("12345678901");
            novoUsuario.setNomeUsuario("Carlos Eduardo");
            novoUsuario.setTelefoneUsuario("11987654321"); // 10 ou 11 dígitos conforme a CONSTRAINT
            novoUsuario.setCargoUsuario("Administrador");
            novoUsuario.setSenhaHash("$2a$12$e8OfaO0p0Xv7bK6k5l3uMeo9v8X8V6Z7K7n8m9l0k1j2h3g4f5"); // Exemplo de Hash
            novoUsuario.setRespostaSeguranca("Meu primeiro carro");
            novoUsuario.setAtivoUsuario(true);

            // Se você já tem um endereço cadastrado no banco (ex: ID 1), descomente as linhas abaixo:
            // Endereco enderecoExistente = new Endereco();
            // enderecoExistente.setIdEndereco(1);
            // novoUsuario.setEndereco(enderecoExistente);

            // Salva no banco de dados
            usuarioDAO.cadastrarUsuario(novoUsuario);
            System.out.println(" Usuário cadastrado com sucesso! ID gerado: " + novoUsuario.getIdUsuario());

            // ==========================================
            // TESTE 2: Buscar Usuário por ID
            // ==========================================
            System.out.println("\n[2] Consultando o usuário cadastrado no banco...");

            int idParaBuscar = novoUsuario.getIdUsuario(); // Busca o mesmo ID recém-criado
            Usuario usuarioBuscado = usuarioDAO.buscarPorId(idParaBuscar);

            if (usuarioBuscado != null) {
                System.out.println(" Usuário encontrado com sucesso!");
                System.out.println("----------------------------------------");
                System.out.println("ID:       " + usuarioBuscado.getIdUsuario());
                System.out.println("Nome:     " + usuarioBuscado.getNomeUsuario());
                System.out.println("CPF:      " + usuarioBuscado.getCpfUsuario());
                System.out.println("Telefone: " + usuarioBuscado.getTelefoneUsuario());
                System.out.println("Cargo:    " + usuarioBuscado.getCargoUsuario());
                System.out.println("Ativo:    " + usuarioBuscado.isAtivoUsuario());

                // Detalhes do Endereço
                if (usuarioBuscado.getEndereco() != null) {
                    Endereco end = usuarioBuscado.getEndereco();
                    System.out.println("\n--- Dados do Endereço ---");
                    System.out.println("ID Endereço: " + end.getIdEndereco());
                    System.out.println("Rua:         " + end.getRua());
                    System.out.println("Número:      " + end.getNumero());
                    System.out.println("Bairro:      " + end.getBairro());
                    System.out.println("Cidade:      " + end.getCidade() + " / " + end.getEstado());
                    System.out.println("CEP:         " + end.getCep());
                } else {
                    System.out.println("\nEndereço: Nenhum endereço vinculado.");
                }
                System.out.println("----------------------------------------");
            } else {
                System.out.println("❌ Usuário com ID " + idParaBuscar + " não foi encontrado.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro durante as operações de banco de dados:");
            e.printStackTrace();
        } finally {
            // Fecha a conexão se sua classe ConexaoBanco tiver um método para isso
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