package ppl.common.utils.command;

import ppl.common.utils.argument.argument.Argument;
import ppl.common.utils.argument.parser.Fragment;
import ppl.common.utils.argument.parser.StringArrayParser;
import ppl.common.utils.string.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CommandParser implements StringArrayParser<Object, String> {

    private static final Pattern LONG_OPTION_PATTERN = Pattern.compile("^[a-zA-Z][0-9a-zA-Z]*$");
    private static final Pattern SHORT_OPTION_PATTERN = Pattern.compile("^[a-zA-Z].*$");

    private static boolean isLongOption(String string) {
        return string.startsWith(BaseOption.LONG_OPTION_PREFIX) &&
                LONG_OPTION_PATTERN
                        .matcher(string.substring(BaseOption.LONG_OPTION_PREFIX.length()))
                        .matches();
    }

    private static boolean isShortOption(String string) {
        return string.startsWith(BaseOption.SHORT_OPTION_PREFIX) &&
                SHORT_OPTION_PATTERN
                        .matcher(string.substring(BaseOption.SHORT_OPTION_PREFIX.length()))
                        .matches();
    }

    private final CommandArguments arguments;

    public CommandParser(CommandArguments arguments) {
        this.arguments = arguments;
    }

    private String unfinishedArgument = null;

    @Override
    public Stream<Fragment<Object, String>> parse(String[] args) {
        List<Fragment<?, String>> res = new ArrayList<>();
        PositionFragment.Creator creator = PositionFragment.creator();

        int idx = 0;
        while (idx < args.length) {
            String arg = args[idx++];
            boolean used = processUnfinishedArgument(res, BaseOption.optionStart(arg) ? null : arg);
            if (BaseOption.optionStart(arg)) {
                try {
                    if (isShortOption(arg)) {
                        parseShortOption(res, arg);
                    } else if (isLongOption(arg)) {
                        parseLongOption(res, arg);
                    } else if (BaseOption.isEndOptionFlag(arg)) {
                        break;
                    } else {
                        throw new CommandLineException("Invalid option: " + arg);
                    }
                } catch (IllegalArgumentException e) {
                    throw new CommandLineException("Invalid command argument fragment: " + arg, e);
                }
            } else if (!used) {
                res.add(creator.create(arg));
            }
        }

        processUnfinishedArgument(res);
        for (; idx < args.length; idx++) {
            res.add(creator.create(args[idx]));
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        Stream<Fragment<Object, String>> r = (Stream) res.stream();
        return r;
    }

    private void processUnfinishedArgument(List<Fragment<?, String>> fragments) {
        processUnfinishedArgument(fragments, null);
    }

    private boolean processUnfinishedArgument(
            List<Fragment<?, String>> fragments, String value) {
        String unfinishedArgument = this.unfinishedArgument;
        if (unfinishedArgument != null) {
            fragments.add(new OptionFragment(unfinishedArgument, value));
            this.unfinishedArgument = null;
            return value != null;
        }
        return false;
    }

    private void parseShortOption(List<Fragment<?, String>> fragments, String argument) {
        int sLen = BaseOption.SHORT_OPTION_PREFIX.length();
        String option = sOption(argument.charAt(sLen));
        String remain = argument.substring(sLen + 1);

        try {
            Argument<String, ?> op = option(option);
            if (op instanceof ToggleOptionArgument) {
                fragments.add(new OptionFragment(option, null));
                parseBriefToggleShortOptions(fragments, remain);
            } else {
                parseNonToggleShortOption(fragments, option, remain);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid short option argument: " + argument + ".", e);
        }
    }

    private void parseBriefToggleShortOptions(List<Fragment<?, String>> fragments, String toggleShortOptions) {
        char[] chars = toggleShortOptions.toCharArray();
        for (char c : chars) {
            String option = sOption(c);
            Argument<String, ?> op;
            try {
                op = option(option);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid brief toggle short options", e);
            }

            if (op instanceof ToggleOptionArgument) {
                fragments.add(new OptionFragment(option, null));
            } else {
                throw new IllegalArgumentException("Invalid brief toggle short options. " +
                        "Because some non-toggle short option are mixed in.");
            }
        }
    }

    private void parseNonToggleShortOption(List<Fragment<?, String>> fragments, String option, String remain) {
        if (!remain.isEmpty()) {
            fragments.add(new OptionFragment(option, remain));
        } else {
            unfinishedArgument = option;
        }
    }

    private void parseLongOption(List<Fragment<?, String>> fragments, String option) {
        Argument<String, ?> op = option(option);
        if (op instanceof ToggleOptionArgument) {
            fragments.add(new OptionFragment(option, null));
        } else {
            unfinishedArgument = option;
        }
    }

    private String sOption(char c) {
        return BaseOption.SHORT_OPTION_PREFIX + c;
    }

    private Argument<String, Object> option(String option) {
        Argument<String, Object> op = arguments.get(option);
        if (op == null) {
            throw new IllegalArgumentException(Strings.format(
                    "Unknown option '{}'.", option));
        }
        return op;
    }

    private static class OptionFragment extends Fragment<String, String> {
        public OptionFragment(String key, String value) {
            super(key, value);
        }

        @Override
        protected String join(String key, String value) {
            return key + (value == null ? "" : Command.SEPARATOR + value);
        }
    }

    private static class PositionFragment extends Fragment<Integer, String> {
        public PositionFragment(Integer key, String value) {
            super(key, value);
        }

        @Override
        protected String join(Integer key, String value) {
            return value;
        }

        private static class Creator {
            private int pos = 0;

            public PositionFragment create(String value) {
                return new PositionFragment(pos++, value);
            }
        }

        public static Creator creator() {
            return new Creator();
        }
    }
}
