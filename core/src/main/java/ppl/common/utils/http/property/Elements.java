package ppl.common.utils.http.property;

import ppl.common.utils.http.Name;
import ppl.common.utils.reflect.PackageLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Elements {

    private static final Function<Object, Element<Object>> DEFAULT_FACTORY = v -> (Element<Object>) () -> v;
    private static final Elements DEFAULT_ELEMENTS = new Elements();
    private static final String PACKAGE_OF_ELEMENTS = "elements";

    static {
        String basePackage = Properties.class.getPackage().getName() + "." + PACKAGE_OF_ELEMENTS;
        PackageLoader loader = new PackageLoader(basePackage, Properties.class.getClassLoader());
        @SuppressWarnings({"rawtypes", "unchecked"})
        Class<Element<?>> eClass = (Class) Element.class;
        loader.load(eClass, true)
                .forEach(c -> {
                    String name = getName(c);
                    Constructor<? extends Element<?>> constructor = getConstructorWithSingleObjectParameter(c);
                    DEFAULT_ELEMENTS.putFactory(name, s -> {
                        try {
                            return constructor.newInstance(s);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(String.format(
                                    "The constructor '%s' is not accessible.", constructor.getName()), e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(String.format(
                                    "Failed to invoke constructor '%s'.", constructor.getName()), e.getCause());
                        } catch (Exception e) {
                            throw new RuntimeException("Unknown exception.", e);
                        }
                    });
                });
    }

    private static String getName(Class<? extends Element<?>> clazz) {
        Name name = clazz.getAnnotation(Name.class);
        if (name == null) {
            throw new IllegalArgumentException(
                    "No name annotation is found in class: " +
                            clazz.getCanonicalName());
        }
        return name.value();
    }

    private static Constructor<? extends Element<?>> getConstructorWithSingleObjectParameter(
            Class<? extends Element<?>> clazz) {
        try {
            Constructor<? extends Element<?>> constructor = clazz.getDeclaredConstructor(Object.class);
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "No constructor with single parameter Object is found in class: " +
                            clazz.getCanonicalName());
        }
    }

    @SuppressWarnings("rawtypes")
    private final Map factories;

    private Elements() {
        this.factories = new HashMap<>();
    }

    private void putFactory(String name, Function<Object, ? extends Element<?>> factory) {
        @SuppressWarnings("unchecked")
        Map<String, Function<Object, ? extends Element<?>>> factories = this.factories;
        if (factories.containsKey(name)) {
            throw new IllegalArgumentException("Property factory: '" + name + "' is already set.");
        }
        factories.put(name, factory);
    }

    Element<Object> create(String name, Object value) {
        @SuppressWarnings("unchecked")
        Function<Object, Element<Object>> factory =
                (Function<Object, Element<Object>>) this.factories.getOrDefault(name, DEFAULT_FACTORY);
        return factory.apply(value);
    }

    static Elements elements() {
        return DEFAULT_ELEMENTS;
    }

}
