package ppl.common.utils.attire.proxy.server;

import ppl.common.utils.Arrays;
import ppl.common.utils.reflect.PackageLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServerLoader {
    private final List<String> basePackages;
    private final transient PackageLoader loader;

    public ServerLoader(String... basePackages) {
        this.basePackages = Arrays.asList(basePackages);
        this.loader = new PackageLoader(basePackages);
    }

    public ServerLoader(Collection<String> basePackages) {
        this.basePackages = new ArrayList<>(basePackages);
        this.loader = new PackageLoader(basePackages);
    }

    public ServerLoader(Collection<String> basePackages, ClassLoader classLoader) {
        this.basePackages = new ArrayList<>(basePackages);
        this.loader = new PackageLoader(basePackages, classLoader);
    }

//    public Stream<Class<?>> load() {
//        loader.load()
//                .filter(Class::isInterface)
//                .filter(c -> !c.isAnnotation())
//                .filter(c -> c.isAnnotationPresent(Server.class));
//
//    }

    private boolean isValidServer(Class<?> interface1) {
        Server[] annotations = interface1.getAnnotationsByType(Server.class);
        if (annotations.length > 1 && Arrays.stream(annotations)
                .map(Server::name)
                .distinct()
                .count() > 1) {
            throw new IllegalArgumentException("Distinct name servers are not allowed to be annotated with one interface.");
        }
        return false;
    }
}
