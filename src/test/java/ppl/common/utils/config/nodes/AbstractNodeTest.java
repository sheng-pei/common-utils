package ppl.common.utils.config.nodes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ppl.common.utils.config.Node;

import java.util.Iterator;
import java.util.stream.Stream;

public class AbstractNodeTest {
    private static final class KeyNode extends AbstractNode {

        public KeyNode(String path) {
            super(path);
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException("");
        }

        @Override
        public Node getChild(String fieldName) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public Node getChild(Integer index) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public Iterator<Node> iterator() {
            throw new UnsupportedOperationException("");
        }

        @Override
        public String textValue(String def) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public String textValue() {
            throw new UnsupportedOperationException("");
        }

        @Override
        public Byte byteValue(Byte def) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public Byte byteValue() {
            throw new UnsupportedOperationException("");
        }

        @Override
        public Short shortValue(Short def) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public Short shortValue() {
            throw new UnsupportedOperationException("");
        }

        @Override
        public Integer intValue(Integer def) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public Integer intValue() {
            throw new UnsupportedOperationException("");
        }

        @Override
        public Long longValue(Long def) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public Long longValue() {
            throw new UnsupportedOperationException("");
        }

        @Override
        public Boolean boolValue(Boolean def) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public Boolean boolValue() {
            throw new UnsupportedOperationException("");
        }

        @Override
        public Double doubleValue(Double def) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public Double doubleValue() {
            throw new UnsupportedOperationException("");
        }

        @Override
        public <E extends Enum<E>> E enumValue(Class<E> enumClass) {
            throw new UnsupportedOperationException("");
        }
    }

    private static Stream<Arguments> fieldNameProvider() {
        return Stream.of(
                Arguments.of(".", ""),
                Arguments.of(".{a}", "a"),
                Arguments.of(".a", "a"),
                Arguments.of(".[1]", null),
                Arguments.of(".1", "1"),
                Arguments.of(".{a.b}", "a.b")
        );
    }

    @ParameterizedTest(name = "The field name of path: ''{0}'' is ''{1}''")
    @MethodSource("fieldNameProvider")
    public void testFieldName(String path, String fieldName) {
        KeyNode keyNode = new KeyNode(path);
        Assertions.assertEquals(fieldName, keyNode.fieldName());
    }

    private static Stream<Arguments> indexProvider() {
        return Stream.of(
                Arguments.of(".", null),
                Arguments.of(".{a}", null),
                Arguments.of(".a", null),
                Arguments.of(".[1]", 1),
                Arguments.of(".1", null),
                Arguments.of(".{a.b}", null)
        );
    }

    @ParameterizedTest(name = "The index of path: ''{0}'' is {1}")
    @MethodSource("indexProvider")
    public void testIndex(String path, Integer index) {
        KeyNode keyNode = new KeyNode(path);
        Assertions.assertEquals(index, keyNode.index());
    }

}
