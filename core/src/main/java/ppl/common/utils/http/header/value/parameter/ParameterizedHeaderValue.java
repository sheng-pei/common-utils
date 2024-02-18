package ppl.common.utils.http.header.value.parameter;

import ppl.common.utils.argument.analyzer.Analyzer;
import ppl.common.utils.argument.argument.Arguments;
import ppl.common.utils.argument.argument.value.ArgumentValue;
import ppl.common.utils.argument.argument.value.ValueArgument;
import ppl.common.utils.http.header.Context;
import ppl.common.utils.http.header.SingleLineHeaderValue;
import ppl.common.utils.http.symbol.HttpCharGroup;
import ppl.common.utils.string.Strings;
import ppl.common.utils.string.ascii.CaseIgnoreString;
import ppl.common.utils.pair.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class ParameterizedHeaderValue<
        T extends Arguments<CaseIgnoreString, String, ValueArgument<CaseIgnoreString, Object>>,
        P extends ParameterizedHeaderValue<T, P>> extends SingleLineHeaderValue {

    static final char DELIMITER = ';';
    public static final char SEPARATOR = '=';

    private final T target;
    private final Map<CaseIgnoreString, ArgumentValue<CaseIgnoreString, Object>> parameters;
    private final String unknownParameters;

    protected ParameterizedHeaderValue(String value,
                                       Function<String, T> targetCreator,
                                       Context context) {
        super(value);
        Pair<String, String> pair = cutTargetFrom(value);
        T target = createTarget(pair.getFirst(), targetCreator);
        Analyzer<CaseIgnoreString, String> analyzer = new Analyzer<>(target);
        ParameterParser parser = parser(context);
        List<Object> res = analyzer.analyse(parser.parse(pair.getSecond()));

        Map<CaseIgnoreString, ArgumentValue<CaseIgnoreString, Object>> ps = new HashMap<>();
        StringBuilder ups = new StringBuilder();
        res.forEach(o -> {
            if (o instanceof ArgumentValue) {
                @SuppressWarnings("unchecked")
                ArgumentValue<CaseIgnoreString, Object> av =  (ArgumentValue<CaseIgnoreString, Object>) o;
                ps.put(av.key(), av);
            } else if (o instanceof ValueArgument) {
                @SuppressWarnings("unchecked")
                ValueArgument<CaseIgnoreString, Object> a = (ValueArgument<CaseIgnoreString, Object>) o;
                ps.put(a.getName(), ArgumentValue.create(a, null));
            } else {
                ups.append(o).append(DELIMITER).append(" ");
            }
        });
        if (ups.length() > 0) {
            ups.setLength(ups.length() - 2);
        }

        this.target = target;
        this.parameters = ps;
        this.unknownParameters = ups.toString();
    }

    private static
    <T extends Arguments<CaseIgnoreString, String, ValueArgument<CaseIgnoreString, Object>>>
    T createTarget(String target, Function<String, T> targetCreator) {
        required(target);
        return targetCreator.apply(target);
    }

    private static Pair<String, String> cutTargetFrom(String string) {
        char[] chars = string.toCharArray();
        int start = 0;
        String first;
        do {
            int end = endOfTarget(chars, start);
            first = first(chars, start, end);
            start = next(end, chars.length);
            if (!first.isEmpty()) {
                break;
            }
        } while (start < chars.length);
        return Pair.create(first, new String(chars, start, chars.length - start));
    }

    private static int endOfTarget(char[] chars, int start) {
        int idx = Strings.indexOf(DELIMITER, chars, start, chars.length);
        if (idx < 0) {
            idx = chars.length;
        }
        return idx;
    }

    private static String first(char[] chars, int start, int end) {
        String first = new String(chars, start, end - start);
        return Strings.trim(first, HttpCharGroup.WS);
    }

    private static int next(int end, int length) {
        return end == length ? length : end + 1;
    }

    private static void required(String string) {
        if (string.isEmpty()) {
            throw new IllegalArgumentException("Target is required.");
        }
    }

    private static ParameterParser parser(Context context) {
        if (context != null && context != Context.DEFAULT) {
            return context.getParameterParser();
        }
        return ParameterParser.DEFAULT;
    }

    public T getTarget() {
        return target;
    }

    public final P appendLexicalParameter(String name, String value) {
        ensureKnown(name);
        ParameterParser parser = ParameterParser.DEFAULT;
        List<Object> res = new Analyzer<>(target)
                .analyse(parser.parse(name, value));
        @SuppressWarnings("unchecked")
        ArgumentValue<CaseIgnoreString, Object> av = (ArgumentValue<CaseIgnoreString, Object>) res.get(0);
        CaseIgnoreString n = av.key();
        ArgumentValue<CaseIgnoreString, Object> exist = parameters.get(n);
        parameters.put(n, exist == null ? av : exist.merge(av));
        return self();
    }

    public final P removeParameter(String name) {
        ensureKnown(name);
        CaseIgnoreString k = CaseIgnoreString.create(name);
        parameters.remove(k);
        return self();
    }

    private P self() {
        @SuppressWarnings("unchecked")
        P p = (P) this;
        return p;
    }

    public final P setParameter(String name, Object value) {
        ensureKnown(name);
        CaseIgnoreString k = CaseIgnoreString.create(name);
        ArgumentValue<CaseIgnoreString, Object> av = ArgumentValue.create(target.getByName(k), value);
        parameters.remove(k);
        return appendLexicalParameter(name, av.toString());
    }

    public final Object getParameter(String name) {
        ensureKnown(name);
        ArgumentValue<CaseIgnoreString, Object> p = parameters.get(CaseIgnoreString.create(name));
        return p == null ? null : p.value();
    }

    public final boolean hasParameter(String name) {
        ensureKnown(name);
        return parameters.containsKey(CaseIgnoreString.create(name));
    }

    private void ensureKnown(String name) {
        if (target.get(name) == null) {
            throw new IllegalArgumentException(Strings.format(
                    "Unknown parameter: '{}' for: '{}'.", name, target));
        }
    }

    @Override
    public String toCanonicalString() {
        return toCanonicalString(parameters.values());
    }

    private String toCanonicalString(Collection<? extends ArgumentValue<CaseIgnoreString, ?>> parameters) {
        String p = concatParameters(parameters);
        return target.toString() + (p.isEmpty() ? "" : DELIMITER + " " + concatParameters(parameters));
    }

    private String concatParameters(Collection<? extends ArgumentValue<CaseIgnoreString, ?>> parameters) {
        StringBuilder pBuilder = new StringBuilder();
        appendParameters(parameters, pBuilder);
        if (!parameters.isEmpty() && unknownParameters.isEmpty()) {
            pBuilder.setLength(pBuilder.length() - 2);
        } else {
            pBuilder.append(unknownParameters);
        }
        return pBuilder.toString();
    }

    private void appendParameters(Collection<? extends ArgumentValue<CaseIgnoreString, ?>> parameters,
                                  StringBuilder builder) {
        parameters.forEach(av -> builder.append(av).append(DELIMITER).append(" "));
    }

}
