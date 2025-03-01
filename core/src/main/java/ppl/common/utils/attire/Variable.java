package ppl.common.utils.attire;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface Variable {
    String value() default "";
    boolean ignoreInBody() default true;
}
