package ppl.common.utils.helper;

import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class EqualsTester {

    private BiConsumer<Boolean, String> assertMethod;
    private final List<List<Object>> equivalenceGroups = new ArrayList<>();

    public void addGroup(Object... objects) {
        this.equivalenceGroups.add(Arrays.asList(objects));
    }

    public void setAssertMethod(BiConsumer<Boolean, String> assertMethod) {
        this.assertMethod = assertMethod;
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
        this.assertMethod.accept(!o1.equals(IncompatibleObject.OBJECT),
                o1 + " must not be equals to incompatible object.");
    }

    private void testNotEqualsToNull(Object o1) {
        this.assertMethod.accept(!o1.equals(null),
                o1 + " must not be equals to null.");
    }

    private void testEqualsToItself(Object o1) {
        this.assertMethod.accept(o1.equals(o1),
                o1 + " must be equals to itself.");
        this.assertMethod.accept(o1.hashCode() == o1.hashCode(),
                "The hashCode of " + o1 + " must be consistent.");
    }

    private void testPairEqual(Object o1, Object o2) {
        this.assertMethod.accept(o1.equals(o2),
                o1 + " and " + o2 + " must be equal.");
        this.assertMethod.accept(o1.hashCode() == o2.hashCode(),
                "The hashCode of " + o1 + " and " + o2 + " must be equal.");
    }

    private void testPairNotEqual(Object o1, Object o2) {
        this.assertMethod.accept(!o1.equals(o2),
                o1 + " and " + o2 + " must not be equal.");
    }

    private enum IncompatibleObject {
        OBJECT;
    }

}
