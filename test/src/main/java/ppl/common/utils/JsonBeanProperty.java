package ppl.common.utils;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonBeanProperty {
    String name();

    boolean ignore() default false;

    boolean flatted() default false;

    String rename() default "";

    class Value {
        private final String name;
        private final boolean ignore;
        private final boolean flatted;
        private final String rename;

        public Value(JsonBeanProperty property) {
            this(property.name(), property.ignore(), property.flatted(), property.rename());
        }

        public Value(String name, boolean ignore, boolean flatted, String rename) {
            this.name = name;
            this.ignore = ignore;
            this.flatted = flatted;
            this.rename = rename;
        }

        public String getName() {
            return name;
        }

        public boolean isIgnore() {
            return ignore;
        }

        public boolean isFlatted() {
            return flatted;
        }

        public String getRename() {
            return rename;
        }
    }
}
