package ppl.common.utils.filesystem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ppl.common.utils.filesystem.path.Path;
import ppl.common.utils.filesystem.path.Paths;
import ppl.common.utils.helper.EqualsTester;

import java.util.stream.Stream;

class PathTest {

    @ParameterizedTest
    @MethodSource("firstMoreProvider")
    void testGet(Path expected, String first, String... more) {
        Assertions.assertEquals(expected, Paths.get(first, more));
    }

    private static Stream<Arguments> firstMoreProvider() {
        return Stream.of(
                Arguments.of(Paths.get(""), "", new String[] {null, ""}),
                Arguments.of(Paths.get("a"), "a", new String[] {null, ""}),
                Arguments.of(Paths.get("a/b"), null, new String[] {"a", "b"}),
                Arguments.of(Paths.get("/a"), "/a", new String[] {})
        );
    }

    @ParameterizedTest
    @MethodSource("pathProvider")
    void testGet(Path expected, String path) {
        Assertions.assertEquals(expected, Paths.get(path));
    }

    private static Stream<Arguments> pathProvider() {
        return Stream.of(
                Arguments.of(Paths.get("a/b/c"), "a//b////c/"),
                Arguments.of(Paths.get("/a/b/c"), "///a/b/c")
        );
    }

    @Test
    void testIsAbsolute() {
        Assertions.assertFalse(Paths.get("a/b").isAbsolute());
        Assertions.assertTrue(Paths.get("/").isAbsolute());
    }

    @ParameterizedTest
    @MethodSource("fileNameProvider")
    void testGetFileName(Path expected, Path path) {
        Assertions.assertEquals(expected, path.getFileName());
    }

    private static Stream<Arguments> fileNameProvider() {
        return Stream.of(
                Arguments.of(Paths.get(".."), Paths.get("a/..")),
                Arguments.of(Paths.get("."), Paths.get("a/.")),
                Arguments.of(Paths.get("b"), Paths.get("/a/b")),
                Arguments.of(Paths.get("b"), Paths.get("a/b")),
                Arguments.of(null, Paths.get("")),
                Arguments.of(null, Paths.get("/"))
        );
    }

    @ParameterizedTest
    @MethodSource("parentProvider")
    void testGetParent(Path expected, Path path) {
        Assertions.assertEquals(expected, path.getParent());
    }

    private static Stream<Arguments> parentProvider() {
        return Stream.of(
                Arguments.of(Paths.get("a/.."), Paths.get("a/../b")),
                Arguments.of(Paths.get("a/."), Paths.get("a/./b")),
                Arguments.of(Paths.get("/"), Paths.get("/a")),
                Arguments.of(null, Paths.get("a")),
                Arguments.of(null, Paths.get("")),
                Arguments.of(null, Paths.get("/")),
                Arguments.of(Paths.get("/a"), Paths.get("/a/b"))
        );
    }

    @ParameterizedTest
    @MethodSource("nameCountProvider")
    void testGetNameCount(int expected, Path path) {
        Assertions.assertEquals(expected, path.getNameCount());
    }

    private static Stream<Arguments> nameCountProvider() {
        return Stream.of(
                Arguments.of(3, Paths.get("a/../b")),
                Arguments.of(3, Paths.get("a/./b")),
                Arguments.of(1, Paths.get("/a")),
                Arguments.of(1, Paths.get("a")),
                Arguments.of(0, Paths.get("")),
                Arguments.of(0, Paths.get("/"))
        );
    }

