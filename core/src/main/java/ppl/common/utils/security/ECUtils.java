package ppl.common.utils.security;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

public class ECUtils {

    private static final String DEFAULT_CIPHER_ALGORITHM = "SM2InC1C3C2";
    private static final String DEFAULT_SIGN_ALGORITHM = "SHA256WITHSM2";
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
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM, DEFAULT_PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey, publicKey.getParams(), SecureRandom.defStrong().sr());
            cipher.update(data);
            return cipher.doFinal();
        } catch (Exception e) {
            throw new ECErrorException("Error: encrypt with ec public key.", e);
        }
    }

    public static byte[] decrypt(ECPrivateKey privateKey, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM, DEFAULT_PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, privateKey, privateKey.getParams(), SecureRandom.defStrong().sr());
            cipher.update(data);
            return cipher.doFinal();
        } catch (Exception e) {
            throw new ECErrorException("Error: decrypt with ec private key.", e);
        }
    }

    public static byte[] sign(ECPrivateKey privateKey, byte[] data) {
        try {
            Signature signature = Signature.getInstance(DEFAULT_SIGN_ALGORITHM, DEFAULT_PROVIDER);
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw new ECErrorException("Error: sign with ec private key.", e);
        }
    }

    public static boolean verifySignature(ECPublicKey publicKey, byte[] data, byte[] signature) {
        try {
            Signature s = Signature.getInstance(DEFAULT_SIGN_ALGORITHM, DEFAULT_PROVIDER);
            s.initVerify(publicKey);
            s.update(data);
            return s.verify(signature);
        } catch (Exception e) {
            throw new ECErrorException("Error: verify signature with ec public key.", e);
        }
    }
}
