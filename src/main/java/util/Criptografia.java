package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Criptografia {
    
    public static String gerarHash(String textoOriginal) {
        if (textoOriginal == null) {
            return null;
        }

        try {
            //instancia o algoritmo de hash
            MessageDigest criptografador = MessageDigest.getInstance("SHA-256");

            //converte a string em bytes e gera o hash do bytes
            byte[] hashBytes = criptografador.digest(textoOriginal.getBytes(StandardCharsets.UTF_8));

            //converte o array de bytes em uma String hexadecimal
            StringBuilder hexadString = new StringBuilder();
            for (byte b : hashBytes) {
                // 0xff & b converte o byte para unsigned int; %02x formata em 2 dígitos hexadecimais
                hexadString.append(String.format("%02x", b));
            }

            return hexadString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao buscar o algoritmo de criptografia SHA-256", e);
        }
    }
}