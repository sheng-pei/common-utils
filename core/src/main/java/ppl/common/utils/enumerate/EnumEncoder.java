package ppl.common.utils.enumerate;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnumEncoder {
    boolean caseSensitive() default true; //TODO, implements
}
