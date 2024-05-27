package ppl.common.utils.string.substring.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class PositionedTargetTest {

    @Test
    public void testForEach() {
        ToStringArguments targets = new ToStringArguments("a", "b");
        List<String> actual = new ArrayList<>();
        actual.add("a");
        actual.add("b");

        List<String> expected = new ArrayList<>();
        while (targets.available()) {
            expected.add(targets.consume());
        }
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testNotAvailable() {
        ToStringArguments targets = new ToStringArguments();
        Assertions.assertFalse(targets.available());
    }

    @Test
    public void testConsumeWhenNotAvailable() {
        ToStringArguments targets = new ToStringArguments();
        Assertions.assertThrows(IllegalStateException.class, targets::consume);
    }

}
