package ppl.common.utils.command;

import ppl.common.utils.argument.Fragment;
import ppl.common.utils.argument.parser.StringArrayParser;
import ppl.common.utils.string.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CommandParser implements StringArrayParser<Object, String> {

    private final CommandArguments arguments;

    public CommandParser(CommandArguments arguments) {
        this.arguments = arguments;
    }

    @Override
    public Stream<Fragment<Object, String>> parse(String[] args) {
        List<Fragment<?, String>> res = new ArrayList<>();
        int pos = 0;
        boolean isEnd = false;
        String needValue = null;
        for (String arg : args) {
            if (isEnd) {
                res.add(new PositionFragment(pos++, arg));
            } else if (isShortOption(arg)) {
                eatValue(res, needValue, null);
                needValue = null;
                if (parseShortOption(res, arg.substring(Option.SHORT_OPTION_PREFIX.length()))) {
                    needValue = arg;
                }
            } else if (isLongOption(arg)) {
                eatValue(res, needValue, null);
                needValue = null;
                if (parseLongOption(res, arg.substring(Option.LONG_OPTION_PREFIX.length()))) {
                    needValue = arg;
                }
            } else if (isEndOptionFlag(arg)) {
                eatValue(res, needValue, null);
                needValue = null;
                isEnd = true;
            } else {
                if (!eatValue(res, needValue, arg)) {
                    res.add(new PositionFragment(pos++, arg));
                }
                needValue = null;
            }
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        Stream<Fragment<Object, String>> r = (Stream) res.stream();
        return r;
    }

    private boolean isShortOption(String arg) {
        return arg.length() >= 2 &&
                arg.startsWith(Option.SHORT_OPTION_PREFIX) &&
                !arg.startsWith(Option.LONG_OPTION_PREFIX);
    }

    private boolean isLongOption(String arg) {
        return arg.length() >= 3 &&
                arg.startsWith(Option.LONG_OPTION_PREFIX);
    }

    private boolean isEndOptionFlag(String arg) {
        return arg.equals(Option.END_OPTION_FLAG);
    }

    private boolean parseShortOption(List<Fragment<?, String>> fragments, String argument) {
        String option = Option.SHORT_OPTION_PREFIX + argument.charAt(0);
        Option<?> op = option(option);
        if (op.isToggle()) {
            fragments.add(new OptionFragment(option, null));
            parseToggleShortOptions(fragments, argument);
            return false;
        } else {
            return parseNonToggleShortOption(fragments, argument);
        }
    }

    private void parseToggleShortOptions(List<Fragment<?, String>> fragments, String argument) {
        for (int j = 1; j < argument.length(); j++) {
            fragments.add(new OptionFragment(Option.SHORT_OPTION_PREFIX + argument.charAt(j), null));
        }
    }

    private boolean parseNonToggleShortOption(List<Fragment<?, String>> fragments, String argument) {
        if (argument.length() == 1) {
            return true;
        } else {
            fragments.add(new OptionFragment(Option.SHORT_OPTION_PREFIX + argument.charAt(0), argument.substring(1)));
            return false;
        }
    }

    private boolean parseLongOption(List<Fragment<?, String>> fragments, String name) {
        String option = Option.LONG_OPTION_PREFIX + name;
        Option<?> op = option(option);
        if (op.isToggle()) {
            fragments.add(new OptionFragment(option, null));
            return false;
        }
        return true;
    }

    private boolean eatValue(List<Fragment<?, String>> fragments, String needValue, String argument) {
        if (needValue != null) {
            fragments.add(new OptionFragment(needValue, argument));
            return true;
        }
        return false;
    }

    private Option<?> option(String option) {
        Option<?> op = (Option<?>) arguments.get(option);
        if (op == null) {
            throw new CommandLineException(Strings.format(
                    "Unknown short option '{}'.", option));
        }
        return op;
    }

    private static class OptionFragment extends Fragment<String, String> {
        public OptionFragment(String key, String value) {
            super(key, value);
        }

        @Override
        protected String merge(String key, String value) {
            return key + (value == null ? "" : Command.SEPARATOR + value);
        }
    }

    private static class PositionFragment extends Fragment<Integer, String> {
        public PositionFragment(Integer key, String value) {
            super(key, value);
        }

        @Override
        protected String merge(Integer key, String value) {
            return value;
        }
    }
}
