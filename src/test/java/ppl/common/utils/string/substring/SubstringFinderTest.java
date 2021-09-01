package ppl.common.utils.string.substring;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ppl.common.utils.string.Substring;
import ppl.common.utils.string.SubstringFinder;

import java.util.function.Function;
import java.util.stream.Stream;

class SubstringFinderTest {

    private static Function<String, SubstringFinder> SUNDAY_FINDER = SundaySubstringFinder::new;
    private static Function<String, SubstringFinder> KMP_FINDER = KMPSubstringFinder::new;
    private static Function<String, SubstringFinder> ESCAPABLE_FINDER = EscapableSubstringFinder::new;

    @ParameterizedTest
    @MethodSource("illegalArgumentsProvider")
    public void testConstructorThrowsIllegalArgumentException(Function<String, SubstringFinder> creator, String pattern) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> creator.apply(pattern));
    }

    private static Stream<Arguments> illegalArgumentsProvider() {
        return Stream.of(
                Arguments.of(SUNDAY_FINDER, ""),
                Arguments.of(SUNDAY_FINDER, null),
                Arguments.of(KMP_FINDER, ""),
                Arguments.of(KMP_FINDER, null),
                Arguments.of(ESCAPABLE_FINDER, ""),
                Arguments.of(ESCAPABLE_FINDER, null),
                Arguments.of(ESCAPABLE_FINDER, "\\"),
                Arguments.of(ESCAPABLE_FINDER, "aba")
        );
    }

    @ParameterizedTest
    @MethodSource({"noFindProvider"})
    public void testNoFind(Function<String, SubstringFinder> creator, String pattern, String input) {
        SubstringFinder finder = creator.apply(pattern);
        Assertions.assertNull(finder.find(input));
    }

    private static Stream<Arguments> noFindProvider() {
        return Stream.of(
                Arguments.of(SUNDAY_FINDER, "jfieak", "jf"),
                Arguments.of(KMP_FINDER, "jfieak", "jf"),
                Arguments.of(ESCAPABLE_FINDER, "jfieak", "jf"),
                Arguments.of(SUNDAY_FINDER, "nfmei", "ahifeklaiwej"),
                Arguments.of(KMP_FINDER, "nfmei", "ahifeklaiwej"),
                Arguments.of(ESCAPABLE_FINDER, "nfmei", "ahifeklaiwej")
        );
    }

    @ParameterizedTest
    @MethodSource({"findFromWholeProvider", "findFromPartOfInputProvider"})
    public void testFindFromWholeInput(Function<String, SubstringFinder> creator, String pattern, String input, Substring expect) {
        SubstringFinder finder = creator.apply(pattern);
        Assertions.assertEquals(expect, finder.find(input));
    }

    private static Stream<Arguments> findFromWholeProvider() {
        return Stream.of(
                Arguments.of(SUNDAY_FINDER, "abccda", "abccda", new ConsistentSubstring("abccda")),
                Arguments.of(KMP_FINDER, "GCGCGT", "GCGCGT", new ConsistentSubstring("GCGCGT")),
                Arguments.of(ESCAPABLE_FINDER, "jfieak", "jfieak", new ConsistentSubstring("jfieak")),
                Arguments.of(SUNDAY_FINDER, "abccda", "abccdanma", new ConsistentSubstring("abccdanma", 0, 6)),
                Arguments.of(KMP_FINDER, "GCGCGT", "GCGCGTnma", new ConsistentSubstring("GCGCGTnma", 0, 6)),
                Arguments.of(ESCAPABLE_FINDER, "jfieak", "jfieakjnma", new ConsistentSubstring("jfieakjnma", 0, 6)),
                Arguments.of(SUNDAY_FINDER, "abccda", "afewfabccda", new ConsistentSubstring("afewfabccda", 5)),
                Arguments.of(KMP_FINDER, "GCGCGT", "afewfGCGCGT", new ConsistentSubstring("afewfGCGCGT", 5)),
                Arguments.of(ESCAPABLE_FINDER, "jfieak", "afewfjfieak", new ConsistentSubstring("afewfjfieak", 5))
        );
    }

    @ParameterizedTest
    @MethodSource({"findFromPartOfInputProvider"})
    public void testFindFromPartOfInput(Function<String, SubstringFinder> creator, String pattern, String input, Substring expect) {
        SubstringFinder finder = creator.apply(pattern);
        Assertions.assertEquals(expect, finder.find(input, 1, input.length() - 1));
    }

    private static Stream<Arguments> findFromPartOfInputProvider() {
        return Stream.of(
                Arguments.of(SUNDAY_FINDER, "abccda", "ababccdacda", new ConsistentSubstring("ababccdacda", 2, 8)),
                Arguments.of(KMP_FINDER, "GCGCGT", "GCGCGCGTCGT", new ConsistentSubstring("GCGCGCGTCGT", 2, 8)),
                Arguments.of(ESCAPABLE_FINDER, "jfieak", "afewfjfieaknma", new ConsistentSubstring("afewfjfieaknma", 5, 11))
        );
    }

    @Test
    public void testFindSubstringWithEscapeCharacter() {
        EscapableSubstringFinder finder = new EscapableSubstringFinder("{}");

        String input = "aa\\\\{}bb";
        Substring actual = finder.find(input);
        Assertions.assertEquals(new EscapableSubString(input, 2, 4, 6), actual);
    }

}