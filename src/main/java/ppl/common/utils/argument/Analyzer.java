package ppl.common.utils.argument;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class Analyzer<K> {
    private final Parser parser;
    private final Function<String, K> keyGenerator;
    private final Map<K, Argument<K, Object>> arguments;

    private Analyzer(Parser parser,
                     Function<String, K> keyGenerator,
                     Map<K, Argument<K, Object>> arguments) {
        Objects.requireNonNull(parser, "Parser is required.");
        Objects.requireNonNull(keyGenerator, "Key generator is required.");
        this.parser = parser;
        this.keyGenerator = keyGenerator;
        this.arguments = arguments;
    }

    public List<ArgumentValue<K, Object>> analyse(InputStream is) {
        Stream<Parser.Fragment> entryStream = parser.parse(is);
        return analyse(entryStream);
    }

    public List<ArgumentValue<K, Object>> analyse(String source) {
        Stream<Parser.Fragment> entryStream = parser.parse(source);
        return analyse(entryStream);
    }

    public List<ArgumentValue<K, Object>> analyse(String name, String value) {
        Stream<Parser.Fragment> entryStream = parser.parse(name, value);
        return analyse(entryStream);
    }

    private List<ArgumentValue<K, Object>> analyse(Stream<Parser.Fragment> entryStream) {
        List<ArgumentValue<K, Object>> res = new ArrayList<>();
        Map<K, FeedingStream<Object>> feedingStreams = new HashMap<>();
        entryStream.forEach(e -> {
            K key = keyGenerator.apply(e.getKey());
            Argument<K, Object> argument = arguments.get(key);
            if (argument == null) {
                res.add(new ArgumentValue<>(null, e));
            } else {
                feedingStreams
                        .computeIfAbsent(argument.getName(), k -> arguments.get(k).stream())
                        .feed(e.getValue());
            }
        });

        feedingStreams.forEach((key, value) ->
                res.add(new ArgumentValue<>(arguments.get(key), value.produce())));
        return res;
    }

    public static <K> Builder<K> newBuilder() {
        return new Builder<>();
    }

    public static class Builder<K> {
        private Parser parser;
        private Function<String, K> keyGenerator;
        private Map<K, Argument<K, Object>> arguments;

        private Builder() {
        }

        public Builder<K> parser(Parser parser) {
            this.parser = parser;
            return this;
        }

        public Builder<K> keyGenerator(Function<String, K> keyGenerator) {
            this.keyGenerator = keyGenerator;
            return this;
        }

        public Builder<K> arguments(List<? extends Argument<K, ?>> arguments) {
            Map<K, Argument<K, Object>> lArguments = this.arguments;
            if (lArguments == null) {
                lArguments = new HashMap<>();
                this.arguments = lArguments;
            }
            for (Argument<K, ?> argument : arguments) {
                @SuppressWarnings("unchecked")
                Argument<K, Object> a = (Argument<K, Object>) argument;
                lArguments.put(argument.getName(), a);
            }
            return this;
        }

        public Builder<K> argument(Argument<K, ?> argument) {
            Map<K, Argument<K, Object>> arguments = this.arguments;
            if (arguments == null) {
                arguments = new HashMap<>();
                this.arguments = arguments;
            }

            @SuppressWarnings("unchecked")
            Argument<K, Object> a = (Argument<K, Object>) argument;
            arguments.put(argument.getName(), a);
            return this;
        }

        public Analyzer<K> build() {
            return new Analyzer<>(parser,
                    keyGenerator,
                    arguments == null ? Collections.emptyMap() : arguments);
        }

    }

}
