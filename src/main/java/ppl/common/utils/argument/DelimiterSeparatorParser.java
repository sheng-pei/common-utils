package ppl.common.utils.argument;

import ppl.common.utils.string.Strings;
import ppl.common.utils.string.kvpair.Pair;
import ppl.common.utils.string.trim.TrimPosition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DelimiterSeparatorParser implements Parser {
    private static final char DEFAULT_DELIMITER = ';';
    private static final char DEFAULT_SEPARATOR = '=';

    private final char delimiter;
    private final char separator;
    private final boolean ignoreEmptyFragment;
    private final boolean ignoreIWhitespace;
    private final boolean ignoreLWhitespace;
    private final boolean ignoreRWhitespace;
    private final boolean errorIfLeadingOrTrailingWhitespace;

    private DelimiterSeparatorParser(char delimiter, char separator,
                                     boolean ignoreEmptyFragment,
                                     boolean ignoreIWhitespace,
                                     boolean ignoreLWhitespace,
                                     boolean ignoreRWhitespace,
                                     boolean errorIfLeadingOrTrailingWhitespace) {
        this.delimiter = delimiter;
        this.separator = separator;
        this.ignoreEmptyFragment = ignoreEmptyFragment;
        this.ignoreIWhitespace = ignoreIWhitespace;
        this.ignoreLWhitespace = ignoreLWhitespace;
        this.ignoreRWhitespace = ignoreRWhitespace;
        this.errorIfLeadingOrTrailingWhitespace = errorIfLeadingOrTrailingWhitespace;
    }

    @Override
    public Stream<Fragment> parse(Reader reader) {
        Iterator<String> iter = new ReaderIter(reader);
        Stream<String> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                iter, Spliterator.ORDERED | Spliterator.NONNULL), false);
        return map(stream);
    }

    @Override
    public Stream<Fragment> parse(String name, String value) {
        Objects.requireNonNull(name, "Name is required.");
        return Stream.of(checkAndNewFragment(Pair.create(name, value)));
    }

    private Stream<Fragment> map(Stream<String> stream) {
        return stream
                .map(s -> ignoreIWhitespace ? Strings.trim(s) : s)
                .map(s -> Strings.kv(s, separator))
                .map(this::postProcess)
                .filter(p -> !ignoreEmptyFragment || !isEmpty(p))
                .map(this::newFragment);
    }

    private boolean isEmpty(Pair<String, String> pair) {
        return pair.getFirst().isEmpty() && pair.getSecond() == null;
    }

    private Fragment checkAndNewFragment(Pair<String, String> pair) {
        return newFragment(postProcess(pair));
    }

    private Pair<String, String> postProcess(Pair<String, String> pair) {
        String name = ignoreLWhitespace(pair.getFirst());
        String value = ignoreRWhitespace(pair.getSecond());
        errorIfLeadingOrTrailingWhitespace(name);
        errorIfLeadingOrTrailingWhitespace(value);
        return Pair.create(name, value);
    }

    private String ignoreLWhitespace(String k) {
        if (ignoreLWhitespace) {
            k = Strings.trim(k, TrimPosition.AFTER);
        }
        return k;
    }

    private String ignoreRWhitespace(String v) {
        if (v == null) {
            return null;
        }

        if (ignoreRWhitespace) {
            v = Strings.trim(v, TrimPosition.BEFORE);
        }
        return v;
    }

    private void errorIfLeadingOrTrailingWhitespace(String string) {
        if (errorIfLeadingOrTrailingWhitespace) {
            checkIfLeadingOrTrailingWhitespace(string);
        }
    }

    private void checkIfLeadingOrTrailingWhitespace(String string) {
        if (string == null) {
            return;
        }

        if (Strings.trim(string).length() < string.length()) {
            throw new IllegalArgumentException("Has leading or trailing whitespace.");
        }
    }

    private Fragment newFragment(Pair<String, String> pair) {
        return new Fragment(pair) {
            @Override
            protected String merge(String key, String value) {
                if (value != null) {
                    return key + separator + value;
                } else {
                    return key;
                }
            }
        };
    }

    private class ReaderIter implements Iterator<String> {
        private final BufferedReader reader;
        private String next = null;

        private ReaderIter(Reader reader) {
            this.reader = new BufferedReader(reader, 2048);
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
        public String next() {
            if (next != null || hasNext()) {
                String n = next;
                next = null;
                return n;
            } else {
                throw new NoSuchElementException();
            }
        }

        private String readNext() throws IOException {
            StringBuilder builder = new StringBuilder();
            int c = reader.read();
            while (c >= 0) {
                if (c != delimiter) {
                    builder.append((char) c);
                } else {
                    return builder.toString();
                }
                c = reader.read();
            }
            return builder.length() == 0 ? null : builder.toString();
        }
    }

    public static DelimiterSeparatorParser compactAttributeParser(char delimiter, char separator) {
        return new DelimiterSeparatorParser(delimiter, separator, false,
                true, false, false,
                true);
    }

    @SuppressWarnings("unused")
    public static DelimiterSeparatorParser defaultCompactAttributeParser() {
        return compactAttributeParser(DEFAULT_DELIMITER, DEFAULT_SEPARATOR);
    }

}