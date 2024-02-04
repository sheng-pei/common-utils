package ppl.common.utils.http.header;//package com.dtstack.digitalize4.http.header;
//
//import com.dtstack.digitalize4.http.symbol.HttpCharGroup;
//import com.dtstack.digitalize4.utils.argument.Parser;
//import com.dtstack.digitalize4.utils.string.Strings;
//import com.dtstack.digitalize4.utils.string.kvpair.Pair;
//import com.dtstack.digitalize4.utils.string.trim.TrimPosition;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.Reader;
//import java.io.UncheckedIOException;
//import java.util.Iterator;
//import java.util.NoSuchElementException;
//import java.util.Spliterator;
//import java.util.Spliterators;
//import java.util.stream.Stream;
//import java.util.stream.StreamSupport;
//
////TODO, customize Reader to be compatible with varied format, for example headers from RFC7230.
////TODO, How to support error position.
////TODO, Only support standard header syntax??? Or extend Argument
//public class OneHeaderPerLineParser implements Parser {
//    private final boolean ignoreWhitespaceBetweenNameAndColon;
//    private final boolean errorIfBlankLineOccurs;
//
//    private OneHeaderPerLineParser(boolean errorIfBlankLineOccurs, boolean ignoreWhitespaceBetweenNameAndColon) {
//        this.ignoreWhitespaceBetweenNameAndColon = ignoreWhitespaceBetweenNameAndColon;
//        this.errorIfBlankLineOccurs = errorIfBlankLineOccurs;
//    }
//
//    public static Builder newBuilder() {
//        return new Builder();
//    }
//
//    @Override
//    public Stream<Fragment> parse(Reader reader) {
//        ReaderIter iter = new ReaderIter(reader);
//        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
//                iter, Spliterator.ORDERED | Spliterator.NONNULL), false);
//    }
//
//    @Override
//    public Stream<Fragment> parse(String name, String value) {
//        return Stream.of(checkAndNewFragment(name, value));
//    }
//
//    private Fragment checkAndNewFragment(String header) {
//        Pair<String, String> pair = Strings.kv(header, Header.SEPARATOR);
//        requiredSeparator(pair);
//        return checkAndNewFragment(pair.getFirst(), pair.getSecond());
//    }
//
//    private void requiredSeparator(Pair<String, String> pair) {
//        if (pair.getSecond() == null) {
//            throw new IllegalArgumentException(String.format(
//                    "Missing separator: '%s'.", Header.SEPARATOR));
//        }
//    }
//
//    private Fragment checkAndNewFragment(String name, String value) {
//        name = ignoreWhitespaceBetweenNameAndColon(name);
//        required(name);
//        trailingWhitespaceNotAllowed(name);
//        return newFragment(name, value);
//    }
//
//    private String ignoreWhitespaceBetweenNameAndColon(String name) {
//        if (ignoreWhitespaceBetweenNameAndColon) {
//            name = Strings.trim(name, HttpCharGroup.WS, TrimPosition.AFTER);
//        }
//        return name;
//    }
//
//    private void required(String name) {
//        if (name.isEmpty()) {
//            throw new IllegalArgumentException("Header name is required.");
//        }
//    }
//
//    private void trailingWhitespaceNotAllowed(String name) {
//        if (HttpCharGroup.WS.test(name.charAt(name.length() - 1))) {
//            throw new IllegalArgumentException(
//                    "Invalid header name. Whitespace is not allowed between name and separator.");
//        }
//    }
//
//    private Fragment newFragment(String name, String value) {
//        return new Fragment(Pair.create(name, value)) {
//            @Override
//            protected String merge(String key, String value) {
//                return key + Header.SEPARATOR + " " + value;
//            }
//        };
//    }
//
//    private class ReaderIter implements Iterator<Fragment> {
//        private final BufferedReader reader;
//        private Fragment next = null;
//
//        private ReaderIter(Reader reader) {
//            this.reader = new BufferedReader(reader, 2048);
//        }
//
//        @Override
//        public boolean hasNext() {
//            if (next != null) {
//                return true;
//            } else {
//                try {
//                    next = readNext();
//                    return (next != null);
//                } catch (IOException e) {
//                    throw new UncheckedIOException(e);
//                }
//            }
//        }
//
//        @Override
//        public Fragment next() {
//            if (next != null || hasNext()) {
//                Fragment n = next;
//                next = null;
//                return n;
//            } else {
//                throw new NoSuchElementException();
//            }
//        }
//
//        private Fragment readNext() throws IOException {
//            String line = reader.readLine();
//            while (line != null) {
//                if (Strings.trim(line, HttpCharGroup.WS).isEmpty()) {
//                    if (errorIfBlankLineOccurs) {
//                        throw new IllegalStateException("One line only has whitespace.");
//                    } else {
//                        line = reader.readLine();
//                    }
//                } else {
//                    return checkAndNewFragment(line);
//                }
//            }
//            return null;
//        }
//
//    }
//
//    public static final class Builder {
//
//        private boolean errorIfBlankLineOccurs;
//        private boolean ignoreWhitespaceBetweenNameAndColon;
//
//        private Builder() {}
//
//        public Builder withErrorIfBlankLineOccurs(boolean errorIfBlankLineOccurs) {
//            this.errorIfBlankLineOccurs = errorIfBlankLineOccurs;
//            return this;
//        }
//
//        public Builder withIgnoreWhitespaceBetweenNameAndColon(boolean ignoreWhitespaceBetweenNameAndColon) {
//            this.ignoreWhitespaceBetweenNameAndColon = ignoreWhitespaceBetweenNameAndColon;
//            return this;
//        }
//
//        public OneHeaderPerLineParser build() {
//            return new OneHeaderPerLineParser(errorIfBlankLineOccurs, ignoreWhitespaceBetweenNameAndColon);
//        }
//
//    }
//
//}
