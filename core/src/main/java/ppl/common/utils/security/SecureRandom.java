package ppl.common.utils.security;

import ppl.common.utils.exception.UnreachableCodeException;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicInteger;

public class SecureRandom {

    private static final SecureRandom DEFAULT_SECURE_RANDOM = new SecureRandom();
    private static final SecureRandom DEFAULT_STRONG_SECURE_RANDOM;

    public static SecureRandom def() {
        return DEFAULT_SECURE_RANDOM;
    }

    public static SecureRandom defStrong() {
        if (DEFAULT_STRONG_SECURE_RANDOM == null) {
            throw new NoStrongSecureRandomException("No strong secure random algorithm.");
        }
        return DEFAULT_STRONG_SECURE_RANDOM;
    }

    private static final int DEFAULT_RESEED = 20;

    static {
        SecureRandom strong = null;
        try {
            strong = new SecureRandom(java.security.SecureRandom.getInstanceStrong(), 1, true);
        } catch (NoSuchAlgorithmException e) {
            //ignore
        }
        DEFAULT_STRONG_SECURE_RANDOM = strong;
    }

    private final AtomicInteger count;
    private final int reseed;
    private final boolean strong;
    private volatile java.security.SecureRandom sr;

    public SecureRandom() {
        this(new java.security.SecureRandom(), DEFAULT_RESEED, false);
    }

    private SecureRandom(java.security.SecureRandom secureRandom, int reseed, boolean strong) {
        this.count = new AtomicInteger();
        this.reseed = reseed;
        this.sr = secureRandom;
        this.strong = strong;
    }

    public byte[] nextBytes(int size) {
        java.security.SecureRandom sr = sr();
        byte[] bytes = new byte[size];
        sr.nextBytes(bytes);
        return bytes;
    }

    public java.security.SecureRandom sr() {
        int cnt = count.incrementAndGet();
        if (cnt % reseed == 0) {
            return reseed();
        } else {
            return sr;
        }
    }

    private java.security.SecureRandom reseed() {
        if (strong) {
            synchronized (this) {
                return pReseed();
            }
        } else {
            return pReseed();
        }
    }

    private java.security.SecureRandom pReseed() {
        java.security.SecureRandom sr = this.sr;
        try {
            this.sr = java.security.SecureRandom.getInstance(sr.getAlgorithm(), sr.getProvider());
        } catch (NoSuchAlgorithmException e) {
            throw new UnreachableCodeException("Reseed SecureRandom from existing instance.", e);
        }
        return sr;
    }

}
