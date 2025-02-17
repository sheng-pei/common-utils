package ppl.common.utils.config.nodes.propertey;

import ppl.common.utils.Maps;
import ppl.common.utils.config.Node;
import ppl.common.utils.config.nodes.scalar.ScalarNode;
import ppl.common.utils.string.Strings;

import java.util.*;

class Property {
    private Integer type;
    private Object scalar;
    private Map<Object, Property> complex;

    Property() {
        this.type = null;
        this.scalar = null;
        this.complex = null;
    }

    Property ensureAndGet(Node.ParsedName name) {
        int t = name.getType();
        if (this.type == null) {
            this.type = t;
            complex = name.isArray() ? new TreeMap<>() : new HashMap<>();
        } else if (this.type != t) {
            throw new IllegalArgumentException("Property is not allowed to be array and object at the same time.");
        }
        return complex.computeIfAbsent(name.getField(), k -> new Property());
    }

    void setScalar(Object scalar) {
        if (scalar != null && !ScalarNode.isScalar(scalar)) {
            throw new IllegalArgumentException("Property value must be scalar.");
        }
        if (this.scalar != null && !Objects.equals(this.scalar, scalar)) {
            throw new IllegalArgumentException("Scalar value already exists, cannot be changed.");
        }
        this.scalar = scalar;
    }

    Node toNode(String path) {
        if (Maps.isEmpty(complex)) {
            if (scalar == null ||
                    scalar instanceof String && Strings.isEmpty(scalar.toString())) {
                return new EmptyPropertiesNode(path);
            }
            return new ValuePropertiesNode(path, scalar);
        } else {
            if (Objects.equals(type, Node.ARRAY)) {
                @SuppressWarnings({"rawtypes", "unchecked"})
                Map<Integer, Property> map = (Map) complex;
                List<Property> list = new ArrayList<>();
                int idx = 0;
                for (Integer i : map.keySet()) {
                    if (i != idx) {
                        throw new IllegalStateException("There is gap in array.");
                    }
                    i = idx;
                    list.add(map.get(i));
                }
                return new ListPropertiesNode(path, scalar, list);
            } else {
                @SuppressWarnings({"rawtypes", "unchecked"})
                Map<String, Property> map = (Map) complex;
                return new MapPropertiesNode(path, scalar, map);
            }
        }
    }
}
