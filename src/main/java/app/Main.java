package app;

import model.Endereco;
import model.Usuario;

public class Main {
    public static void main(String[] args) {
        // 1. Instanciando um endereço
        Endereco endereco = new Endereco(
            1,
            "Rua das Flores",
            123,
            "96800-000",
            "Centro",
            "Santa Cruz do Sul",
            "RS"
        );

        // 2. Instanciando um usuário associado ao endereço criado
        Usuario usuario = new Usuario(
            1,
            "123.456.789-00",
            "Leonardo Schuler",
            "(51) 99999-8888",
            endereco,
            "Administrador",
            "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3", // Exemplo de hash SHA-256
            "Rex" // Resposta para a pergunta de segurança
        );

        // 3. Testando o método exibirDados() do Endereço
        System.out.println("=== DADOS DO ENDEREÇO ===");
        System.out.println(endereco.exibirDados());
        
        System.out.println("\n-----------------------------------\n");

        // 4. Testando o método exibirDados() do Usuário (que já inclui o endereço internamente)
        System.out.println("=== DADOS DO USUÁRIO ===");
        System.out.println(usuario.exibirDados());
    }
}