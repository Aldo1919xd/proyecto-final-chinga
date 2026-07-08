package com.app.ventas.util;

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

    private final SecretKey clave;

    public EncryptionUtil(@Value("${app.encryption.secret:VentasApp}") String secret) {
        try {
            byte[] keyBytes = secret.getBytes("UTF-8");
            if (keyBytes.length < 8) {
                byte[] padded = new byte[8];
                System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
                keyBytes = padded;
            }
            DESKeySpec desKeySpec = new DESKeySpec(keyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            this.clave = keyFactory.generateSecret(desKeySpec);
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar clave DES", e);
        }
    }

    public String cifrar(String texto) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, clave);
            byte[] bytes = cipher.doFinal(texto.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Error al cifrar con DES", e);
        }
    }

    public String descifrar(String textoCifrado) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, clave);
            byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(textoCifrado));
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Error al descifrar con DES", e);
        }
    }
}
