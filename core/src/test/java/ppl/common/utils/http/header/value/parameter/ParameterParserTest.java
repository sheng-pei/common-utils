package ppl.common.utils.http.header.value.parameter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ppl.common.utils.Arrays;
import ppl.common.utils.pair.Pair;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class ParameterParserTest {

    private final ParameterParser parser = ParameterParser.DEFAULT;
    private final ParameterParser ignoreBadWhitespaceParser = ParameterParser.newBuilder()
            .ignoreNameBadWhitespace(true).ignoreValueBadWhitespace(true).build();

    @Test
    void testParseEmpty() {
        Assertions.assertEquals(0, parser.parse("").count());
    }

    @ParameterizedTest
    @MethodSource("parameterProvider")
    void testParseReader(String string, List<Pair<String, String>> expects) {
        List<Pair<String, String>> actual = parser.parse(string)
                .map(f -> Pair.create(f.getKey(), f.getValue()))
                .collect(Collectors.toList());
        Assertions.assertEquals(expects.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            Assertions.assertEquals(expects.get(i).getFirst(), actual.get(i).getFirst());
            Assertions.assertEquals(expects.get(i).getSecond(), actual.get(i).getSecond());
        }
    }

    private static Stream<Arguments> parameterProvider() {
        return Stream.of(
                Arguments.of("z", Collections.singletonList(Pair.create("z", null))),
                Arguments.of("a=b", Collections.singletonList(Pair.create("a", "b"))),
                Arguments.of("a=b;", Collections.singletonList(Pair.create("a", "b"))),
                Arguments.of("a=b;z", Arrays.asList(Pair.create("a", "b"), Pair.create("z", null))),
                Arguments.of("z;u=v", Arrays.asList(Pair.create("z", null), Pair.create("u", "v"))),
                Arguments.of("a=b;u=v", Arrays.asList(Pair.create("a", "b"), Pair.create("u", "v"))),
                Arguments.of("a=b;;u=v", Arrays.asList(Pair.create("a", "b"), Pair.create("u", "v"))),
                Arguments.of("a=\"b\\\\\\\"\"", Collections.singletonList(Pair.create("a", "b\\\"")))
        );
    }

    @ParameterizedTest
    @MethodSource("optionalParameterProvider")
    void testOptionalWhitespace(String string, List<Pair<String, String>> expects) {
        List<Pair<String, String>> actual = parser.parse(string)
                .map(f -> Pair.create(f.getKey(), f.getValue()))
                .collect(Collectors.toList());
        Assertions.assertEquals(expects.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            Assertions.assertEquals(expects.get(i).getFirst(), actual.get(i).getFirst());
            Assertions.assertEquals(expects.get(i).getSecond(), actual.get(i).getSecond());
        }
    }

    private static Stream<Arguments> optionalParameterProvider() {
        return Stream.of(
                Arguments.of(" \t ", Collections.emptyList()),
                Arguments.of(" \tz \t", Collections.singletonList(Pair.create("z", null))),
                Arguments.of(" \ta=b \t", Collections.singletonList(Pair.create("a", "b"))),
                Arguments.of(" \ta=b \t; \t", Collections.singletonList(Pair.create("a", "b"))),
                Arguments.of(" \ta=b \t; \tz \t", Arrays.asList(Pair.create("a", "b"), Pair.create("z", null))),
                Arguments.of(" \tz \t; \tu=v \t", Arrays.asList(Pair.create("z", null), Pair.create("u", "v"))),
                Arguments.of(" \ta=b \t; \tu=v \t", Arrays.asList(Pair.create("a", "b"), Pair.create("u", "v"))),
                Arguments.of(" \ta=b \t; \t; \tu=v \t", Arrays.asList(Pair.create("a", "b"), Pair.create("u", "v")))
        );
    }

    @ParameterizedTest
    @MethodSource("badParameterProvider")
    void testBadWhitespace(String string) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse(string).count());
    }

    private static Stream<Arguments> badParameterProvider() {
        return Stream.of(
                Arguments.of("a \t=b"),
                Arguments.of("a= \tb"),
                Arguments.of("a=b;u \t=v"),
                Arguments.of("a=b;u= \tv")
        );
    }

    @ParameterizedTest
    @MethodSource("ignoredBadParameterProvider")
    void testIgnoredBadWhitespace(String string, List<Pair<String, String>> expects) {
        List<Pair<String, String>> actual = ignoreBadWhitespaceParser.parse(string)
                .map(f -> Pair.create(f.getKey(), f.getValue()))
                .collect(Collectors.toList());
        Assertions.assertEquals(expects.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            Assertions.assertEquals(expects.get(i).getFirst(), actual.get(i).getFirst());
            Assertions.assertEquals(expects.get(i).getSecond(), actual.get(i).getSecond());
        }
    }

    private static Stream<Arguments> ignoredBadParameterProvider() {
        return Stream.of(
                Arguments.of("a \t=b", Collections.singletonList(Pair.create("a", "b"))),
                Arguments.of("a= \tb;", Collections.singletonList(Pair.create("a", "b"))),
                Arguments.of("a=b;u \t=v", Arrays.asList(Pair.create("a", "b"), Pair.create("u", "v"))),
                Arguments.of("a=b;u= \tv", Arrays.asList(Pair.create("a", "b"), Pair.create("u", "v"))),
                Arguments.of("a \t= \tb;u \t= \tv", Arrays.asList(Pair.create("a", "b"), Pair.create("u", "v")))
        );
    }

    @ParameterizedTest
    @MethodSource("errorValueParameterProvider")
    void testErrorValueParseReader(String string) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse(string).count());
    }

    private static Stream<Arguments> errorValueParameterProvider() {
        return Stream.of(
                Arguments.of("a="),
                Arguments.of("a=v\r"),
                Arguments.of("a=v\""),
                Arguments.of("a=\"v\\\"")
        );
    }

    @ParameterizedTest
    @MethodSource("errorNameParameterProvider")
    void testErrorNameParseReader(String string) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse(string).count());
    }

    private static Stream<Arguments> errorNameParameterProvider() {
        return Stream.of(
                Arguments.of("=v"),
                Arguments.of("a\r=v"),
                Arguments.of("\"a\"=v")
        );
    }

