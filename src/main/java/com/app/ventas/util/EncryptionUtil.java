package com.app.ventas.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.util.Base64;

@Component
public class EncryptionUtil {

    private static final String ALGORITHM = "DES";

    @Value("${app.encryption.secret:VentasApp}")
    private String secretKey;

    private static String staticSecretKey;

    @PostConstruct
    public void init() {
        staticSecretKey = secretKey;
    }

    private static SecretKey generarClave() throws Exception {
        DESKeySpec desKeySpec = new DESKeySpec(staticSecretKey.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        return keyFactory.generateSecret(desKeySpec);
    }

    public static String cifrar(String texto) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, generarClave());
            byte[] bytes = cipher.doFinal(texto.getBytes());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Error al cifrar con DES", e);
        }
    }

    public static String descifrar(String textoCifrado) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, generarClave());
            byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(textoCifrado));
            return new String(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Error al descifrar con DES", e);
        }
    }
}
