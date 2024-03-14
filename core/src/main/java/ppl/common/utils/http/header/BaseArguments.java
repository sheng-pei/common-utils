package ppl.common.utils.http.header;

import ppl.common.utils.argument.argument.Arguments;
import ppl.common.utils.argument.argument.value.ValueArgument;
import ppl.common.utils.string.ascii.CaseIgnoreString;

import java.util.*;

public class BaseArguments implements Arguments<String, ValueArgument<Object>> {

    public static final BaseArguments EMPTY = new BaseArguments(Collections.emptyList());

    @SuppressWarnings("rawtypes")
    private final Map arguments;

    public BaseArguments(List<? extends ValueArgument<?>> arguments) {
        Map<Object, Object> m = new HashMap<>();
        for (ValueArgument<?> va : arguments) {
            CaseIgnoreString cis = CaseIgnoreString.create(va.name());
            if (m.containsKey(cis)) {
                throw new IllegalArgumentException("Duplicate argument: " + cis.toString() + ".");
            }
            m.put(cis, va);
        }
        this.arguments = Collections.unmodifiableMap(m);
    }

    @Override
    public List<ValueArgument<Object>> getArguments() {
        Map<CaseIgnoreString, ValueArgument<Object>> arguments = map();
        return arguments.isEmpty() ? Collections.emptyList() : new ArrayList<>(arguments.values());
    }

    @Override
    public ValueArgument<Object> getByKey(String s) {
        if (s == null) {
            return null;
        }
        return pGetByName(s);
    }

    @Override
    public ValueArgument<Object> getByName(String name) {
        if (name == null) {
            return null;
        }
        return pGetByName(name);
    }

    private ValueArgument<Object> pGetByName(String name) {
        return map().get(CaseIgnoreString.create(name));
    }

    private Map<CaseIgnoreString, ValueArgument<Object>> map() {
        @SuppressWarnings("unchecked")
        Map<CaseIgnoreString, ValueArgument<Object>> arguments = this.arguments;
        return arguments;
    }

}
