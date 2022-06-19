package ppl.common.utils.config;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ppl.common.utils.config.nodes.jackson.JacksonNode;
import ppl.common.utils.config.nodes.list.ListNode;
import ppl.common.utils.config.nodes.map.MapNode;
import ppl.common.utils.config.nodes.scalar.ScalarNode;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class NodesTest {

    private static final class ListInterface implements List<Object> {

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<Object> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean add(Object o) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(int index, Collection<?> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public Object get(int index) {
            return null;
        }

        @Override
        public Object set(int index, Object element) {
            return null;
        }

        @Override
        public void add(int index, Object element) {

        }

        @Override
        public Object remove(int index) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }

        @Override
        public ListIterator<Object> listIterator() {
            return null;
        }

        @Override
        public ListIterator<Object> listIterator(int index) {
            return null;
        }

        @Override
        public List<Object> subList(int fromIndex, int toIndex) {
            return null;
        }
    }

    private static final class MapInterface implements Map<Object, Object> {

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public Object get(Object key) {
            return null;
        }

        @Override
        public Object put(Object key, Object value) {
            return null;
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public void putAll(Map<?, ?> m) {

        }

        @Override
        public void clear() {

        }

        @Override
        public Set<Object> keySet() {
            return null;
        }

        @Override
        public Collection<Object> values() {
            return null;
        }

        @Override
        public Set<Entry<Object, Object>> entrySet() {
            return null;
        }
    }

    private static final class JacksonInterface extends JsonNode {

        @Override
        public <T extends JsonNode> T deepCopy() {
            return null;
        }

        @Override
        public JsonToken asToken() {
            return null;
        }

        @Override
        public JsonParser.NumberType numberType() {
            return null;
        }

        @Override
        public JsonNode get(int index) {
            return null;
        }

        @Override
        public JsonNode path(String fieldName) {
            return null;
        }

        @Override
        public JsonNode path(int index) {
            return null;
        }

        @Override
        public JsonParser traverse() {
            return null;
        }

        @Override
        public JsonParser traverse(ObjectCodec codec) {
            return null;
        }

        @Override
        protected JsonNode _at(JsonPointer ptr) {
            return null;
        }

        @Override
        public JsonNodeType getNodeType() {
            return null;
        }

        @Override
        public String asText() {
            return null;
        }

        @Override
        public JsonNode findValue(String fieldName) {
            return null;
        }

        @Override
        public JsonNode findPath(String fieldName) {
            return null;
        }

        @Override
        public JsonNode findParent(String fieldName) {
            return null;
        }

        @Override
        public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar) {
            return null;
        }

        @Override
        public List<String> findValuesAsText(String fieldName, List<String> foundSoFar) {
            return null;
        }

        @Override
        public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar) {
            return null;
        }

        @Override
        public String toString() {
            return null;
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {

        }

        @Override
        public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {

        }
    }

    private static final class NegativeOrderNodeFactory implements NodeFactory {
        @Override
        public int order() {
            return -9;
        }

        @Override
        public boolean accept(Object material) {
            return false;
        }

        @Override
        public Node createRoot(Object material) {
            return null;
        }

        @Override
        public Node create(String path, Object material) {
            return null;
        }
    }

    private static final class OneNodeFactory implements NodeFactory {

        @Override
        public int order() {
            return 1;
        }

        @Override
        public boolean accept(Object material) {
            return false;
        }

        @Override
        public Node createRoot(Object material) {
            return null;
        }

        @Override
        public Node create(String path, Object material) {
            return null;
        }
    }

    private static final class AnotherOneNodeFactory implements NodeFactory {

        @Override
        public int order() {
            return 1;
        }

        @Override
        public boolean accept(Object material) {
            return false;
        }

        @Override
        public Node createRoot(Object material) {
            return null;
        }

        @Override
        public Node create(String path, Object material) {
            return null;
        }
    }

    @Test
    public void testOrderMustBePositive() {
        List<NodeFactory> factories = new ArrayList<>();
        factories.add(new NegativeOrderNodeFactory());

        Nodes.ignoreNegativeOrderFactories(factories);

        Assertions.assertTrue(factories.isEmpty());
    }

    @Test
    public void testOrderMustBeUnique() {
        List<NodeFactory> factories = new ArrayList<>();
        factories.add(new OneNodeFactory());
        factories.add(new AnotherOneNodeFactory());

        Nodes.removeDuplicateOrderFactories(factories);

        Assertions.assertEquals(1, factories.size());
    }

    private static final Map<String, Object> TARGET_OBJECTS;

    static {
        Map<String, Object> targets = new HashMap<>();
        targets.put("scalar", new Object());
        targets.put("list", new ListInterface());
        targets.put("map", new MapInterface());
        targets.put("jackson", new JacksonInterface());
        TARGET_OBJECTS = targets;
    }

    private static Stream<Arguments> systemFactoryProvider() {
        return Stream.of(
                Arguments.of("scalar", ScalarNode.class),
                Arguments.of("list", ListNode.class),
                Arguments.of("map", MapNode.class),
                Arguments.of("jackson", JacksonNode.class)
        );
    }

    @ParameterizedTest(name = "''{0}'' node has {1}")
    @MethodSource("systemFactoryProvider")
    public void testSystemFactories(String type, Class<? extends Node> nodeClass) {
        Node node = Nodes.root(TARGET_OBJECTS.get(type));
        Assertions.assertTrue(nodeClass.isInstance(node));
    }

    private static Stream<Arguments> invalidPathProvider() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of(".."),
                Arguments.of(".{}"),
                Arguments.of(".[a]"),
                Arguments.of(".{{}"),
                Arguments.of(".{}}"),
                Arguments.of(".{[}"),
                Arguments.of(".{]}"),
                Arguments.of(".{a}.")
        );
    }

    @ParameterizedTest(name = "Invalid path: ''{0}''")
    @MethodSource("invalidPathProvider")
    public void testInvalidPath(String path) {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Nodes.createByPath(path, new Object()));
    }

    private static Stream<Arguments> pathProvider() {
        return Stream.of(
                Arguments.of("."),
                Arguments.of(".[1]"),
                Arguments.of(".{a}"),
                Arguments.of(".{a.b}"),
                Arguments.of(".a"),
                Arguments.of(".1"),
                Arguments.of(".a.b")
        );
    }

    @ParameterizedTest(name = "Valid path: ''{0}''")
    @MethodSource("pathProvider")
    public void testPath(String path) {
        Node node = Nodes.createByPath(path, new Object());
        Assertions.assertEquals(path, node.path());
    }
}
