package ppl.common.utils.http.property;

import ppl.common.utils.http.Name;
import ppl.common.utils.reflect.PackageLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ElementFactory {

    private static final ElementCreator<Object> ELEMENT_CREATOR = v -> (Element<Object>) () -> v;
    private static final ElementFactory DEFAULT_ELEMENT_FACTORY = new ElementFactory();
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
                    DEFAULT_ELEMENT_FACTORY.putCreator(name, s -> {
                        try {
                            @SuppressWarnings("unchecked")
                            Element<Object> ret = (Element<Object>) constructor.newInstance(s);
                            return ret;
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
    private final Map creators;

    private ElementFactory() {
        this.creators = new HashMap<>();
    }

    private void putCreator(String name, ElementCreator<Object> creator) {
        @SuppressWarnings("unchecked")
        Map<String, ElementCreator<Object>> creators = this.creators;
        if (creators.containsKey(name)) {
            throw new IllegalArgumentException("Property creator: '" + name + "' is already set.");
        }
        creators.put(name, creator);
    }

    Element<Object> create(String name, Object value) {
        @SuppressWarnings("unchecked")
        ElementCreator<Object> creator =
                (ElementCreator<Object>) this.creators.getOrDefault(name, ELEMENT_CREATOR);
        return creator.create(value);
    }

    static ElementFactory def() {
        return DEFAULT_ELEMENT_FACTORY;
    }

}
