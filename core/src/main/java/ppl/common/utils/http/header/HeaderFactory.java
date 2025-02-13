package ppl.common.utils.http.header;

import ppl.common.utils.http.symbol.HttpCharGroup;
import ppl.common.utils.reflect.PackageLoader;
import ppl.common.utils.string.Strings;
import ppl.common.utils.pair.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class HeaderFactory {

    private static final String[] PACKAGES_OF_KNOWN_HEADERS = new String[] {"known", "internal"};
    private static final HeaderFactory DEFAULT;

    static {
        HeaderFactory def = new HeaderFactory();
        for (String name : PACKAGES_OF_KNOWN_HEADERS) {
            load(name, def);
        }
        DEFAULT = def;
    }

    private static void load(String name, HeaderFactory headers) {
        String knownHeaderPackage = HeaderFactory.class.getPackage().getName() +
                "." + name;
        PackageLoader loader = new PackageLoader(knownHeaderPackage,
                HeaderFactory.class.getClassLoader());
        loader.load(Header.class, true)
                .map(h -> {
                    @SuppressWarnings("unchecked")
                    Class<? extends Header<? extends HeaderValue>> res =
                            (Class<? extends Header<? extends HeaderValue>>) h;
                    return res;

                })
                .forEach(headers::registerKnownHeader);
    }

    private static HeaderName getName(Class<? extends Header<? extends HeaderValue>> clazz) {
        HeaderName name = Header.nameOf(clazz);
        if (name == null) {
            throw new IllegalArgumentException(
                    "No name annotation is found in class: " +
                            clazz.getCanonicalName());
        }
        return name;
    }

    private static Constructor<Header<HeaderValue>> getConstructor(
            Class<? extends Header<? extends HeaderValue>> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        return Arrays.stream(constructors)
                .filter(c -> {
                    Class<?>[] parameterTypes = c.getParameterTypes();
                    if (parameterTypes.length == 2 &&
                            parameterTypes[0].equals(String.class) &&
                            parameterTypes[1].equals(Context.class)) {
                        return true;
                    } else if (parameterTypes.length == 1 &&
                            parameterTypes[0].equals(String.class)) {
                        return true;
                    }
                    return false;
                })
                .map(HeaderFactory::setAccessible)
                .map(c -> {
                    @SuppressWarnings("unchecked")
                    Constructor<Header<HeaderValue>> res = (Constructor<Header<HeaderValue>>) c;
                    return res;
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported to be used by: '" +
                        HeaderCreator.class.getCanonicalName() + "'."));
    }

    private static Constructor<?> setAccessible(Constructor<?> c) {
        c.setAccessible(true);
        return c;
    }

    private HeaderFactory() {
        this.headers = new HashMap<>();
        this.classToFactory = new HashMap<>();
    }

    private final Map<HeaderName, HeaderCreator> headers;
    private final Map<Class<? extends Header<? extends HeaderValue>>, HeaderCreator> classToFactory;

    private void registerKnownHeader(Class<? extends Header<? extends HeaderValue>> clazz) {
        HeaderName name = getName(clazz);
        Constructor<Header<HeaderValue>> constructor = getConstructor(clazz);
        HeaderCreator factory = (s, context) -> {
            try {
                if (constructor.getParameterCount() == 2) {
                    return constructor.newInstance(s, context);
                } else {
                    return constructor.newInstance(s);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(String.format(
                        "The constructor '%s' is not accessible.", constructor.getName()), e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(String.format(
                        "Failed to invoke constructor '%s'.", constructor.getName()), e.getCause());
            } catch (Exception e) {
                throw new RuntimeException("Unknown exception.", e);
            }
        };
        this.headers.put(name, factory);
        this.classToFactory.put(clazz, factory);
    }

    public HeaderCreator getFactory(Class<? extends Header<? extends HeaderValue>> clazz) {
        HeaderCreator factory = classToFactory.get(clazz);
        if (factory == null) {
            factory = UnknownHeader.getFactory(getName(clazz));
        }
        return factory;
    }

    public Header<HeaderValue> create(String header) {
        return create(header, (Context) null);
    }

    public Header<HeaderValue> create(String header, Context context) {
        Objects.requireNonNull(header, "Null header is not allowed.");
        Pair<String, String> pair = Strings.kv(header, Header.SEPARATOR);
        requiredSeparator(pair);
        required(pair.getFirst());
        trailingWhitespaceNotAllowed(pair.getFirst());
        return create(HeaderName.create(pair.getFirst()), pair.getSecond(), context);
    }

    private void requiredSeparator(Pair<String, String> pair) {
        if (pair.getSecond() == null) {
            throw new IllegalArgumentException(String.format("Missing separator: '%s'.", Header.SEPARATOR));
        }
    }

    private void required(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Header name is required.");
        }
    }

    private void trailingWhitespaceNotAllowed(String name) {
        if (HttpCharGroup.WS.test(name.charAt(name.length() - 1))) {
            throw new IllegalArgumentException(
                    "Invalid header name. Whitespace is not allowed between name and separator.");
        }
    }

    public Header<HeaderValue> create(String name, String value) {
        return create(name, value, null);
    }

    public Header<HeaderValue> create(String name, String value, Context context) {
        Objects.requireNonNull(name, "Null name is not allowed.");
        return create(HeaderName.create(name), value, context);
    }

    private Header<HeaderValue> create(HeaderName name, String value, Context context) {
        HeaderCreator factory;
        if (headers.containsKey(name)) {
            factory = headers.get(name);
            value = Strings.trim(value, HttpCharGroup.WS);
        } else {
            factory = UnknownHeader.getFactory(name);
        }
        return factory.create(value, context);
    }

    public static HeaderFactory def() {
        return DEFAULT;
    }

}
