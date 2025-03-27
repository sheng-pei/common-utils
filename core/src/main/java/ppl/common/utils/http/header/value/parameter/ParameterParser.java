package ppl.common.utils.http.header.value.parameter;

import ppl.common.utils.argument.parser.Fragment;
import ppl.common.utils.argument.parser.StringParser;
import ppl.common.utils.http.symbol.HttpCharGroup;
import ppl.common.utils.http.symbol.Lexer;
import ppl.common.utils.pair.Pair;
import ppl.common.utils.string.Strings;
import ppl.common.utils.string.TrimPosition;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class ParameterParser implements StringParser<String, String> {

    public static final ParameterParser DEFAULT = newBuilder().build();

    private final boolean ignoreNameBadWhitespace;
    private final boolean ignoreValueBadWhitespace;

    private ParameterParser(boolean ignoreNameBadWhitespace,
                            boolean ignoreValueBadWhitespace) {
        this.ignoreNameBadWhitespace = ignoreNameBadWhitespace;
        this.ignoreValueBadWhitespace = ignoreValueBadWhitespace;
    }

    @Override
    public Stream<Fragment<String, String>> parse(Reader reader) {
        Iterator<Fragment<String, String>> iter = new ReaderIter(reader);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                iter, Spliterator.ORDERED | Spliterator.NONNULL), false);
    }

    public Stream<Fragment<String, String>> parse(String name, String value) {
        Objects.requireNonNull(name);
        if (!Lexer.isToken(name)) {
            throw new IllegalArgumentException("Name is not token.");
        }
        if (value != null && !Lexer.isToken(value) && !Lexer.isQuotedPairText(value)) {
            throw new IllegalArgumentException("Value cannot be quoted.");
        }
        return Stream.of(newFragment(name, value));
    }

    private Fragment<String, String> newFragment(String name, String value) {
        return new Fragment<String, String>(Pair.create(name, value)) {
            @Override
            protected String join(String key, String value) {
                return value == null ? key : key + ParameterizedHeaderValue.SEPARATOR + value;
            }
        };
    }

    private class ReaderIter implements Iterator<Fragment<String, String>> {
        private final BufferedReader reader;
        private final StringBuilder buffer;
        private Fragment<String, String> next = null;

        private ReaderIter(InputStream is, Charset charset) {
            this(new BufferedReader(new InputStreamReader(is, charset)));
        }

        private ReaderIter(Reader reader) {
            this.reader = new BufferedReader(reader);
            this.buffer = new StringBuilder();
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
            if (hasNext()) {
                try {
                    Fragment<String, String> ret = next;
                    next = readNext();
                    return ret;
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            } else {
                throw new NoSuchElementException();
            }
        }

        private Fragment<String, String> readNext() throws IOException {
            String name = null;
            String value = null;
            int c = reader.read();
            while (true) {
                if (name == null && c == ParameterizedHeaderValue.SEPARATOR) {
                    name = this.buffer.toString();
                    resetBuffer();
                } else if (c == ParameterizedHeaderValue.DELIMITER || c < 0) {
                    if (name == null) {
                        name = this.buffer.toString();
                    } else {
                        value = this.buffer.toString();
                    }
                    resetBuffer();
                    break;
                } else {
                    this.buffer.append((char) c);
                }
                c = reader.read();
            }

            name = Strings.trim(name, HttpCharGroup.WHITESPACE, TrimPosition.BEFORE);
            value = Strings.trim(value, HttpCharGroup.WHITESPACE, TrimPosition.AFTER);
            if (name.isEmpty() && value == null) {
                if (c < 0) {
                    return null;
                } else {
                    return readNext();
                }
            }

            if (ignoreNameBadWhitespace || value == null) {
                name = Strings.trim(name, HttpCharGroup.WHITESPACE, TrimPosition.AFTER);
            }
            if (ignoreValueBadWhitespace) {
                value = Strings.trim(value, HttpCharGroup.WHITESPACE, TrimPosition.BEFORE);
            }
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name is not exists.");
            }
            if (HttpCharGroup.WHITESPACE.test(name.charAt(name.length() - 1))) {
                throw new IllegalArgumentException(
                        "Invalid parameter name. Whitespace is not allowed between name and =.");
            }
            if (Strings.isNotEmpty(value) && HttpCharGroup.WHITESPACE.test(value.charAt(0))) {
                throw new IllegalArgumentException(
                        "Invalid parameter value. Whitespace is not allowed between = and value.");
            }
            if (!Lexer.isToken(name)) {
                throw new IllegalArgumentException("Name is not token.");
            }

            return newFragment(name, Lexer.readValue(value));
        }

        private void resetBuffer() {
            this.buffer.setLength(0);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private boolean ignoreNameBadWhitespace;
        private boolean ignoreValueBadWhitespace;

        private Builder() {}

        public Builder ignoreNameBadWhitespace(boolean bool) {
            this.ignoreNameBadWhitespace = bool;
            return this;
        }

        public Builder ignoreValueBadWhitespace(boolean bool) {
            this.ignoreValueBadWhitespace = bool;
            return this;
        }

        public ParameterParser build() {
            return new ParameterParser(ignoreNameBadWhitespace, ignoreValueBadWhitespace);
        }

    }

}
