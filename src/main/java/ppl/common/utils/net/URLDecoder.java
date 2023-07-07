package ppl.common.utils.net;

import ppl.common.utils.HexUtils;
import ppl.common.utils.character.ascii.AsciiGroup;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class URLDecoder {
    private URLDecoder () {}

    public static String decode(String string) {
        return decode(string, StandardCharsets.UTF_8);
    }

    public static String decode(String string, Charset charset) {
        StringBuilder builder = new StringBuilder(string.length());
        ByteBuffer byteBuffer = ByteBuffer.allocate(string.length() / 3);
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            while (i < chars.length && '%' == chars[i]) {
                if (chars.length - i > 2 &&
                        AsciiGroup.HEX_DIGIT.test(chars[i+1]) &&
                        AsciiGroup.HEX_DIGIT.test(chars[i+2])) {
                    byteBuffer.put(HexUtils.aByte(chars[i+1], chars[i+2]));
                    i += 3;
                } else {
                    throw new IllegalArgumentException("Illegal escaped pattern.");
                }
            }
            byteBuffer.flip();
            builder.append(charset.decode(byteBuffer));
            byteBuffer.clear();

            if (i < chars.length) {
                if ('+' == chars[i]) {
                    builder.append(' ');
                } else {
                    builder.append(chars[i]);
                }
            }
        }
        return builder.toString();
    }
}
