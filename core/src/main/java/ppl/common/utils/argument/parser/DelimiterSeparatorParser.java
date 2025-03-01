package ppl.common.utils.argument.parser;

import ppl.common.utils.pair.Pair;
import ppl.common.utils.pair.PairStream;
import ppl.common.utils.string.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DelimiterSeparatorParser implements StringParser<String, String> {
    private enum EmptyFragment {
        IGNORE,
        ERROR,
        RETAIN;
    }

    private static final char DEFAULT_DELIMITER = ';';
    private static final char DEFAULT_SEPARATOR = '=';
    private static final Function<String, String> TRIM = Strings::trim;
    private static final Function<String, String> IDENTITY = Function.identity();

    private final char delimiter;
    private final char separator;
    private final Function<String, String> keyFunc;
    private final Function<String, String> valueFunc;
    private final boolean errorKeyIfLeadingOrTrailingWhitespace;
    private final boolean errorValueIfLeadingOrTrailingWhitespace;
    private final EmptyFragment emptyFragment;

    private DelimiterSeparatorParser(char delimiter, char separator,
                                     Function<String, String> keyFunc,
                                     Function<String, String> valueFunc,
                                     boolean errorKeyIfLeadingOrTrailingWhitespace,
                                     boolean errorValueIfLeadingOrTrailingWhitespace,
                                     EmptyFragment emptyFragment) {
        this.delimiter = delimiter;
        this.separator = separator;
        this.keyFunc = keyFunc;
        this.valueFunc = valueFunc;
        this.errorKeyIfLeadingOrTrailingWhitespace = errorKeyIfLeadingOrTrailingWhitespace;
        this.errorValueIfLeadingOrTrailingWhitespace = errorValueIfLeadingOrTrailingWhitespace;
        this.emptyFragment = emptyFragment;
    }

    @Override
    public Stream<Fragment<String, String>> parse(Reader reader) {
        Iterator<String> iter = new ReaderIter(reader);
        Stream<String> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                iter, Spliterator.ORDERED | Spliterator.NONNULL), false);
        return map(stream);
    }

    private Stream<Fragment<String, String>> map(Stream<String> stream) {
        return PairStream.create(stream, s -> Strings.kv(s, separator))
                .pmap(keyFunc, valueFunc)
                .pmap(k -> {
                    errorIfLeadingOrTrailingWhitespace(k, errorKeyIfLeadingOrTrailingWhitespace);
                    return k;
                }, v -> {
                    errorIfLeadingOrTrailingWhitespace(v, errorValueIfLeadingOrTrailingWhitespace);
                    return v;
                })
                .filter(this::processEmptyFragment)
                .map(this::newFragment);
    }

    private boolean processEmptyFragment(Pair<String, String> pair) {
        if (emptyFragment == EmptyFragment.RETAIN || emptyFragment == null) {
            return true;
        }

        if (isEmpty(pair)) {
            if (emptyFragment == EmptyFragment.ERROR) {
                throw new IllegalArgumentException("Empty fragment.");
            }
            return false;
        }
        return true;
    }

    private boolean isEmpty(Pair<String, String> pair) {
        return pair.getFirst().isEmpty() && pair.getSecond() == null;
    }

    private void errorIfLeadingOrTrailingWhitespace(String string, boolean error) {
        if (error) {
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

    private Fragment<String, String> newFragment(Pair<String, String> pair) {
        return new Fragment<String, String>(pair) {
            @Override
            protected String join(String key, String value) {
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
        return new DelimiterSeparatorParser(delimiter, separator,
                IDENTITY, IDENTITY,
                true, true,
                EmptyFragment.ERROR);
    }

    @SuppressWarnings("unused")
    public static DelimiterSeparatorParser defaultCompactAttributeParser() {
        return compactAttributeParser(DEFAULT_DELIMITER, DEFAULT_SEPARATOR);
    }

}