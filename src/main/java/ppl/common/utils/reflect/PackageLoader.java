package ppl.common.utils.reflect;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

public class PackageLoader {

    private final ClassLoader classLoader;
    private final String basePackage;

    public PackageLoader(String basePackage) {
        this(basePackage, null);
    }

    public PackageLoader(String basePackage, ClassLoader classLoader) {
        if (basePackage == null) {
            throw new IllegalArgumentException("Base package is required.");
        }
        this.basePackage = basePackage;
        this.classLoader = classLoader == null ? PackageLoader.class.getClassLoader() : classLoader;
    }

    public Stream<Class<?>> load() {
        String resourceName = resourceName();

        Enumeration<URL> urls;
        try {
            urls = classLoader.getResources(resourceName); //exclude package(or directory) loaded by extension or bootstrap classloader.
        } catch (IOException e) {
            throw new RuntimeException("Error occurs when finding resource: '" + resourceName + "'.", e);
        }

        List<Class<?>> allClasses = new ArrayList<>();
        while (urls.hasMoreElements()) {
            allClasses.addAll(loadUserOuterClass(urls.nextElement(), resourceName));
        }

        return allClasses.stream();
    }

    public <T> Stream<Class<? extends T>> load(Class<T> clazz) {
        return load(clazz, false);
    }

    public <T> Stream<Class<? extends T>> load(Class<T> clazz, boolean shouldNotAbstract) {
        Objects.requireNonNull(clazz, "Target class is required.");
        return load()
                .filter(clazz::isAssignableFrom)
                .filter(c -> !shouldNotAbstract || (c.getModifiers() & Modifier.ABSTRACT) == 0)
                .map(c -> {
                    @SuppressWarnings("unchecked")
                    Class<? extends T> res = (Class<? extends T>) c;
                    return res;
                });
    }

    private String resourceName() {
        return this.basePackage.replaceAll("\\.", "/");
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
                .filter(this::isUserOuterClassName)
                .map(n -> n.replaceAll("/", "."))
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
                .filter(this::isUserOuterClassName)
                .map(n -> n.replaceAll("/", "."))
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
                .getPathMatcher("glob:" + realPath.resolve("**.class"));
        return Files.find(realPath, Integer.MAX_VALUE,
                (p, u) -> pathMatcher.matches(p) && !u.isDirectory(),
                FileVisitOption.FOLLOW_LINKS);
    }

    private static final Pattern USER_OUTER_CLASSNAME_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*");
    private Boolean isUserOuterClassName(String string) {
        String name = string;
        int idx = name.lastIndexOf('/');
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
