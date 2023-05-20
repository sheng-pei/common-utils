package ppl.common.utils.net;

import ppl.common.utils.HexUtils;
import ppl.common.utils.string.Strings;

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

        StringBuilder builder = new StringBuilder();
        byte[] bytes = string.getBytes(charset);
        for (int i = 0; i < bytes.length; i++) {
            if ('%' == bytes[i] && percentEncodingReserved) {
                if (bytes.length - i > 2 &&
                        MaskPredicate.HEX.test((char) bytes[i+1]) &&
                        MaskPredicate.HEX.test((char) bytes[i+2])) {
                    builder.append((char) bytes[i])
                            .append((char) bytes[i + 1])
                            .append((char) bytes[i + 2]);
                    i += 2;
                    continue;
                }
            }

            if (dontNeedToEncode.test((char) bytes[i])) {
                builder.append((char) bytes[i]);
            } else {
                builder.append('%').append(HexUtils.hex(bytes[i]));
            }
        }
        return builder.toString();
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
        private Predicate<Character> dontNeedToEncode;
        private boolean percentEncodingReserved;

        public Builder() {}

        public Builder setPercentEncodingReserved(boolean percentEncodingReserved) {
            this.percentEncodingReserved = percentEncodingReserved;
            return this;
        }

        public Builder or(Predicate<Character> dontNeedToEncode) {
            if (this.dontNeedToEncode == null) {
                this.dontNeedToEncode = dontNeedToEncode;
            } else {
                this.dontNeedToEncode = this.dontNeedToEncode.or(dontNeedToEncode);
            }
            return this;
        }

        public URLEncoder build() {
            dontNeedToEncode = dontNeedToEncode == null ? MaskPredicate.UNRESERVED : dontNeedToEncode;
            return new URLEncoder(dontNeedToEncode, this.percentEncodingReserved);
        }
    }
}
