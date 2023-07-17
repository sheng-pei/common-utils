package ppl.common.utils.filesystem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ppl.common.utils.filesystem.path.BasePath;
import ppl.common.utils.helper.EqualsTester;

import java.util.stream.Stream;

class BasePathTest {

    @ParameterizedTest
    @MethodSource("firstMoreProvider")
    void testGet(BasePath expected, String first, String... more) {
        Assertions.assertEquals(expected, BasePath.get(first, more));
    }

    private static Stream<Arguments> firstMoreProvider() {
        return Stream.of(
                Arguments.of(BasePath.get(""), "", new String[] {null, ""}),
                Arguments.of(BasePath.get("a"), "a", new String[] {null, ""}),
                Arguments.of(BasePath.get("a/b"), null, new String[] {"a", "b"}),
                Arguments.of(BasePath.get("/a"), "/a", new String[] {})
        );
    }

    @ParameterizedTest
    @MethodSource("pathProvider")
    void testGet(BasePath expected, String path) {
        Assertions.assertEquals(expected, BasePath.get(path));
    }

    private static Stream<Arguments> pathProvider() {
        return Stream.of(
                Arguments.of(BasePath.get("a/b/c"), "a//b////c/"),
                Arguments.of(BasePath.get("/a/b/c"), "///a/b/c")
        );
    }

    @Test
    void testIsAbsolute() {
        Assertions.assertFalse(BasePath.get("a/b").isAbsolute());
        Assertions.assertTrue(BasePath.get("/").isAbsolute());
    }

    @ParameterizedTest
    @MethodSource("fileNameProvider")
    void testGetFileName(BasePath expected, BasePath path) {
        Assertions.assertEquals(expected, path.getFileName());
    }

    private static Stream<Arguments> fileNameProvider() {
        return Stream.of(
                Arguments.of(BasePath.get(".."), BasePath.get("a/..")),
                Arguments.of(BasePath.get("."), BasePath.get("a/.")),
                Arguments.of(BasePath.get("b"), BasePath.get("/a/b")),
                Arguments.of(BasePath.get("b"), BasePath.get("a/b")),
                Arguments.of(null, BasePath.get("")),
                Arguments.of(null, BasePath.get("/"))
        );
    }

    @ParameterizedTest
    @MethodSource("parentProvider")
    void testGetParent(BasePath expected, BasePath path) {
        Assertions.assertEquals(expected, path.getParent());
    }

    private static Stream<Arguments> parentProvider() {
        return Stream.of(
                Arguments.of(BasePath.get("a/.."), BasePath.get("a/../b")),
                Arguments.of(BasePath.get("a/."), BasePath.get("a/./b")),
                Arguments.of(BasePath.get("/"), BasePath.get("/a")),
                Arguments.of(null, BasePath.get("a")),
                Arguments.of(null, BasePath.get("")),
                Arguments.of(null, BasePath.get("/")),
                Arguments.of(BasePath.get("/a"), BasePath.get("/a/b"))
        );
    }

    @ParameterizedTest
    @MethodSource("nameCountProvider")
    void testGetNameCount(int expected, BasePath path) {
        Assertions.assertEquals(expected, path.getNameCount());
    }

    private static Stream<Arguments> nameCountProvider() {
        return Stream.of(
                Arguments.of(3, BasePath.get("a/../b")),
                Arguments.of(3, BasePath.get("a/./b")),
                Arguments.of(1, BasePath.get("/a")),
                Arguments.of(1, BasePath.get("a")),
                Arguments.of(0, BasePath.get("")),
                Arguments.of(0, BasePath.get("/"))
        );
    }

