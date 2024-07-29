package ppl.common.utils.security;

import javax.crypto.Cipher;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

public class ECUtils {

    private static final String DEFAULT_ALGORITHM = "SM2InC1C3C2";
    private static final String DEFAULT_PROVIDER = BCECUtils.DEFAULT_PROVIDER;

    static {
        //initialize BCECUtils
        try {
            Class.forName(BCECUtils.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] encrypt(ECPublicKey publicKey, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_ALGORITHM, DEFAULT_PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey, publicKey.getParams(), SecureRandom.defStrong().sr());
            cipher.update(data);
            return cipher.doFinal();
        } catch (Exception e) {
            throw new ECErrorException("Error: encrypt with ec public key.", e);
        }
    }

    public static byte[] decrypt(ECPrivateKey privateKey, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_ALGORITHM, DEFAULT_PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, privateKey, privateKey.getParams(), SecureRandom.defStrong().sr());
            cipher.update(data);
            return cipher.doFinal();
        } catch (Exception e) {
            throw new ECErrorException("Error: decrypt with ec private key.", e);
        }
    }
}
