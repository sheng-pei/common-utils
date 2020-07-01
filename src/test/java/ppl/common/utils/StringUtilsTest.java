package ppl.common.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StringUtilsTest {

    private static final String[] EMPTY_ARRAY = new String[0];
    public static ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testSplitNullByEmptyRegexSeparator() {
        String[] actual = StringUtils.splitByRegexSeparator(null, "");
        Assert.assertArrayEquals(EMPTY_ARRAY, actual);
    }

    @Test
    public void testSplitEmptyStringByEmptyRegexSeparator() {
        String[] actual = StringUtils.splitByRegexSeparator("", "");
        Assert.assertArrayEquals(EMPTY_ARRAY, actual);
    }

    @Test
    public void testSplitByEmptyRegexSeparator() {
        String[] actual = StringUtils.splitByRegexSeparator("abndc", "");
        String[] expected = {"a", "b", "n", "d", "c"};
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testSplitNullByRegexSeparator() {
        String[] actual = StringUtils.splitByRegexSeparator(null, ",*");
        Assert.assertArrayEquals(EMPTY_ARRAY, actual);
    }

    @Test
    public void testSplitEmptyStringByRegexSeparator() {
        String[] actual = StringUtils.splitByRegexSeparator("", ",*");
        Assert.assertArrayEquals(EMPTY_ARRAY, actual);
    }

    @Test
    public void testSplitWholeMatchingByRegexSeparator() {
        String[] actual = StringUtils.splitByRegexSeparator(",", ",*");
        String[] expected = {""};
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testSplitHeadPositiveWidthMatchingByRegexSeparator() {
        String[] actual = StringUtils.splitByRegexSeparator(",a", ",*");
        String[] expected = {"", "a"};
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testSplitHeadZeroWidthMatchingByRegexSeparator() {
        String[] actual = StringUtils.splitByRegexSeparator("a", ",*");
        String[] expected = {"a"};
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testSplitIgnoreTrailingEmptyStringByRegexSeparator() {
        String[] actual = StringUtils.splitByRegexSeparator("a,", ",*");
        String[] expected = {"a"};
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testSplitByRegexSeparator() {
        String[] actual = StringUtils.splitByRegexSeparator("a,bfd,,,,,d,,,,,", ",*");
        String[] expected = {"a", "b", "f", "d", "d"};
        Assert.assertArrayEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitByNullSeparator() {
        StringUtils.splitByRegexSeparator("", null);
    }

    @Test
    public void testUniqueOfNull() {
        String[] actual = StringUtils.unique(null);
        Assert.assertArrayEquals(EMPTY_ARRAY, actual);
    }

    @Test
    public void testUnique() {
        String[] input = {"a", "b", "a"};
        String[] expected = {"a", "b"};

        String[] actual = StringUtils.unique(input);

        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testIsEmptyOfNull() {
        Assert.assertTrue(StringUtils.isEmpty(null));
    }

    @Test
    public void testIsEmptyOfZeroLengthString() {
        Assert.assertTrue(StringUtils.isEmpty(""));
    }

    @Test
    public void testIsEmptyOfNonZeroLengthString() {
        Assert.assertFalse(StringUtils.isEmpty(" a,"));
    }

    @Test
    public void testIsNotEmptyOfNull() {
        Assert.assertFalse(StringUtils.isNotEmpty(null));
    }

    @Test
    public void testIsNotEmptyOfZeroLengthString() {
        Assert.assertFalse(StringUtils.isNotEmpty(""));
    }

    @Test
    public void testIsNotEmptyOfNonZeroLengthString() {
        Assert.assertTrue(StringUtils.isNotEmpty(" a,"));
    }

    @Test
    public void testIsBlankOfNull() {
        Assert.assertTrue(StringUtils.isBlank(null));
    }

    @Test
    public void testIsBlankOfZeroLengthString() {
        Assert.assertTrue(StringUtils.isBlank(""));
    }

    @Test
    public void testIsBlankOfStringContainedOnlyWhitespace() {
        String whitespace = " \t\n\r";
        Assert.assertTrue(StringUtils.isBlank(whitespace));
    }

    @Test
    public void testIsBlankOfStringContainedNonWhitespace() {
        String whitespace = " \t\n\rc\r\n\t ";
        Assert.assertFalse(StringUtils.isBlank(whitespace));
    }

    @Test
    public void testEqualsNullNull() {
        Assert.assertTrue(StringUtils.equals(null, null));
    }

    @Test
    public void testEqualsNullNonnull() {
        Assert.assertFalse(StringUtils.equals(null, ""));
    }

    @Test
    public void testEqualsNonnullNull() {
        Assert.assertFalse(StringUtils.equals("", null));
    }

    @Test
    public void testEquals() {
        Assert.assertTrue(StringUtils.equals("aavgf", "aavgf"));
    }

    @Test
    public void testToSnakeAndLowerCaseMM() {
        String MM = "MM";
        String mm = StringUtils.toSnakeAndLowerCase(MM);
        Assert.assertEquals("mm", mm);
    }

    @Test
    public void testToSnakeAndLowerCasemM() {
        String mM = "mM";
        String m_m = StringUtils.toSnakeAndLowerCase(mM);
        Assert.assertEquals("m_m", m_m);
    }

    @Test
    public void testToSnakeAndLowerCasem_M() {
        String m_M = "m_M";
        String m_m = StringUtils.toSnakeAndLowerCase(m_M);
        Assert.assertEquals("m_m", m_m);
    }

    @Test
    public void testFormatJustSingleReference() {
        String singleReference = "{}";
        String replaced = StringUtils.format(singleReference, "ab");
        Assert.assertEquals("ab", replaced);

        System.out.println(StringUtils.format("abc{}", "ab"));
        System.out.println(StringUtils.format("a\\\\\\\\{}aaa", "ab"));
    }

    @Test
    public void testFormatNotSpecialBackslash() {
        String noReferenceBackslash = "ab\\\\{";
        String replaced = StringUtils.format(noReferenceBackslash);
        Assert.assertEquals("ab\\\\{", replaced);
    }

    @Test
    public void testFormatSpecialBackslash() {
        String specialBackslash = "abc\\\\\\{}";
        String replaced = StringUtils.format(specialBackslash, "ab");
        Assert.assertEquals("abc\\{}", replaced);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFormatNotEnoughParameter() {
        String formatString = "{}";
        StringUtils.format(formatString);
    }

    @Test
    public void testFormat() {
        String formatString = "{}abc{}\\{}aaaa{}";
        String replaced = StringUtils.format(formatString, "ab", "cd", "ef");
        Assert.assertEquals("ababccd{}aaaaef", replaced);
    }

}