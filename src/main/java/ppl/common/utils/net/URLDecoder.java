package ppl.common.utils.net;

import ppl.common.utils.HexUtils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class URLDecoder {
    private URLDecoder () {}

    public static String decode(String string) {
        return decode(string, StandardCharsets.UTF_8);
    }

    public static String decode(String string, Charset charset) {
        StringBuilder builder = new StringBuilder();
        int pos = 0;
        byte[] escaped = new byte[string.length() / 3];
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars.length - i > 2 && '%' == chars[i] &&
                    URICharacter.HEX.test(chars[i+1]) && URICharacter.HEX.test(chars[i+2])) {
                escaped[pos++] = HexUtils.aByte(chars[i+1], chars[i+2]);
                i += 2;
            } else {
                if (pos > 0) {
                    builder.append(charset.decode(ByteBuffer.wrap(escaped, 0, pos)));
                    pos = 0;
                }
                builder.append(chars[i]);
            }
        }
        if (pos > 0) {
            builder.append(charset.decode(ByteBuffer.wrap(escaped, 0, pos)));
        }
        return builder.toString();
    }
}
