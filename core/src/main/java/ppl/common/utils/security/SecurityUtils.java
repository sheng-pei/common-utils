package ppl.common.utils.security;

import ppl.common.utils.bytes.Bytes;
import ppl.common.utils.exception.JavaLibraryException;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {

    public static String sha1(byte[] bytes) {
        return Bytes.hex(byteSha1(bytes));
    }

    public static String sha1(String string) {
        return Bytes.hex(byteSha1(string));
    }

    public static String sha1(String string, Charset charset) {
        return Bytes.hex(byteSha1(string, charset));
    }

    public static byte[] byteSha1(byte[] bytes) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            sha1.update(bytes);
            return sha1.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new JavaLibraryException("Sha1 algorithm not found.", e);
        }
    }

    public static byte[] byteSha1(String string) {
        return byteSha1(string, Charset.defaultCharset());
    }

    public static byte[] byteSha1(String string, Charset charset) {
        return byteSha1(string.getBytes(charset));
    }

    public static String md5(byte[] bytes) {
        return Bytes.hex(byteMd5(bytes));
    }

    public static String md5(String string) {
        return Bytes.hex(byteMd5(string));
    }

    public static String md5(String string, Charset charset) {
        return Bytes.hex(byteMd5(string, charset));
    }

    public static byte[] byteMd5(byte[] bytes) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            return md5.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new JavaLibraryException("Md5 algorithm not found.", e);
        }
    }

    public static byte[] byteMd5(String string) {
        return byteMd5(string, Charset.defaultCharset());
    }

    public static byte[] byteMd5(String string, Charset charset) {
        return byteMd5(string.getBytes(charset));
    }
}
