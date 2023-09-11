package ppl.common.utils.hdfs.selector;

import ppl.common.utils.http.url.URL;

import java.util.function.Supplier;

public final class Selectors {
    private Selectors() {}

    public static Supplier<Selector> nextSelector(String prefix, URL... nameNodes) {
        if (nameNodes.length == 0) {
            throw new IllegalArgumentException("No name node is given.");
        }
        return new NextSelector.SupplierImpl(prefix, nameNodes);
    }

}
