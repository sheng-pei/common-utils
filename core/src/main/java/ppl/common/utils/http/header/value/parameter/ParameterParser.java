package ppl.common.utils.http.header.value.parameter;

import ppl.common.utils.argument.parser.Fragment;
import ppl.common.utils.argument.parser.NameValueParser;
import ppl.common.utils.argument.parser.StringParser;
import ppl.common.utils.http.symbol.HttpCharGroup;
import ppl.common.utils.http.symbol.Lexer;
import ppl.common.utils.string.Strings;
import ppl.common.utils.pair.Pair;
import ppl.common.utils.string.TrimPosition;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class ParameterParser implements StringParser<String, String>, NameValueParser<String, String> {

    public static final ParameterParser DEFAULT = newBuilder()
            .ignoreLWhitespace(false)
            .ignoreRWhitespace(false)
            .build();

    private final boolean ignoreLWhitespace;
    private final boolean ignoreRWhitespace;

    private ParameterParser(boolean ignoreLWhitespace,
                            boolean ignoreRWhitespace) {
        this.ignoreLWhitespace = ignoreLWhitespace;
        this.ignoreRWhitespace = ignoreRWhitespace;
    }

    @Override
    public Stream<Fragment<String, String>> parse(Reader reader) {
        Iterator<Fragment<String, String>> iter = new ReaderIter(reader);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                iter, Spliterator.ORDERED | Spliterator.NONNULL), false);
    }

    @Override
    public Stream<Fragment<String, String>> parse(String name, String value) {
        return Stream.of(checkAndNewFragment(name, value));
    }

    private Fragment<String, String> checkAndNewFragment(String name, String value) {
        name = postProcessName(name);
        value = postProcessValue(value, true);
        return newFragment(name, value);
    }

    private Fragment<String, String> newFragment(String name, String value) {
        return new Fragment<String, String>(Pair.create(name, value)) {
            @Override
            protected String join(String key, String value) {
                return key + ParameterizedHeaderValue.SEPARATOR + value;
            }
        };
    }

    private String postProcessName(String name) {
        name = ignoreLeadingWhitespace(name, true);
        name = ignoreTrailingWhitespace(name, ignoreLWhitespace);
        required(name, "Parameter name is required.");
        trailingWhitespaceNotAllowed(name);
        mustToken(name, "Invalid parameter name. Invalid token :'%s'.");
        return name;
    }

    private String ignoreLeadingWhitespace(String string, boolean ignore) {
        if (ignore) {
            string = Strings.trim(string, HttpCharGroup.WS, TrimPosition.BEFORE);
        }
        return string;
    }

    private String ignoreTrailingWhitespace(String string, boolean ignore) {
        if (ignore) {
            string = Strings.trim(string, HttpCharGroup.WS, TrimPosition.AFTER);
        }
        return string;
    }

    private void required(String string, String message) {
        if (string.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void trailingWhitespaceNotAllowed(String name) {
        if (HttpCharGroup.WS.test(name.charAt(name.length() - 1))) {
            throw new IllegalArgumentException(
                    "Invalid parameter name. Whitespace is not allowed between name and separator.");
        }
    }

    private void mustToken(String string, String message) {
        if (!Lexer.isToken(string)) {
            throw new IllegalArgumentException(String.format(message, string));
        }
    }

    private String postProcessValue(String value, boolean check) {
        value = ignoreLeadingWhitespace(value, ignoreRWhitespace);
        value = ignoreTrailingWhitespace(value, true);
        required(value, "Parameter value is required.");
        leadingWhitespaceNotAllowed(value);
        return mustTokenOrQuotedString(value, check);
    }

    private void leadingWhitespaceNotAllowed(String value) {
        if (HttpCharGroup.WS.test(value.charAt(0))) {
            throw new IllegalArgumentException(
                    "Invalid parameter value. Whitespace is not allowed between separator and value.");
        }
    }

    private String mustTokenOrQuotedString(String value, boolean checkQuotedString) {
        if (!HttpCharGroup.QM.test(value.charAt(0))) {
            mustToken(value, "Invalid parameter value. Invalid token :'%s'.");
        } else if (checkQuotedString) {
            mustQuotedString(value);
        }
        return value;
    }

    private void mustQuotedString(String value) {
        if (!Lexer.isQuotedString(value)) {
            throw new IllegalArgumentException(
                    "Invalid parameter value. Invalid quoted string :'" + value + "'.");
        }
    }

    private class ReaderIter implements Iterator<Fragment<String, String>> {
        private final BufferedReader reader;
        private final CharBuffer buffer;
        private StringBuilder fragment;
        private int curr;
        private Fragment<String, String> next = null;

        private ReaderIter(InputStream is, Charset charset) {
            this(new BufferedReader(new InputStreamReader(is, charset)));
        }

        private ReaderIter(Reader reader) {
            this.reader = new BufferedReader(reader);
            CharBuffer buffer = CharBuffer.allocate(1024);
            buffer.flip();
            this.buffer = buffer;
        }

        @Override
        public boolean hasNext() {
            if (next != null) {
                return true;
            } else {
                try {
                    next = readNext();
                    return (next != null);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }

        @Override
        public Fragment<String, String> next() {
            if (next != null || hasNext()) {
                Fragment<String, String> n = next;
                next = null;
                return n;
            } else {
                throw new NoSuchElementException();
            }
        }

        private Fragment<String, String> readNext() throws IOException {
            while (true) {
                try {
                    resetFragment();
                    int separatorIdx = separatorIdx();
                    if (separatorIdx <= -2) {
                        return null;
                    }
                    if (separatorIdx == -1) {
                        String string = this.fragment.toString();
                        string = getRidOfDelimiter(string);
                        string = Strings.trim(string, HttpCharGroup.WS);
                        requiredSeparator(string);
                        continue;
                    }
                    return checkAndNewFragment(0, separatorIdx, endOfValue());
                } finally {
                    this.fragment = null;
                }
            }
        }

        private void resetFragment() {
            this.fragment = new StringBuilder(32);
            this.curr = -1;
        }

        private int separatorIdx() throws IOException {
            int count = fillBuffer();
            while (count >= 0) {
                CharBuffer buffer = this.buffer;
                while (buffer.hasRemaining()) {
                    char c = get(buffer);
                    if (c == ParameterizedHeaderValue.SEPARATOR) {
                        return curr;
                    } else if (c == ParameterizedHeaderValue.DELIMITER) {
                        return -1;
                    }
                }
                count = fillBuffer();
            }
            return curr < 0 ? -2 : -1;
        }

        private String getRidOfDelimiter(String string) {
            if (!string.isEmpty()) {
                char last = string.charAt(string.length() - 1);
                if (last == ParameterizedHeaderValue.DELIMITER) {
                    string = string.substring(0, string.length() - 1);
                }
            }
            return string;
        }

        private void requiredSeparator(String string) {
            if (!string.isEmpty()) {
                throw new IllegalArgumentException("Invalid parameter, no separator.");
            }
        }

        private int endOfValue() throws IOException {
            if (fillBuffer() < 0) {
                return curr + 1;
            }

            if (swallowWhitespace() < 0) {
                return curr + 1;
            }

            char c = fragment.charAt(curr);
            if (ParameterizedHeaderValue.DELIMITER == c) {
                return curr;
            } else if (HttpCharGroup.QM.test(c)) {
                return endOfQuotedStringValue();
            } else {
                return finishParameter();
            }
        }

        private int swallowWhitespace() throws IOException {
            int count = fillBuffer();
            while (count >= 0) {
                CharBuffer buffer = this.buffer;
                while (buffer.hasRemaining()) {
                    if (!HttpCharGroup.WS.test(get(buffer))) {
                        return curr;
                    }
                }
                count = fillBuffer();
            }
            return -1;
        }

        private int endOfQuotedStringValue() throws IOException {
            int endQuotedStringIdx = endOfQuotedString();
            int end = finishParameter();
            requiredWhitespaceAfterQuotedString(endQuotedStringIdx, end);
            return end;
        }

        private int endOfQuotedString() throws IOException {
            boolean quoted = false;
            int count = fillBuffer();
            while (count >= 0) {
                CharBuffer buffer = this.buffer;
                while (buffer.hasRemaining()) {
                    if (quoted) {
                        if (HttpCharGroup.QUOTED_TEXT.test(get(buffer))) {
                            quoted = false;
                        } else {
                            throw new IllegalArgumentException("Invalid quoted pair.");
                        }
                    } else {
                        char c = get(buffer);
                        if (HttpCharGroup.BS.test(c)) {
                            quoted = true;
                        } else if (HttpCharGroup.QM.test(c)) {
                            return curr + 1;
                        } else if (!HttpCharGroup.QDTEXT.test(c)) {
                            throw new IllegalArgumentException("Invalid quoted text.");
                        }
                    }
                }
                count = fillBuffer();
            }
            throw new IllegalArgumentException("Incomplete quoted string.");
        }

        private int finishParameter() throws IOException {
            int count = fillBuffer();
            while (count >= 0) {
                CharBuffer buffer = this.buffer;
                while (buffer.hasRemaining()) {
                    char c = get(buffer);
                    if (c == ParameterizedHeaderValue.DELIMITER) {
                        return curr;
                    }
                }
                count = fillBuffer();
            }
            return curr + 1;
        }

        private void requiredWhitespaceAfterQuotedString(int endQuotedStringIdx, int end) {
            if (!Lexer.isWhitespace(fragment.substring(endQuotedStringIdx, end))) {
                throw new IllegalArgumentException("Invalid parameter value. " +
                        "Nonwhitespace character is not allowed between quoted string and delimiter.");
            }
        }

        private Fragment<String, String> checkAndNewFragment(String name, String value) {
            name = postProcessName(name);
            value = postProcessValue(value, true);
            return newFragment(name, value);
        }

        private Fragment<String, String> checkAndNewFragment(int begin, int separatorIdx, int endOfValue) {
            String parameter = fragment.toString();
            String name = getName(parameter, begin, separatorIdx);
            String value = getValue(parameter, separatorIdx + 1, endOfValue);
            return newFragment(name, value);
        }

        private String getName(String parameter, int begin, int end) {
            return postProcessName(parameter.substring(begin, end));
        }

        private String getValue(String parameter, int begin, int end) {
            return postProcessValue(parameter.substring(begin, end), false);
        }

        private int fillBuffer() throws IOException {
            CharBuffer buffer = this.buffer;
            if (!buffer.hasRemaining()) {
                buffer.clear();
                int res = reader.read(buffer);
                buffer.flip();
                return res;
            }
            return buffer.remaining();
        }

        private char get(CharBuffer buffer) {
            char c = buffer.get();
            fragment.append(c);
            curr++;
            return c;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private boolean ignoreLWhitespace;
        private boolean ignoreRWhitespace;

        private Builder() {}

        public Builder ignoreLWhitespace(boolean bool) {
            this.ignoreLWhitespace = bool;
            return this;
        }

        public Builder ignoreRWhitespace(boolean bool) {
            this.ignoreRWhitespace = bool;
            return this;
        }

        public ParameterParser build() {
            return new ParameterParser(ignoreLWhitespace, ignoreRWhitespace);
        }

    }

}
