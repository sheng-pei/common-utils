package ppl.common.utils.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class MapReaderImpl extends AbstractReader {

    private transient final Map<?, ?> map;

    MapReaderImpl(Object map) {
        super();
        this.map = convert(map, Map.class);
    }

    MapReaderImpl(Reader parent, Object key) {
        super(parent, key);
        this.map = parent.get(key, Map.class);
    }

    @Override
    protected String path(Object key) {
        return key.toString();
    }

    @Override
    protected Object get(Object key) {
        return this.map.get(key);
    }

    @Override
    protected Object value() {
        return this.map;
    }

    @Override
    public Iterator<Reader> iterator() {
        return new Iter();
    }

    private class Iter implements Iterator<Reader> {

        private final List<?> keys;
        private int cursor;

        private Iter() {
            this.keys = new ArrayList<>(MapReaderImpl.this.map.keySet());
            this.cursor = 0;
        }

        @Override
        public boolean hasNext() {
            return this.cursor < this.keys.size();
        }

        @Override
        public Reader next() {
            try {
                int i = this.cursor;
                Object k = this.keys.get(i);
                this.cursor++;
                return MapReaderImpl.this.getChild(k);
            } catch (IndexOutOfBoundsException e) {
                throw new ReaderException("Out of bounds");
            }
        }

    }

}
