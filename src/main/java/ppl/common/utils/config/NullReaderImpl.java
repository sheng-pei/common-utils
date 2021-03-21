package ppl.common.utils.config;

import ppl.common.utils.exception.ReaderException;

import java.util.Iterator;

final class NullReaderImpl extends AbstractReader {

    NullReaderImpl() {
        super();
    }

    NullReaderImpl(Reader parent, Object key) {
        super(parent, key);
    }

    @Override
    protected Object get(Object key) {
        return null;
    }

    @Override
    protected String path(Object key) {
        return "N[" + key + "]";
    }

    @Override
    protected Object value() {
        return null;
    }

    @Override
    public Iterator<Reader> iterator() {
        return new Iterator<Reader>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Reader next() {
                throw new ReaderException("Out of bounds");
            }
        };
    }

}
