package ppl.common.utils.hdfs.selector;

import ppl.common.utils.filesystem.Path;
import ppl.common.utils.filesystem.path.BasePath;
import ppl.common.utils.http.url.URL;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

class NextSelector implements Selector {

    private final SupplierImpl supplier;
    private int active;

    private NextSelector(SupplierImpl supplier) {
        this.supplier = supplier;
        this.active = supplier.active;
    }

    @Override
    public int maxAttempts(int maxAttempts) {
        int res = supplier.nameNodes.length;
        if (maxAttempts > res) {
            res = maxAttempts;
        }
        return res;
    }

    @Override
    public URL init(Path path) {
        Objects.requireNonNull(path);
        active = supplier.active;
        URL url = supplier.current();
        if (path.isAbsolute()) {
            path = BasePath.get(Path.C_ROOT_DIR).relativize(path);
        }
        return URL.create(url.toString() + path.normalize().toString());
    }

    @Override
    public URL next(Path path) {
        Objects.requireNonNull(path);
        URL url = supplier.get(++active);
        if (path.isAbsolute()) {
            path = BasePath.get(Path.C_ROOT_DIR).relativize(path);
        }
        return URL.create(url.toString() + path.normalize().toString());
    }

    @Override
    public void finish() {
        supplier.active = this.active;
    }

    static class SupplierImpl implements Supplier<Selector> {

        private final URL[] nameNodes;
        private volatile int active;

        SupplierImpl(String prefix, URL[] nameNodes) {
            this.nameNodes = Arrays.stream(nameNodes)
                    .map(u -> URL.create(u.toString() + prefix))
                    .toArray(URL[]::new);
            this.active = 0;
        }

        @Override
        public Selector get() {
            return new NextSelector(this);
        }

        private URL current() {
            return nameNodes[active];
        }

        private URL get(int idx) {
            return nameNodes[idx % nameNodes.length];
        }

    }
}
