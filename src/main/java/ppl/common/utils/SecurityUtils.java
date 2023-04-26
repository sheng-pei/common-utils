package ppl.common.utils;

import ppl.common.utils.exception.JavaLibraryException;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {
    public static String md5(byte[] bytes) {
        return HexUtils.hex(byteMd5(bytes));
    }

    public static String md5(String string) {
        return HexUtils.hex(byteMd5(string));
    }

    public static String md5(String string, Charset charset) {
        return HexUtils.hex(byteMd5(string, charset));
    }

    public static byte[] byteMd5(byte[] bytes) {
        try{
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            return md5.digest();
        } catch (NoSuchAlgorithmException e){
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
