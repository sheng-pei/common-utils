package ppl.common.utils.security;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class DESUtils {

    private static final String DEFAULT_ALG = "DES/ECB/PKCS5Padding";

    public static SecretKey getKey(String s) throws Exception {
        SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");
        DESKeySpec ks = new DESKeySpec(s.getBytes());
        return kf.generateSecret(ks);
    }

    public static byte[] encrypt(SecretKey key, byte[] input) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_ALG);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(input);
        } catch (InvalidKeyException e) {
            throw new DESErrorException("Invalid key.", e);
        } catch (NoSuchAlgorithmException e) {
            throw new DESErrorException("No such algorithm.", e);
        } catch (NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            throw new DESErrorException("Block size or padding error.", e);
        }
    }

    public static byte[] decrypt(SecretKey key, byte[] input) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_ALG);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(input);
        } catch (InvalidKeyException e) {
            throw new DESErrorException("Invalid key.", e);
        } catch (NoSuchAlgorithmException e) {
            throw new DESErrorException("No such algorithm.", e);
        } catch (NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            throw new DESErrorException("Block size or padding error.", e);
        }
    }
}
