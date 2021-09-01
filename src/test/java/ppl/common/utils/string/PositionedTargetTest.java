package ppl.common.utils.string;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class PositionedTargetTest {

    @ParameterizedTest
    @MethodSource({"outOfBoundProvider"})
    public void testOutOfBound(Executable exe) {
        Assertions.assertThrows(IllegalStateException.class, exe);
    }

    private static Stream<Arguments> outOfBoundProvider() {
        PositionedTargets targets = new PositionedTargets();
        Executable consume = targets::consume;
        Executable get = targets::get;
        return Stream.of(
                Arguments.of(consume),
                Arguments.of(get)
        );
    }

    @ParameterizedTest
    @MethodSource({"outOfBoundProvider"})
    public void testConsume() {
        PositionedTargets targets = new PositionedTargets("a", "b");
        targets.consume();
        Object actual = targets.get();
        Assertions.assertEquals("b", actual);
    }

}
