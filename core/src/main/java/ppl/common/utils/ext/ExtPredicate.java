package ppl.common.utils.ext;

import java.util.function.Predicate;

public interface ExtPredicate extends Predicate<String> {
    ExtPredicate ALWAYS_TRUE = s -> true;
}