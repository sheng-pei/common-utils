package ppl.common.utils.config;

import ppl.common.utils.exception.ReaderException;

import java.util.Iterator;

final class ScalarReaderImpl extends AbstractReader {

    private transient final Object scalar;

    ScalarReaderImpl(Object scalar) {
        super();
        this.scalar = scalar;
    }

    ScalarReaderImpl(Reader parent, Object key) {
        super(parent, key);
        this.scalar = parent.get(key, Object.class);
    }

    @Override
    protected String path(Object key) {
        return "S[" + key + "]";
    }

    @Override
    protected Object get(Object key) {
        return null;
    }

    @Override
    protected Object value() {
        return scalar;
    }

    @Override
    public Iterator<Reader> iterator() {
        return new Iter();
    }

    private class Iter implements Iterator<Reader> {

        private boolean consumed = false;

        @Override
        public boolean hasNext() {
            return !consumed;
        }

        @Override
        public Reader next() {
            if (this.consumed) {
                throw new ReaderException("Out of bounds");
            }

            this.consumed = true;
            return ScalarReaderImpl.this;
        }
    }

}
