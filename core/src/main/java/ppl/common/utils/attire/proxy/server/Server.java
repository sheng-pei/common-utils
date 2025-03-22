package ppl.common.utils.attire.proxy.server;

import ppl.common.utils.reflect.annotation.Alias;

import java.lang.annotation.*;

/**
 * 根据name，version进行自动装配，比如依据是否具有相应的配置信息进行。
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Server {
    @Alias("name")
    String value() default "";
    @Alias("value")
    String name() default "";
    String version() default "";
}