    @ParameterizedTest
    @MethodSource("errorIndexProvider")
    void testGetName(Path path, int index) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> path.getName(index));
    }

    private static Stream<Arguments> errorIndexProvider() {
        return Stream.of(
                Arguments.of(Paths.get("a/../b"), -1),
                Arguments.of(Paths.get("a/../b"), 3),
                Arguments.of(Paths.get("/a"), 1),
                Arguments.of(Paths.get("a"), 1),
                Arguments.of(Paths.get(""), 0),
                Arguments.of(Paths.get("/"), 0)
        );
    }

    @ParameterizedTest
    @MethodSource("nameProvider")
    void testGetName(Path expected, Path path, int index) {
        Assertions.assertEquals(expected, path.getName(index));
    }

    private static Stream<Arguments> nameProvider() {
        return Stream.of(
                Arguments.of(Paths.get(".."), Paths.get("a/../b"), 1),
                Arguments.of(Paths.get("a"), Paths.get("/a"), 0),
                Arguments.of(Paths.get("a"), Paths.get("a"), 0)
        );
    }

    @ParameterizedTest
    @MethodSource("errorSubpathProvider")
    void testSubpath(Path path, int start, int end) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> path.subpath(start, end));
    }

    private static Stream<Arguments> errorSubpathProvider() {
        return Stream.of(
                Arguments.of(Paths.get("a/../b"), -1, 1),
                Arguments.of(Paths.get("a/../b"), 0, -1),
                Arguments.of(Paths.get("a/../b"), 2, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("subpathProvider")
    void testSubpath(Path expected, Path path, int start, int end) {
        Assertions.assertEquals(expected, path.subpath(start, end));
    }

    private static Stream<Arguments> subpathProvider() {
        return Stream.of(
                Arguments.of(Paths.get("../b"), Paths.get("a/../b"), 1, 3)
        );
    }

    @ParameterizedTest
    @MethodSource("startsWithProvider")
    void testStartsWith(boolean expected, Path path, String other) {
        Assertions.assertEquals(expected, path.startsWith(other));
        Assertions.assertEquals(expected, path.startsWith(Paths.get(other)));
    }

    private static Stream<Arguments> startsWithProvider() {
        return Stream.of(
                Arguments.of(true, Paths.get("/"), "/"),
                Arguments.of(true, Paths.get("/a/b"), "/a"),
                Arguments.of(true, Paths.get("/a/b"), "/a/b"),
                Arguments.of(true, Paths.get(""), ""),
                Arguments.of(true, Paths.get("a/b"), "a"),
                Arguments.of(true, Paths.get("a/b"), "a/b"),
                Arguments.of(false, Paths.get("a/b"), "/"),
                Arguments.of(false, Paths.get("/"), "a/b")
        );
    }

    @ParameterizedTest
    @MethodSource("endsWithProvider")
    void testEndsWith(boolean expected, Path path, String other) {
        Assertions.assertEquals(expected, path.endsWith(other));
        Assertions.assertEquals(expected, path.endsWith(Paths.get(other)));
    }

    private static Stream<Arguments> endsWithProvider() {
        return Stream.of(
                Arguments.of(true, Paths.get("/a/b"), "/a/b"),
                Arguments.of(false, Paths.get("/a/b"), "/a"),
                Arguments.of(false, Paths.get("a/b"), "/b"),
                Arguments.of(true, Paths.get(""), ""),
                Arguments.of(true, Paths.get("/a/b"), "b"),
                Arguments.of(true, Paths.get("a/b"), "a/b"),
                Arguments.of(true, Paths.get("a/b"), "b")
        );
    }

    @ParameterizedTest
    @MethodSource("resolveProvider")
    void testResolve(Path expected, Path path, String other) {
        Assertions.assertEquals(expected, path.resolve(other));
        Assertions.assertEquals(expected, path.resolve(Paths.get(other)));
    }

    private static Stream<Arguments> resolveProvider() {
        return Stream.of(
                Arguments.of(Paths.get("/"), Paths.get("/a/b"), "/"),
                Arguments.of(Paths.get("/"), Paths.get("a/b"), "/"),
                Arguments.of(Paths.get("a/../b/.."), Paths.get("a/../b"), ".."),
                Arguments.of(Paths.get("a/../b/c"), Paths.get("a/../b"), "c"),
                Arguments.of(Paths.get("/a/b/c"), Paths.get("/a/b"), "c"),
                Arguments.of(Paths.get("/a/b"), Paths.get("/a/b"), ""),
                Arguments.of(Paths.get("a/b"), Paths.get(""), "a/b")
        );
    }

    @ParameterizedTest
    @MethodSource("resolveSiblingProvider")
    void testResolveSibling(Path expected, Path path, String other) {
        Assertions.assertEquals(expected, path.resolveSibling(other));
        Assertions.assertEquals(expected, path.resolveSibling(Paths.get(other)));
    }

    private static Stream<Arguments> resolveSiblingProvider() {
        return Stream.of(
                Arguments.of(Paths.get("/"), Paths.get("/a/b"), "/"),
                Arguments.of(Paths.get("a/b"), Paths.get(""), "a/b"),
                Arguments.of(Paths.get("a/b"), Paths.get("a"), "a/b"),
                Arguments.of(Paths.get("a/b"), Paths.get("/"), "a/b"),
                Arguments.of(Paths.get("a/../.."), Paths.get("a/../b"), ".."),
                Arguments.of(Paths.get("a/../c"), Paths.get("a/../b"), "c"),
                Arguments.of(Paths.get("/a/c"), Paths.get("/a/b"), "c"),
                Arguments.of(Paths.get("/a/"), Paths.get("/a/b"), "")
        );
    }

    @ParameterizedTest
    @MethodSource("relativizeErrorProvider")
    void testRelativize(Path path, Path other) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> path.relativize(other));
    }

    private static Stream<Arguments> relativizeErrorProvider() {
        return Stream.of(
                Arguments.of(Paths.get("/a/b"), Paths.get("a")),
                Arguments.of(Paths.get("a"), Paths.get("/a/b"))
        );
    }

    @ParameterizedTest
    @MethodSource("relativizeProvider")
    void testRelativize(Path expected, Path path, Path other) {
        Assertions.assertEquals(expected, path.relativize(other));
    }

    private static Stream<Arguments> relativizeProvider() {
        return Stream.of(
                Arguments.of(Paths.get("../.."), Paths.get("/a/b"), Paths.get("/")),
                Arguments.of(Paths.get("a/b"), Paths.get(""), Paths.get("a/b")),
                Arguments.of(Paths.get("../../../a/b"), Paths.get("../a/b"), Paths.get("a/b")),
                Arguments.of(Paths.get("b"), Paths.get("a"), Paths.get("a/b"))
        );
    }

    @ParameterizedTest
    @MethodSource("normalizeProvider")
    void testNormalize(Path expected, Path path) {
        Assertions.assertEquals(expected, path.normalize());
    }

    private static Stream<Arguments> normalizeProvider() {
        return Stream.of(
                Arguments.of(Paths.get("../a/c"), Paths.get("../a/./b/../c")),
                Arguments.of(Paths.get("/"), Paths.get("/..")),
                Arguments.of(Paths.get(".."), Paths.get("a/../..")),
                Arguments.of(Paths.get(""), Paths.get("a/..")),
                Arguments.of(Paths.get(""), Paths.get(""))
        );
    }

    @Test
    void testIterator() {
        Path[] paths = new Path[] {Paths.get("a"), Paths.get(".."), Paths.get("c")};

        Path p = Paths.get("a/../c");
        int i = 0;
        for (Path path : p) {
            Assertions.assertEquals(paths[i++], path);
        }

        Path p1 = Paths.get("/a/../c");
        int i1 = 0;
        for (Path path : p1) {
            Assertions.assertEquals(paths[i1++], path);
        }
    }

    @Test
    void compareTo() {
        Assertions.assertEquals(0, Paths.get("a//").compareTo(Paths.get("a/")));
        Assertions.assertTrue(Paths.get("//b").compareTo(Paths.get("//a")) > 0);
        Assertions.assertTrue(Paths.get("/").compareTo(Paths.get("a")) < 0);
    }

    @Test
    void testEquals() {
        Path path1 = Paths.get("a/b/");
        Path path2 = Paths.get("a/b");
        Path path3 = Paths.get("a//b");

        EqualsTester tester = new EqualsTester();
        tester.addGroup(path1, path2, path3);
        tester.addGroup(Paths.get("a"));
        tester.test();
    }

}