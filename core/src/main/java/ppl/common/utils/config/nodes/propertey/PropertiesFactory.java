package ppl.common.utils.config.nodes.propertey;

import ppl.common.utils.config.Node;
import ppl.common.utils.config.NodeException;
import ppl.common.utils.config.NodeFactory;

import java.util.*;
import java.util.regex.Matcher;

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
            throw new NodeException("Not properties.");
        }

        Property root = new Property();
        Properties properties = (Properties) obj;
        try {
            Map<String, String[]> keys = keys(properties);
            for (Map.Entry<String, String[]> e : keys.entrySet()) {
                String k = e.getKey();
                String[] ns = e.getValue();
                Object v = properties.get(k);

                Property property = root;
                for (String n : ns) {
                    Node.ParsedName field = Node.ParsedName.parse(n);
                    property = property.ensureAndGet(field);
                }
                property.setScalar(v);
            }
        } catch (RuntimeException e) {
            throw new NodeException("Properties error.", e);
        }

        return root.toNode(path);
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
            throw new IllegalArgumentException("Invalid config, key must be string.");
        }
        return (String) o;
    }

}
