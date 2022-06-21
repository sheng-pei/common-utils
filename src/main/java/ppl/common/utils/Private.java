package ppl.common.utils;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Documented
@Inherited
public @interface Private {
    String value() default "";
}
