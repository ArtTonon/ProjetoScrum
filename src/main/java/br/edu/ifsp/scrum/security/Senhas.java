package br.edu.ifsp.scrum.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Senhas {
    private static final int ITERACOES = 210_000;
    private static final int BYTES = 32;
    private static final SecureRandom RANDOM = new SecureRandom();
    private Senhas() {}

    public static String gerarHash(String senha) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        byte[] hash = derivar(senha, salt, ITERACOES);
        return "pbkdf2_sha256$" + ITERACOES + "$" +
            Base64.getEncoder().encodeToString(salt) + "$" +
            Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(String senha, String armazenada) {
        if (armazenada == null || senha == null) return false;
        if (!armazenada.startsWith("pbkdf2_sha256$")) {
            // Compatibilidade com bancos antigos; a migracao ocorre na inicializacao.
            return MessageDigest.isEqual(senha.getBytes(StandardCharsets.UTF_8),
                armazenada.getBytes(StandardCharsets.UTF_8));
        }
        try {
            String[] partes = armazenada.split("\\$", -1);
            if (partes.length != 4) return false;
            int iteracoes = Integer.parseInt(partes[1]);
            if (iteracoes < 100_000 || iteracoes > 1_000_000) return false;
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            return MessageDigest.isEqual(esperado, derivar(senha, salt, iteracoes));
        } catch (RuntimeException e) { return false; }
    }

    private static byte[] derivar(String senha, byte[] salt, int iteracoes) {
        PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), salt, iteracoes, BYTES * 8);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("Falha na protecao de senhas", e);
        } finally { spec.clearPassword(); }
    }
}
