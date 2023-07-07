package ppl.common.utils.argument;

import ppl.common.utils.string.kvpair.Pair;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Stream;

public interface Parser {
    default Stream<Fragment> parse(InputStream is) {
        return parse(is, StandardCharsets.UTF_8);
    }
    default Stream<Fragment> parse(InputStream is, Charset charset) {
        Reader reader = new InputStreamReader(is, charset);
        return parse(reader);
    }
    default Stream<Fragment> parse(String source) {
        Reader reader = new StringReader(source);
        return parse(reader);
    }
    Stream<Fragment> parse(Reader reader);

    Stream<Fragment> parse(String name, String value);

    abstract class Fragment {
        private final String key;
        private final String value;

        public Fragment(Pair<String, String> pair) {
            this.key = pair.getFirst();
            this.value = pair.getSecond();
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public String toString() {
            return merge(key, value);
        }

        /**
         * Get Canonical string. Given a Parser A, if a Fragment M is come from A then
         * when the method parse of A is called with the canonical string of M,
         * the parse method will return a Fragment which is equals to M.
         * @return Canonical string.
         */
        protected abstract String merge(String key, String value);

        @Override
        public final boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Fragment fragment = (Fragment) o;
            return Objects.equals(key, fragment.key) && Objects.equals(value, fragment.value);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(key, value);
        }
    }
}
