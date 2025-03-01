package ppl.common.utils.http.header;

import ppl.common.utils.argument.argument.Arguments;
import ppl.common.utils.argument.argument.value.ValuedArgument;
import ppl.common.utils.character.ascii.CaseIgnoreString;

import java.util.*;

public class BaseArguments implements Arguments<String, ValuedArgument<Object>> {

    public static final BaseArguments EMPTY = new BaseArguments(Collections.emptyList());

    @SuppressWarnings("rawtypes")
    private final Map arguments;

    public BaseArguments(List<? extends ValuedArgument<?>> arguments) {
        Map<Object, Object> m = new HashMap<>();
        for (ValuedArgument<?> va : arguments) {
            CaseIgnoreString cis = CaseIgnoreString.create(va.name());
            if (m.containsKey(cis)) {
                throw new IllegalArgumentException("Duplicate argument: " + cis.toString() + ".");
            }
            m.put(cis, va);
        }
        this.arguments = Collections.unmodifiableMap(m);
    }

    @Override
    public ValuedArgument<Object> getByKey(String s) {
        if (s == null) {
            return null;
        }
        return pGetByName(s);
    }

    @Override
    public ValuedArgument<Object> getByName(String name) {
        if (name == null) {
            return null;
        }
        return pGetByName(name);
    }

    private ValuedArgument<Object> pGetByName(String name) {
        return map().get(CaseIgnoreString.create(name));
    }

    private Map<CaseIgnoreString, ValuedArgument<Object>> map() {
        @SuppressWarnings("unchecked")
        Map<CaseIgnoreString, ValuedArgument<Object>> arguments = this.arguments;
        return arguments;
    }

}
