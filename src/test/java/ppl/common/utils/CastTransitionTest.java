package ppl.common.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CastTransitionTest {

    public static ExpectedException expectedException = ExpectedException.none();

    private static class TestObject {
        String v;
        private TestObject(String v) {
            this.v = v;
        }
        public int hashCode() {
            return System.identityHashCode(v);
        }
        public String toString() {
            return v;
        }
    }

    @Test
    public void testCastNull() {
        Assert.assertNull(Conversion.castInteger(null));
    }

    @Test(expected = ClassCastException.class)
    public void testCastException() {
        Conversion.castInteger("1");
    }

    @Test
    public void testCastIntegerToLong() {
        Assert.assertTrue(Conversion.castLong(1) instanceof Long);
        Assert.assertEquals(1L, Conversion.castLong(1).longValue());
    }

    @Test
    public void testCastFloatToDouble() {
        Assert.assertTrue(Conversion.castDouble(0.1f) instanceof Double);
        Assert.assertEquals(0.1d, Conversion.castDouble(0.1f), 0.01);
    }

}
