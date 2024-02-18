package ppl.common.utils.http.header;

import ppl.common.utils.argument.argument.Arguments;
import ppl.common.utils.argument.argument.value.ValueArgument;
import ppl.common.utils.string.ascii.CaseIgnoreString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BaseArguments implements Arguments<CaseIgnoreString, String, ValueArgument<CaseIgnoreString, Object>> {

    public static final BaseArguments EMPTY = new BaseArguments(Collections.emptyList());

    @SuppressWarnings("rawtypes")
    private final Map arguments;

    public BaseArguments(List<? extends ValueArgument<CaseIgnoreString, ?>> arguments) {
        this.arguments = Collections.unmodifiableMap(arguments.stream()
                .collect(Collectors.toMap(ValueArgument::getName, Function.identity())));
    }

    @Override
    public List<ValueArgument<CaseIgnoreString, Object>> getArguments() {
        Map<CaseIgnoreString, ValueArgument<CaseIgnoreString, Object>> arguments = map();
        return arguments.isEmpty() ? Collections.emptyList() : new ArrayList<>(arguments.values());
    }

    @Override
    public ValueArgument<CaseIgnoreString, Object> get(String s) {
        if (s == null) {
            return null;
        }
        return pGetByName(CaseIgnoreString.create(s));
    }

    @Override
    public ValueArgument<CaseIgnoreString, Object> getByName(CaseIgnoreString caseIgnoreString) {
        if (caseIgnoreString == null) {
            return null;
        }
        return pGetByName(caseIgnoreString);
    }

    private ValueArgument<CaseIgnoreString, Object> pGetByName(CaseIgnoreString caseIgnoreString) {
        return map().get(caseIgnoreString);
    }

    private Map<CaseIgnoreString, ValueArgument<CaseIgnoreString, Object>> map() {
        @SuppressWarnings("unchecked")
        Map<CaseIgnoreString, ValueArgument<CaseIgnoreString, Object>> arguments = this.arguments;
        return arguments;
    }

}
