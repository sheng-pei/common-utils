package ppl.common.utils.security;

import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.GMCipherSpi;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.Objects;

public class BCECUtils {
    private static final String DEFAULT_STD_NAME = "sm2p256v1";
    private static final String DEFAULT_ALGORITHM = "EC";
    public static final String DEFAULT_PROVIDER = "BC";

    static {
        BCECUtils.register();
    }

    public static final class SM2InC1C3C2 extends GMCipherSpi {
        public SM2InC1C3C2() {
            super(new SM2Engine(SM2Engine.Mode.C1C3C2));
        }
    }

    public static void register() {
        BouncyCastleProvider provider = new BouncyCastleProvider();
        provider.addAlgorithm("Cipher.SM2InC1C3C2", SM2InC1C3C2.class.getName());
        Security.addProvider(provider);
    }

    public static KeyPair createBCECKeyPair() {
        return createBCECKeyPair(DEFAULT_ALGORITHM, DEFAULT_STD_NAME);
    }

    public static KeyPair createBCECKeyPair(String algorithm, String stdName) {
        Objects.requireNonNull(algorithm);
        Objects.requireNonNull(stdName);

        //使用标准名称创建EC参数生成的参数规范
        final ECGenParameterSpec sm2Spec = new ECGenParameterSpec(stdName);

        // 获取一个椭圆曲线类型的密钥对生成器
        try {
            final KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm, DEFAULT_PROVIDER);
            kpg.initialize(sm2Spec, SecureRandom.defStrong().sr());
            return kpg.generateKeyPair();
        } catch (NoStrongSecureRandomException e) {
            throw new ECErrorException(e.getMessage(), e);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new ECErrorException("EC algorithm error '" + algorithm + "'.", e);
        } catch (NoSuchProviderException e) {
            throw new ECErrorException("EC provider error '" + DEFAULT_PROVIDER + "'.", e);
        }
    }

    @Deprecated
    public static String encodePublicKey(BCECPublicKey publicKey) {
        return Hex.toHexString(publicKey.getQ().getEncoded(false));
    }

    @Deprecated
    public static String encodePrivateKey(BCECPrivateKey privateKey) {
        return privateKey.getS().toString(16);
    }

    @Deprecated
    public static BCECPublicKey publicKey(String hex) {
        return publicKey(hex, DEFAULT_ALGORITHM, DEFAULT_STD_NAME);
    }

    @Deprecated
    public static BCECPublicKey publicKey(String hex, String algorithm, String stdName) {
        Objects.requireNonNull(hex);

        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec(stdName);
        if (spec == null) {
            throw new ECErrorException("EC curve error '" + stdName + "'.");
        }

        ECNamedCurveSpec params = new ECNamedCurveSpec(stdName, spec.getCurve(), spec.getG(),
                spec.getN());
        ECPoint point = ECPointUtil.decodePoint(params.getCurve(), Hex.decode(hex));
        ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(point, params);
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm, DEFAULT_PROVIDER);
            return (BCECPublicKey) kf.generatePublic(pubKeySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new ECErrorException("EC algorithm error '" + algorithm + "'.", e);
        } catch (NoSuchProviderException e) {
            throw new ECErrorException("EC provider error '" + DEFAULT_PROVIDER + "'.", e);
        } catch (InvalidKeySpecException e) {
            throw new ECErrorException(e.getMessage(), e);
        }
    }

    @Deprecated
    public static BCECPrivateKey privateKey(String hex) {
        return privateKey(hex, DEFAULT_ALGORITHM, DEFAULT_STD_NAME);
    }

    @Deprecated
    public static BCECPrivateKey privateKey(String hex, String algorithm, String stdName) {
        Objects.requireNonNull(hex);

        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec(stdName);
        if (spec == null) {
            throw new ECErrorException("EC curve error '" + stdName + "'.");
        }

        ECNamedCurveSpec params = new ECNamedCurveSpec(stdName, spec.getCurve(), spec.getG(),
                spec.getN());
        ECPrivateKeySpec priKeySpec = new ECPrivateKeySpec(new BigInteger(hex, 16), params);
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm, DEFAULT_PROVIDER);
            return (BCECPrivateKey) kf.generatePrivate(priKeySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new ECErrorException("EC algorithm error '" + algorithm + "'.", e);
        } catch (NoSuchProviderException e) {
            throw new ECErrorException("EC provider error '" + DEFAULT_PROVIDER + "'.", e);
        } catch (InvalidKeySpecException e) {
            throw new ECErrorException(e.getMessage(), e);
        }
    }
}
