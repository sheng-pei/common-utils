package ppl.common.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class EnumUtilsTest {

    public ExpectedException expectedException = ExpectedException.none();

//    private enum TestEnum {
//
//        A("a"),
//        B("b");
//
//        private String key;
//        TestEnum(String key) {
//            this.key = key;
//        }
//
//        public static TestEnum enumOf(String key) {
//            return EnumUtils.enumOf(TestEnum.class, key, (em, k) -> StringUtils.equals(em.key, k));
//        }
//
//    }
//
//    @Test
//    public void testUsageOfEnumOf() {
//        TestEnum actual = TestEnum.enumOf("a");
//        Assert.assertTrue(TestEnum.A == actual);
//    }
//
//    @Test
//    public void testEnumOf() {
//        TestEnum actual = EnumUtils.enumOf(TestEnum.class, "a", (em, k) -> StringUtils.equals(em.key, k));
//        Assert.assertTrue(TestEnum.A == actual);
//    }
//
//    @Test(expected = UnknownEnumException.class)
//    public void testEnumOfUnknown() {
//        EnumUtils.enumOf(TestEnum.class, "ac", (em, k) -> StringUtils.equals(em.key, k));
//    }

}
