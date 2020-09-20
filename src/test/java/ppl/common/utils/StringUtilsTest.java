package ppl.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringUtilsTest {

    private static final String[] EMPTY_ARRAY = new String[0];

    @Test
    public void testSplitNullByEmptyRegexSeparator() {
        String[] actual = StringUtils.split(null, "");
        Assertions.assertArrayEquals(EMPTY_ARRAY, actual);
    }

    @Test
    public void testSplitEmptyStringByEmptyRegexSeparator() {
        String[] actual = StringUtils.split("", "");
        Assertions.assertArrayEquals(EMPTY_ARRAY, actual);
    }

    @Test
    public void testSplitByEmptyRegexSeparator() {
        String[] actual = StringUtils.split("abndc", "");
        String[] expected = {"a", "b", "n", "d", "c"};
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testSplitNullByRegexSeparator() {
        String[] actual = StringUtils.split(null, ",*");
        Assertions.assertArrayEquals(EMPTY_ARRAY, actual);
    }

    @Test
    public void testSplitEmptyStringByRegexSeparator() {
        String[] actual = StringUtils.split("", ",*");
        Assertions.assertArrayEquals(EMPTY_ARRAY, actual);
    }

    @Test
    public void testSplitWholeMatchingByRegexSeparator() {
        String[] actual = StringUtils.split(",", ",*");
        String[] expected = {""};
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testSplitHeadPositiveWidthMatchingByRegexSeparator() {
        String[] actual = StringUtils.split(",a", ",*");
        String[] expected = {"", "a"};
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testSplitHeadZeroWidthMatchingByRegexSeparator() {
        String[] actual = StringUtils.split("a", ",*");
        String[] expected = {"a"};
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testSplitIgnoreTrailingEmptyStringByRegexSeparator() {
        String[] actual = StringUtils.split("a,", ",*");
        String[] expected = {"a"};
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testSplitByRegexSeparator() {
        String[] actual = StringUtils.split("a,bfd,,,,,d,,,,,", ",*");
        String[] expected = {"a", "b", "f", "d", "d"};
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testSplitByNullSeparator() {
        Assertions.assertThrows(NullPointerException.class, () -> StringUtils.split("", null));
    }

    @Test
    public void testUniqueOfNull() {
        String[] actual = StringUtils.removeDuplicate(null);
        Assertions.assertArrayEquals(EMPTY_ARRAY, actual);
    }

    @Test
    public void testUnique() {
        String[] input = {"a", "b", "a"};
        String[] expected = {"a", "b"};

        String[] actual = StringUtils.removeDuplicate(input);

        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testIsEmptyOfNull() {
        Assertions.assertTrue(StringUtils.isEmpty(null));
    }

    @Test
    public void testIsEmptyOfZeroLengthString() {
        Assertions.assertTrue(StringUtils.isEmpty(""));
    }

    @Test
    public void testIsEmptyOfNonZeroLengthString() {
        Assertions.assertFalse(StringUtils.isEmpty(" a,"));
    }

    @Test
    public void testIsNotEmptyOfNull() {
        Assertions.assertFalse(StringUtils.isNotEmpty(null));
    }

    @Test
    public void testIsNotEmptyOfZeroLengthString() {
        Assertions.assertFalse(StringUtils.isNotEmpty(""));
    }

    @Test
    public void testIsNotEmptyOfNonZeroLengthString() {
        Assertions.assertTrue(StringUtils.isNotEmpty(" a,"));
    }

    @Test
    public void testIsBlankOfNull() {
        Assertions.assertTrue(StringUtils.isBlank(null));
    }

    @Test
    public void testIsBlankOfZeroLengthString() {
        Assertions.assertTrue(StringUtils.isBlank(""));
    }

    @Test
    public void testIsBlankOfStringContainedOnlyWhitespace() {
        String whitespace = " \t\n\r";
        Assertions.assertTrue(StringUtils.isBlank(whitespace));
    }

    @Test
    public void testIsBlankOfStringContainedNonWhitespace() {
        String whitespace = " \t\n\rc\r\n\t ";
        Assertions.assertFalse(StringUtils.isBlank(whitespace));
    }

    @Test
    public void testEqualsNullNull() {
        Assertions.assertTrue(StringUtils.equals(null, null));
    }

    @Test
    public void testEqualsNullNonnull() {
        Assertions.assertFalse(StringUtils.equals(null, ""));
    }

    @Test
    public void testEqualsNonnullNull() {
        Assertions.assertFalse(StringUtils.equals("", null));
    }

    @Test
    public void testEquals() {
        Assertions.assertTrue(StringUtils.equals("aavgf", "aavgf"));
    }

    @Test
    public void testToSnakeAndLowerCaseMM() {
        String MM = "MM";
        String mm = StringUtils.toSnakeCase(MM);
        Assertions.assertEquals("mm", mm);
    }

    @Test
    public void testToSnakeAndLowerCasemM() {
        String mM = "mM";
        String m_m = StringUtils.toSnakeCase(mM);
        Assertions.assertEquals("m_m", m_m);
    }

    @Test
    public void testToSnakeAndLowerCasem_M() {
        String m_M = "m_M";
        String m_m = StringUtils.toSnakeCase(m_M);
        Assertions.assertEquals("m_m", m_m);
    }

    @Test
    public void testFormatJustSingleReference() {
        String singleReference = "{}";
        String replaced = StringUtils.format(singleReference, "ab");
        Assertions.assertEquals("ab", replaced);
    }

    @Test
    public void testFormatNotSpecialBackslash() {
        String noReferenceBackslash = "ab\\\\{";
        String replaced = StringUtils.format(noReferenceBackslash);
        Assertions.assertEquals("ab\\\\{", replaced);
    }

    @Test
    public void testFormatSpecialBackslash() {
        String specialBackslash = "abc\\\\\\{}";
        String replaced = StringUtils.format(specialBackslash, "ab");
        Assertions.assertEquals("abc\\\\{}", replaced);
    }

    @Test
    public void testFormatNotEnoughParameter() {
        String formatString = "{}";

        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.format(formatString));
    }

    @Test
    public void testFormat() {
        String formatString = "{}abc{}\\\\\\{}aaaa{}";
        String replaced = StringUtils.format(formatString, "ab", "cd", "ef");
        Assertions.assertEquals("ababccd\\\\{}aaaaef", replaced);
    }

}