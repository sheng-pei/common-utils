package ppl.common.utils.enumerate;

import ppl.common.utils.StringUtils;

import java.lang.annotation.*;
import java.math.BigInteger;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnumEncoder {
}