    @ParameterizedTest
    @MethodSource("errorIndexProvider")
    void testGetName(BasePath path, int index) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> path.getName(index));
    }

    private static Stream<Arguments> errorIndexProvider() {
        return Stream.of(
                Arguments.of(BasePath.get("a/../b"), -1),
                Arguments.of(BasePath.get("a/../b"), 3),
                Arguments.of(BasePath.get("/a"), 1),
                Arguments.of(BasePath.get("a"), 1),
                Arguments.of(BasePath.get(""), 0),
                Arguments.of(BasePath.get("/"), 0)
        );
    }

    @ParameterizedTest
    @MethodSource("nameProvider")
    void testGetName(BasePath expected, BasePath path, int index) {
        Assertions.assertEquals(expected, path.getName(index));
    }

    private static Stream<Arguments> nameProvider() {
        return Stream.of(
                Arguments.of(BasePath.get(".."), BasePath.get("a/../b"), 1),
                Arguments.of(BasePath.get("a"), BasePath.get("/a"), 0),
                Arguments.of(BasePath.get("a"), BasePath.get("a"), 0)
        );
    }

    @ParameterizedTest
    @MethodSource("errorSubpathProvider")
    void testSubpath(BasePath path, int start, int end) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> path.subpath(start, end));
    }

    private static Stream<Arguments> errorSubpathProvider() {
        return Stream.of(
                Arguments.of(BasePath.get("a/../b"), -1, 1),
                Arguments.of(BasePath.get("a/../b"), 0, -1),
                Arguments.of(BasePath.get("a/../b"), 2, 1),
                Arguments.of(BasePath.get("a/../b"), 2, 2)
        );
    }

    @ParameterizedTest
    @MethodSource("subpathProvider")
    void testSubpath(BasePath expected, BasePath path, int start, int end) {
        Assertions.assertEquals(expected, path.subpath(start, end));
    }

    private static Stream<Arguments> subpathProvider() {
        return Stream.of(
                Arguments.of(BasePath.get("../b"), BasePath.get("a/../b"), 1, 3)
        );
    }

    @ParameterizedTest
    @MethodSource("startsWithProvider")
    void testStartsWith(boolean expected, BasePath path, String other) {
        Assertions.assertEquals(expected, path.startsWith(other));
        Assertions.assertEquals(expected, path.startsWith(BasePath.get(other)));
    }

    private static Stream<Arguments> startsWithProvider() {
        return Stream.of(
                Arguments.of(true, BasePath.get("/"), "/"),
                Arguments.of(true, BasePath.get("/a/b"), "/a"),
                Arguments.of(true, BasePath.get("/a/b"), "/a/b"),
                Arguments.of(true, BasePath.get(""), ""),
                Arguments.of(true, BasePath.get("a/b"), "a"),
                Arguments.of(true, BasePath.get("a/b"), "a/b"),
                Arguments.of(false, BasePath.get("a/b"), "/"),
                Arguments.of(false, BasePath.get("/"), "a/b")
        );
    }

    @ParameterizedTest
    @MethodSource("endsWithProvider")
    void testEndsWith(boolean expected, BasePath path, String other) {
        Assertions.assertEquals(expected, path.endsWith(other));
        Assertions.assertEquals(expected, path.endsWith(BasePath.get(other)));
    }

    private static Stream<Arguments> endsWithProvider() {
        return Stream.of(
                Arguments.of(true, BasePath.get("/a/b"), "/a/b"),
                Arguments.of(false, BasePath.get("/a/b"), "/a"),
                Arguments.of(false, BasePath.get("a/b"), "/b"),
                Arguments.of(true, BasePath.get(""), ""),
                Arguments.of(true, BasePath.get("/a/b"), "b"),
                Arguments.of(true, BasePath.get("a/b"), "a/b"),
                Arguments.of(true, BasePath.get("a/b"), "b")
        );
    }

    @ParameterizedTest
    @MethodSource("resolveProvider")
    void testResolve(BasePath expected, BasePath path, String other) {
        Assertions.assertEquals(expected, path.resolve(other));
        Assertions.assertEquals(expected, path.resolve(BasePath.get(other)));
    }

    private static Stream<Arguments> resolveProvider() {
        return Stream.of(
                Arguments.of(BasePath.get("/"), BasePath.get("/a/b"), "/"),
                Arguments.of(BasePath.get("/"), BasePath.get("a/b"), "/"),
                Arguments.of(BasePath.get("a/../b/.."), BasePath.get("a/../b"), ".."),
                Arguments.of(BasePath.get("a/../b/c"), BasePath.get("a/../b"), "c"),
                Arguments.of(BasePath.get("/a/b/c"), BasePath.get("/a/b"), "c"),
                Arguments.of(BasePath.get("/a/b"), BasePath.get("/a/b"), ""),
                Arguments.of(BasePath.get("a/b"), BasePath.get(""), "a/b")
        );
    }

    @ParameterizedTest
    @MethodSource("resolveSiblingProvider")
    void testResolveSibling(BasePath expected, BasePath path, String other) {
        Assertions.assertEquals(expected, path.resolveSibling(other));
        Assertions.assertEquals(expected, path.resolveSibling(BasePath.get(other)));
    }

    private static Stream<Arguments> resolveSiblingProvider() {
        return Stream.of(
                Arguments.of(BasePath.get("/"), BasePath.get("/a/b"), "/"),
                Arguments.of(BasePath.get("a/b"), BasePath.get(""), "a/b"),
                Arguments.of(BasePath.get("a/b"), BasePath.get("a"), "a/b"),
                Arguments.of(BasePath.get("a/b"), BasePath.get("/"), "a/b"),
                Arguments.of(BasePath.get("a/../.."), BasePath.get("a/../b"), ".."),
                Arguments.of(BasePath.get("a/../c"), BasePath.get("a/../b"), "c"),
                Arguments.of(BasePath.get("/a/c"), BasePath.get("/a/b"), "c"),
                Arguments.of(BasePath.get("/a/"), BasePath.get("/a/b"), "")
        );
    }

    @ParameterizedTest
    @MethodSource("relativizeErrorProvider")
    void testRelativize(BasePath path, BasePath other) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> path.relativize(other));
    }

    private static Stream<Arguments> relativizeErrorProvider() {
        return Stream.of(
                Arguments.of(BasePath.get("/a/b"), BasePath.get("a")),
                Arguments.of(BasePath.get("a"), "/a/b")
        );
    }

    @ParameterizedTest
    @MethodSource("relativizeProvider")
    void testRelativize(BasePath expected, BasePath path, BasePath other) {
        Assertions.assertEquals(expected, path.relativize(other));
    }

    private static Stream<Arguments> relativizeProvider() {
        return Stream.of(
                Arguments.of(BasePath.get("../.."), BasePath.get("/a/b"), "/"),
                Arguments.of(BasePath.get("a/b"), BasePath.get(""), "a/b"),
                Arguments.of(BasePath.get("../../../a/b"), BasePath.get("../a/b"), "a/b"),
                Arguments.of(BasePath.get("b"), BasePath.get("a"), "a/b")
        );
    }

    @ParameterizedTest
    @MethodSource("normalizeProvider")
    void testNormalize(BasePath expected, BasePath path) {
        Assertions.assertEquals(expected, path.normalize());
    }

    private static Stream<Arguments> normalizeProvider() {
        return Stream.of(
                Arguments.of(BasePath.get("../a/c"), BasePath.get("../a/./b/../c")),
                Arguments.of(BasePath.get("/"), BasePath.get("/..")),
                Arguments.of(BasePath.get(".."), BasePath.get("a/../..")),
                Arguments.of(BasePath.get(""), BasePath.get("a/..")),
                Arguments.of(BasePath.get(""), BasePath.get(""))
        );
    }

    @Test
    void testIterator() {
        BasePath[] paths = new BasePath[] {BasePath.get("a"), BasePath.get(".."), BasePath.get("c")};

        BasePath p = BasePath.get("a/../c");
        int i = 0;
        for (Path path : p) {
            Assertions.assertEquals(paths[i++], path);
        }

        BasePath p1 = BasePath.get("/a/../c");
        int i1 = 0;
        for (Path path : p1) {
            Assertions.assertEquals(paths[i1++], path);
        }
    }

    @Test
    void compareTo() {
        Assertions.assertEquals(0, BasePath.get("a//").compareTo(BasePath.get("a/")));
        Assertions.assertTrue(BasePath.get("//b").compareTo(BasePath.get("//a")) > 0);
        Assertions.assertTrue(BasePath.get("/").compareTo(BasePath.get("a")) < 0);
    }

    @Test
    void testEquals() {
        BasePath path1 = BasePath.get("a/b/");
        BasePath path2 = BasePath.get("a/b");
        BasePath path3 = BasePath.get("a//b");

        EqualsTester tester = new EqualsTester();
        tester.addGroup(path1, path2, path3);
        tester.addGroup(BasePath.get("a"));
        tester.test();
    }

}