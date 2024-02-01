package ppl.common.utils.string;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public final class FStrings {
    private FStrings() {}
    public static List<String> parseArray(String listString) {
        return parseArray(listString, Function.identity());
    }

    public static <T> List<T> parseArray(String listString, Function<String, T> converter) {
        return parseArray(listString, ",", converter);
    }

    public static <T> List<T> parseArray(String listString, String regex, Function<String, T> converter) {
        return Optional.ofNullable(listString)
                .map(s -> Strings.streaming(s, regex))
                .orElse(Stream.of())
                .map(String::trim)
                .filter(Strings::isNotEmpty)
                .map(converter)
                .collect(toList());
    }
}
