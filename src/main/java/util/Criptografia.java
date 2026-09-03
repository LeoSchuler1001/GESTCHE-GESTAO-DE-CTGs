package util;

import org.mindrot.jbcrypt.BCrypt;

public class Criptografia {
    //FUNÇÕES
    public static String gerarHash(String senhaTextoPuro) {
        return BCrypt.hashpw(senhaTextoPuro, BCrypt.gensalt());
    }

    public static boolean verificar(String senhaTextoPuro, String hashArmazenado) {
        return BCrypt.checkpw(senhaTextoPuro, hashArmazenado);
    }
}