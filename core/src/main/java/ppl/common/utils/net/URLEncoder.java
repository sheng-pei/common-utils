package ppl.common.utils.net;

import ppl.common.utils.HexUtils;
import ppl.common.utils.character.ascii.AsciiGroup;
import ppl.common.utils.exception.UnreachableCodeException;
import ppl.common.utils.string.Strings;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

public class URLEncoder {

    private static final URLEncoder DEFAULT_ENCODER = URLEncoder.builder().build();

    private final Predicate<Character> dontNeedToEncode;
    private final boolean percentEncodingReserved;

    private URLEncoder(Predicate<Character> dontNeedToEncode, boolean percentEncodingReserved) {
        this.dontNeedToEncode = dontNeedToEncode;
        this.percentEncodingReserved = percentEncodingReserved;
    }

    public String parse(String string) {
        return parse(string, StandardCharsets.UTF_8);
    }

    public String parse(String string, Charset charset) {
        if (Strings.isEmpty(string)) {
            return string;
        }

        byte[] bytes = string.getBytes(charset);
        ByteArrayOutputStream stream = new ByteArrayOutputStream(bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            if ('%' == bytes[i] && percentEncodingReserved) {
                if (bytes.length - i > 2 &&
                        AsciiGroup.HEX_DIGIT.test((char) bytes[i+1]) &&
                        AsciiGroup.HEX_DIGIT.test((char) bytes[i+2])) {
                    stream.write(bytes, i, 3);
                    i += 2;
                    continue;
                }
            }

            if (dontNeedToEncode.test((char) bytes[i])) {
                stream.write(bytes[i]);
            } else {
                stream.write('%');
                stream.write(HexUtils.hex(bytes[i]).getBytes(), 0, 2);
            }
        }
        try {
            return stream.toString(charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new UnreachableCodeException(e);
        }
    }

    public static String encode(String string) {
        return encode(string, StandardCharsets.UTF_8);
    }

    public static String encode(String string, Charset charset) {
        return DEFAULT_ENCODER.parse(string, charset);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Predicate<Character> dontNeedToEncode = AsciiGroup.EMPTY;
        private boolean percentEncodingReserved;

        public Builder() {}

        public Builder setPercentEncodingReserved(boolean percentEncodingReserved) {
            this.percentEncodingReserved = percentEncodingReserved;
            return this;
        }

        public Builder or(Predicate<Character> dontNeedToEncode) {
            this.dontNeedToEncode = this.dontNeedToEncode.or(dontNeedToEncode);
            return this;
        }

        public URLEncoder build() {
            return new URLEncoder(dontNeedToEncode.or(URICharGroup.UNRESERVED), this.percentEncodingReserved);
        }
    }
}
