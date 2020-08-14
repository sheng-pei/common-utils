package ppl.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SundaySubstringFinderTest {

    @Test
    public void testConstructorWithEmptyPattern() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new SundaySubstringFinder(""));
    }

    @Test
    public void testConstructorWithNullPattern() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new SundaySubstringFinder(null));
    }

    @Test
    public void testFind() {
        SundaySubstringFinder finder = new SundaySubstringFinder("");
    }

    @Test
    public void test() {

    }

}