//    @ParameterizedTest
//    @MethodSource("nameValueProvider")
//    void testParseNameValue(String name, String value, Pair<String, String> expect) {
//        List<Pair<String, String>> actual = parser.parse(name, value)
//                .map(f -> Pair.create(f.getKey(), f.getValue()))
//                .collect(Collectors.toList());
//        Assertions.assertEquals(1, actual.size());
//        Assertions.assertEquals(expect.getFirst(), actual.get(0).getFirst());
//        Assertions.assertEquals(expect.getSecond(), actual.get(0).getSecond());
//    }
//
//    private static Stream<Arguments> nameValueProvider() {
//        return Stream.of(
//                Arguments.of("a", "b", Pair.create("a", "b")),
//                Arguments.of("a", "b\\\"", Pair.create("a", "b\\\"")),
//                Arguments.of(" \ta \t", " \tb \t", Pair.create("a", "b"))
//        );
//    }
//
//    @ParameterizedTest
//    @MethodSource("notTokenNameProvider")
//    void testNotTokenName(String name, String value) {
//        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse(name, value).count());
//    }
//
//    private static Stream<Arguments> notTokenNameProvider() {
//        return Stream.of(
//                Arguments.of("", "v"),
//                Arguments.of("\ra", "v"),
//                Arguments.of("a=", "v"),
//                Arguments.of("\"a\"", "v")
//        );
//    }
//
//    @ParameterizedTest
//    @MethodSource("notTokenAndQuotedStringValueProvider")
//    void testNotTokenAndQuotedStringValueProvider(String name, String value) {
//        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse(name, value).count());
//    }
//
//    private static Stream<Arguments> notTokenAndQuotedStringValueProvider() {
//        return Stream.of(
//                Arguments.of("a", "v\r"),
//                Arguments.of("a", "\"v")
//        );
//    }
//
//    @ParameterizedTest
//    @MethodSource("badParameterProvider")
//    void testBadWhitespace(String string) {
//        Assertions.assertThrows(IllegalArgumentException.class, () -> parser.parse(string).count());
//    }
//
//    private static Stream<Arguments> badParameterProvider() {
//        return Stream.of(
//                Arguments.of("a \t=b"),
//                Arguments.of("a= \tb"),
//                Arguments.of("a=b;u \t=v"),
//                Arguments.of("a=b;u= \tv")
//        );
//    }
//
//    @ParameterizedTest
//    @MethodSource("ignoredBadParameterProvider")
//    void testIgnoredBadWhitespace(String string, List<Pair<String, String>> expects) {
//        List<Pair<String, String>> actual = ignoreBadWhitespaceParser.parse(string)
//                .map(f -> Pair.create(f.getKey(), f.getValue()))
//                .collect(Collectors.toList());
//        Assertions.assertEquals(expects.size(), actual.size());
//        for (int i = 0; i < actual.size(); i++) {
//            Assertions.assertEquals(expects.get(i).getFirst(), actual.get(i).getFirst());
//            Assertions.assertEquals(expects.get(i).getSecond(), actual.get(i).getSecond());
//        }
//    }
//
//    private static Stream<Arguments> ignoredBadParameterProvider() {
//        return Stream.of(
//                Arguments.of("a \t=b", Collections.singletonList(Pair.create("a", "b"))),
//                Arguments.of("a= \tb;", Collections.singletonList(Pair.create("a", "b"))),
//                Arguments.of("a=b;u \t=v", Arrays.asList(Pair.create("a", "b"), Pair.create("u", "v"))),
//                Arguments.of("a=b;u= \tv", Arrays.asList(Pair.create("a", "b"), Pair.create("u", "v"))),
//                Arguments.of("a \t= \tb;u \t= \tv", Arrays.asList(Pair.create("a", "b"), Pair.create("u", "v")))
//        );
//    }

    @Test
    void testParseNameValue() {
    }

}