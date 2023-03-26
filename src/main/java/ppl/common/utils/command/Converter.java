package ppl.common.utils.command;

import java.util.Optional;

@SuppressWarnings("unused")
public interface Converter<V> {
    Converter<String> IDENTITY = new Converter<String>() {
        @Override
        public String comment() {
            return "return as is";
        }

        @Override
        public Optional<String> convert(String value) {
            return Optional.of(value);
        }
    };

    Converter<Integer> INTEGER_CONVERTER = new Converter<Integer>() {
        @Override
        public String comment() {
            return "integer";
        }

        @Override
        public Optional<Integer> convert(String value) {
            if (value == null) {
                return Optional.empty();
            }
            return Optional.of(Integer.parseInt(value));
        }
    };

    String comment();
    Optional<V> convert(String value);
}
