package ppl.common.utils.helper;

import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EqualsTester {
    private final List<List<Object>> equivalenceGroups = new ArrayList<>();

    public void addGroup(Object... objects) {
        this.equivalenceGroups.add(Arrays.asList(objects));
    }

    public void test() {
        for (List<Object> group : equivalenceGroups) {
            for (Object item : group) {
                testNotEqualsToIncompatibleObject(item);
                testNotEqualsToNull(item);
                testEqualsToItself(item);
                for (Object item1 : group) {
                    if (item != item1) {
                        testPairEqual(item, item1);
                    }
                }
                for (List<Object> group1 : equivalenceGroups) {
                    if (group != group1) {
                        for (Object item1 : group1) {
                            testPairNotEqual(item, item1);
                        }
                    }
                }
            }
        }
    }

    private void testNotEqualsToIncompatibleObject(Object o1) {
        Assertions.assertNotEquals(o1, IncompatibleObject.OBJECT, o1 + " must not be equals to incompatible object.");
    }

    private void testNotEqualsToNull(Object o1) {
        Assertions.assertNotEquals(null, o1, o1 + " must not be equals to null.");
    }

    private void testEqualsToItself(Object o1) {
        Assertions.assertEquals(o1, o1, o1 + " must be equals to itself.");
        Assertions.assertEquals(o1.hashCode(), o1.hashCode(), "The hashCode of " + o1 + " must be consistent.");
    }

    private void testPairEqual(Object o1, Object o2) {
        Assertions.assertEquals(o1, o2, o1 + " and " + o2 + " must be equal.");
        Assertions.assertEquals(o1.hashCode(), o2.hashCode(), "The hashCode of " + o1 + " and " + o2 + " must be equal.");
    }

    private void testPairNotEqual(Object o1, Object o2) {
        Assertions.assertNotEquals(o1, o2, o1 + " and " + o2 + " must not be equal.");
    }

    private enum IncompatibleObject {
        OBJECT
    }

}
