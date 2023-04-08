package ppl.common.utils.command.argument.collector;

import ppl.common.utils.command.argument.Collector;

import java.util.HashMap;
import java.util.Map;

public class KVCollector implements Collector<String, Map<String, String>> {
    private final String delim;
    private final Map<String, String> map;

    KVCollector() {
        this("=");
    }

    KVCollector(String delim) {
        this.delim = delim;
        this.map = new HashMap<>();
    }

    @Override
    public Map<String, String> collect(String v) {
        parseKVPair(v);
        return map;
    }

    private void parseKVPair(String v) {
        int equalIdx = v.indexOf(delim);
        String key = v;
        if (equalIdx >= 0) {
            key = v.substring(0, equalIdx).trim();
        }
        if (key.isEmpty()) {
            throw new CollectorException("Invalid k-v-pair. No key provided.");
        }

        String value = "";
        if (equalIdx >= 0) {
            value = v.substring(equalIdx + 1);
        }

        if (map.containsKey(key)) {
            throw new CollectorException("Invalid k-v-pair. Duplicate key: " + key);
        }
        map.put(key, value);
    }

}
