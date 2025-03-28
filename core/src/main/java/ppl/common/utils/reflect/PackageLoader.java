package ppl.common.utils.reflect;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import ppl.common.utils.Arrays;

import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

public class PackageLoader {

    private final ClassLoader classLoader;
    private final Collection<String> basePackages;

    public PackageLoader(String... basePackages) {
        this(Arrays.asList(basePackages), null);
    }

    public PackageLoader(Collection<String> basePackages) {
        this(basePackages, null);
    }

    public PackageLoader(Collection<String> basePackages, ClassLoader classLoader) {
        if (basePackages.isEmpty()) {
            throw new IllegalArgumentException("Base packages is required.");
        }
        this.basePackages = new HashSet<>(basePackages);
        this.classLoader = classLoader == null ? PackageLoader.class.getClassLoader() : classLoader;
    }

    public Stream<Class<?>> load(boolean concreteOnly) {
        return load(c -> !concreteOnly || (c.getModifiers() & Modifier.ABSTRACT) == 0);
    }

    public <T> Stream<Class<? extends T>> load(Class<T> clazz) {
        return load(clazz, false);
    }

    public <T> Stream<Class<? extends T>> load(Class<T> clazz, boolean concreteOnly) {
        Objects.requireNonNull(clazz, "Target class is required.");
        return load(clazz::isAssignableFrom,
                c -> (!concreteOnly || (c.getModifiers() & Modifier.ABSTRACT) == 0))
                .map(c -> {
                    @SuppressWarnings("unchecked")
                    Class<? extends T> res = (Class<? extends T>) c;
                    return res;
                });
    }

    @SafeVarargs
    public final Stream<Class<?>> load(Predicate<Class<?>>... predicates) {
        Predicate<Class<?>>[] temp = predicates;
        if (predicates == null) {
            @SuppressWarnings("unchecked")
            Predicate<Class<?>>[] ps = (Predicate<Class<?>>[]) Arrays.zero(Predicate.class);
            temp = ps;
        }

        Set<String> resourceNames = resourceNames();
        Map<String, Enumeration<URL>> urls = new HashMap<>(resourceNames.size());

        for (String resourceName : resourceNames) {
            try {
                urls.put(resourceName, classLoader.getResources(resourceName)); //exclude package(or directory) loaded by extension or bootstrap classloader.
            } catch (IOException e) {
                throw new RuntimeException("Error occurs when finding resource: '" + resourceName + "'.", e);
            }
        }

        Stream<Class<?>> stream = Stream.of(urls.entrySet().toArray(Arrays.zero(Map.Entry.class)))
                .flatMap(e -> {
                    String resourceName = (String) e.getKey();
                    @SuppressWarnings("unchecked")
                    Enumeration<URL> us = (Enumeration<URL>) e.getValue();
                    List<Class<?>> allClasses = new ArrayList<>();
                    while (us.hasMoreElements()) {
                        allClasses.addAll(loadUserOuterClass(us.nextElement(), resourceName));
                    }
                    return allClasses.stream();
                });

        for (Predicate<Class<?>> predicate : temp) {
            stream = stream.filter(predicate);
        }
        return stream;
    }

    private Set<String> resourceNames() {
        return this.basePackages.stream()
                .map(s -> s.replaceAll("\\.", "/"))
                .collect(Collectors.toSet());
    }

    private List<Class<?>> loadUserOuterClass(URL resource, String resourceName) {
        if (resource.getProtocol().equalsIgnoreCase("jar")) {
            try (JarFile jarFile = ((JarURLConnection) resource.openConnection()).getJarFile()) {
                return loadUserOuterClassFileFromJar(jarFile, resourceName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read class from '" + resource + "'.", e);
            }
        } else if (resource.getProtocol().equalsIgnoreCase("file")) {
            try {
                File file = new File(resource.toURI());
                if (!file.exists() || !file.isDirectory()) {
                    throw new RuntimeException("Not found or not a directory. '" + resource + "'.");
                }
                return loadUserOuterClassFileFromFile(file, resourceName);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("It is not a local resource: '" + resource + "'.", e);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid uri: " + resource + ".", e);
            } catch (IOException e) {
                throw new RuntimeException("Error occurs when finding class in package: '" +
                        resourceName.replaceAll("/", ".") + "'.", e);
            }
        } else {
            throw new RuntimeException("Unknown protocol of '" + resource + "'.");
        }
    }

    private List<Class<?>> loadUserOuterClassFileFromJar(JarFile file, String resourceName) {
        return file.stream()
                .filter(e -> !e.isDirectory())
                .map(ZipEntry::getName)
                .filter(n -> n.startsWith(resourceName))
                .filter(n -> n.endsWith(".class"))
                .map(n -> n.substring(0, n.length() - 6))
                .map(n -> n.replaceAll("/", "."))
                .filter(this::isUserOuterClassName)
                .map(this::loadClass)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private List<Class<?>> loadUserOuterClassFileFromFile(File directory, String resourceName) throws IOException {
        Path realPath = directory.toPath().toRealPath(LinkOption.NOFOLLOW_LINKS);
        Path root = root(realPath, resourceName);
        return listFiles(realPath)
                .map(root::relativize)
                .map(Path::toString)
                .map(n -> n.substring(0, n.length() - 6))
                .map(n -> n.replaceAll(Pattern.quote(File.separator), "."))
                .filter(this::isUserOuterClassName)
                .map(this::loadClass)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Path root(Path realPath, String resourceName) throws IOException {
        Path root = realPath;
        Path resourceNamePath = Paths.get(resourceName);
        for (int i = 0; i < resourceNamePath.getNameCount(); i++) {
            root = root.resolve("..");
        }
        return root.toRealPath(LinkOption.NOFOLLOW_LINKS);
    }

    private Stream<Path> listFiles(Path realPath) throws IOException {
        PathMatcher pathMatcher = FileSystems.getDefault()
                .getPathMatcher("glob:**.class");
        return Files.find(realPath, Integer.MAX_VALUE, (p, u) -> pathMatcher.matches(p) && !u.isDirectory(),
                FileVisitOption.FOLLOW_LINKS);
    }

    private static final Pattern USER_OUTER_CLASSNAME_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*");

    private Boolean isUserOuterClassName(String string) {
        String name = string;
        int idx = name.lastIndexOf('.');
        if (idx >= 0) {
            name = name.substring(idx + 1);
        }
        return USER_OUTER_CLASSNAME_PATTERN.matcher(name).matches();
    }

    private Optional<Class<?>> loadClass(String className) {
        Optional<Class<?>> res = Optional.empty();
        try {
            res = Optional.of(classLoader.loadClass(className));
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            //ignore
        }
        return res;
    }

}
