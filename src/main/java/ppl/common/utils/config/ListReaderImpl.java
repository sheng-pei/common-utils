package ppl.common.utils.config;

import ppl.common.utils.config.convert.Converter;

import java.util.Iterator;
import java.util.List;

final class ListReaderImpl extends AbstractReader {

    private transient final List<?> list;

    ListReaderImpl(Object list) {
        super();
        this.list = convert(list, List.class);
    }

    ListReaderImpl(Reader parent, Object key) {
        super(parent, key);
        this.list = parent.get(key, List.class);
    }

    @Override
    protected Object get(Object key) {
        Integer intKey = toInt(key);
        return (intKey == null || intKey >= this.list.size() || intKey < 0) ? null : this.list.get(intKey);
    }

    @Override
    protected Object value() {
        return this.list;
    }

    private Integer toInt(Object key) {
        Converter<Integer> intConverter = Converter.getInstance(Integer.class);
        return intConverter.convertNullIfConvertExcept(key);
    }

    @Override
    protected String path(Object key) {
        return "L[" + key + "]";
    }

    @Override
    public Iterator<Reader> iterator() {
        return new Iter();
    }

    private class Iter implements Iterator<Reader> {

        private int cursor;
        private final int size;

        private Iter() {
            this.cursor = 0;
            this.size = ListReaderImpl.this.list.size();
        }

        @Override
        public boolean hasNext() {
            return this.cursor < this.size;
        }

        @Override
        public Reader next() {
            try {
                int key = this.cursor;
                this.cursor++;
                return ListReaderImpl.this.getChild(key);
            } catch (IndexOutOfBoundsException e) {
                throw new ReaderException("Out of bounds");
            }
        }

    }

}
