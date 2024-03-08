package ppl.common.utils.config.nodes.map;

import ppl.common.utils.config.Node;
import ppl.common.utils.config.NodeFactory;
import ppl.common.utils.config.Nodes;
import ppl.common.utils.config.nodes.NullNode;
import ppl.common.utils.config.nodes.map.MapNode;
import ppl.common.utils.config.nodes.scalar.ScalarNode;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertiesFactory implements NodeFactory {

    @Override
    public int order() {
        return -29;
    }

    @Override
    public boolean accept(Object obj) {
        return obj instanceof Properties;
    }

    @Override
    public Node createRoot(Object obj) {
        return create(Node.ROOT_PATH, obj);
    }

    @Override
    public Node create(String path, Object obj) {
        if (!accept(obj)) {
            throw new IllegalArgumentException("Not properties.");
        }

        Properties properties = (Properties) obj;
        if (properties.isEmpty()) {
            return new NullNode(path);
        }

        Map<String, String[]> keys = keys(properties);
        Object root = null;
        for (Map.Entry<String, String[]> e : keys.entrySet()) {
            String k = e.getKey();
            String[] ns = e.getValue();
            String v = properties.getProperty(k);

            if (ns.length == 0) {
                root = node(root, v);
                continue;
            }

            Node.ParsedName pn = Node.ParsedName.parse(ns[0]);
            root = node(root, container(root, pn.getType()));

            PropNode container = (PropNode) root;
            Node.ParsedName currName = pn;
            for (int i = 1; i < ns.length; i++) {
                Node.ParsedName nextName = Node.ParsedName.parse(ns[i]);
                PropNode curr = (PropNode) container.get(currName.getField());
                curr = (PropNode) node(curr, container(curr, nextName.getType()));
                container.put(currName.getField(), curr);
                container = curr;
                currName = nextName;
            }

            Node.ParsedName lastName = Node.ParsedName.parse(ns[ns.length - 1]);
            Object last = container.get(lastName.getField());
            last = node(last, v);
            container.put(lastName.getField(), last);
        }

        if (root == null) {
            return new NullNode(path);
        } else if (root instanceof String) {
            return Nodes.createByPath(path, root);
        } else {
            PropNode r = (PropNode) root;
            return Nodes.createByPath(path, r.value());
        }
    }

    private Object node(Object o, Object n) {
        if (n == null) {
            return o;
        }

        if (o == null) {
            return n;
        }

        if (o == n) {
            return o;
        }

        if (o instanceof String && n instanceof String) {
            String r = (String) o;
            String v = (String) n;
            if (r.equals(v)) {
                return o;
            }
        }

        throw new IllegalArgumentException("Node already exists, cannot be changed.");
    }

    private PropNode container(Object old, int type) {
        if (old == null) {
            return new PropNode(type);
        } else if (!(old instanceof PropNode)) {
            return new PropNode(type);
        } else {
            PropNode n = (PropNode) old;
            if (n.getType() != type) {
                return new PropNode(type);
            }
            return n;
        }
    }

    private Map<String, String[]> keys(Properties properties) {
        Map<String, String[]> res = new HashMap<>();
        Enumeration<Object> keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = key(keys.nextElement());
            Matcher matcher = Node.PATH_PATTERN.matcher(key);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid path '" + key + "' in properties.");
            }

            Matcher fieldMatcher = Node.FIELD_PATTERN.matcher(key);
            List<String> tmp = new ArrayList<>();
            while (fieldMatcher.find()) {
                tmp.add(fieldMatcher.group());
            }
            res.put(key, tmp.toArray(new String[0]));
        }
        return res;
    }

    private String key(Object o) {
        if (!(o instanceof String)) {
            throw new IllegalArgumentException("Invalid config, string key is required.");
        }
        return (String) o;
    }

    private static class PropNode {
        private final int type;
        private Map<Object, Object> v;

        public PropNode(int type) {
            if (type != Node.ARRAY && type != Node.OBJECT) {
                throw new IllegalArgumentException("Unknown complex property node.");
            }
            this.type = type;
            this.v = Collections.emptyMap();
        }

        public void put(Object k, Object v) {
            if (!(k instanceof String) && !(k instanceof Integer)) {
                throw new IllegalArgumentException("Unknown key type.");
            }
            if (k instanceof String && type == Node.ARRAY || k instanceof Integer && type == Node.OBJECT) {
                throw new IllegalArgumentException("Mixed complex property node.");
            }
            if (this.v.isEmpty()) {
                this.v = new HashMap<>();
            }
            this.v.put(k, v);
        }

        public Object get(Object k) {
            return this.v.get(k);
        }

        public boolean isArray() {
            return Node.ARRAY == type;
        }

        public boolean isObject() {
            return Node.OBJECT == type;
        }

        public Object value() {
            if (type == Node.ARRAY) {
                List<Object> ret = new ArrayList<>();
                for (int i = 0; i < v.size(); i++) {
                    Object s = v.get(i);
                    if (s == null) {
                        throw new IllegalStateException("There is gap in array.");
                    }
                    ret.add(s instanceof PropNode ? ((PropNode) s).value() : s);
                }
                return ret;
            } else {
                Map<Object, Object> ret = new HashMap<>();
                for (Object k : v.keySet()) {
                    Object s = v.get(k);
                    ret.put(k, s instanceof PropNode ? ((PropNode) s).value() : s);
                }
                return ret;
            }
        }

        public int getType() {
            return type;
        }
    }
}
