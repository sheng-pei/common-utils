package ppl.common.utils.test.point;

import ppl.common.utils.reflect.PackageLoader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Points {

    private static final Points DEFAULT_POINTS;

    public static Points def() {
        return DEFAULT_POINTS;
    }

    private static final String DEFAULT_SPECIFIC_PACKAGE_NAME = Points.class.getPackage().getName() + ".specific";
    private static final Map<String, Class<? extends Point>> SYSTEM_POINT_CLASSES;

    static {
        Map<String, Class<? extends Point>> classes = new HashMap<>();
        PackageLoader loader = new PackageLoader(DEFAULT_SPECIFIC_PACKAGE_NAME, Points.class.getClassLoader());
        loader.load(Point.class, true)
                .forEach(c -> addPointClass(classes, c));
        SYSTEM_POINT_CLASSES = Collections.unmodifiableMap(classes);
        DEFAULT_POINTS = new Points();
    }

    private static void addPointClass(Map<String, Class<? extends Point>> classes, Class<? extends Point> pointClass) {
        PointType pt = pointClass.getAnnotation(PointType.class);
        if (pt == null || pt.value() == null || pt.value().isEmpty()) {
            throw new IllegalArgumentException("Point type is required.");
        }

        if (classes.containsKey(pt.value())) {
            throw new IllegalArgumentException("Point type already exists.");
        }

        classes.put(pt.value(), pointClass);
    }

    private final Map<String, Class<? extends Point>> specificClasses;

    public Points() {
        this.specificClasses = new HashMap<>(SYSTEM_POINT_CLASSES);
    }

    public void addPointClass(Class<? extends Point> pointClass) {
        Objects.requireNonNull(pointClass);
        addPointClass(specificClasses, pointClass);
    }

    public Class<? extends Point> getPointClass(String type) {
        return specificClasses.get(type);
    }
}
