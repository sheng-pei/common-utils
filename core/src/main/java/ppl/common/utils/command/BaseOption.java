package ppl.common.utils.command;

import ppl.common.utils.string.Strings;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BaseOption implements Option {
    private static final String START_OPTION_FLAG = "-";
    static final String END_OPTION_FLAG = "--";
    static final String LONG_OPTION_PREFIX = "--";
    static final String SHORT_OPTION_PREFIX = "-";
    private static final Pattern LONG_OPTION_PATTERN = Pattern.compile("^[a-zA-Z][0-9a-zA-Z]*$");
    private static final Pattern SHORT_OPTION_PATTERN = Pattern.compile("^[a-zA-Z]$");

    static boolean isLongOption(String string) {
        return string.startsWith(BaseOption.LONG_OPTION_PREFIX) &&
                LONG_OPTION_PATTERN
                        .matcher(string.substring(BaseOption.LONG_OPTION_PREFIX.length()))
                        .matches();
    }

    static boolean isShortOption(String string) {
        return string.startsWith(BaseOption.SHORT_OPTION_PREFIX) &&
                SHORT_OPTION_PATTERN
                        .matcher(string.substring(BaseOption.SHORT_OPTION_PREFIX.length()))
                        .matches();
    }

    static String name(String longOption, Character shortOption) {
        String name = longOption;
        if (name == null || name.isEmpty()) {
            name = shortOption == null ? "" : shortOption.toString();
        }
        return name;
    }

    static boolean optionStart(String string) {
        return string.startsWith(START_OPTION_FLAG);
    }

    static boolean isEndOptionFlag(String arg) {
        return arg.equals(BaseOption.END_OPTION_FLAG);
    }

    static Builder newBuilder() {
        return new Builder();
    }

    private final List<String> longOptions;
    private final List<String> shortOptions;

    private BaseOption(List<String> longOptions, List<Character> shortOptions) {
        this.longOptions = Collections.unmodifiableList(longOptions.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(BaseOption::checkLongOption)
                .map(l -> LONG_OPTION_PREFIX + l)
                .collect(Collectors.toList()));
        this.shortOptions = Collections.unmodifiableList(shortOptions.stream()
                .filter(Objects::nonNull)
                .map(BaseOption::checkShortOption)
                .map(c -> SHORT_OPTION_PREFIX + c)
                .collect(Collectors.toList()));
        if (this.longOptions.isEmpty() && this.shortOptions.isEmpty()) {
            throw new IllegalArgumentException("Long options or short options must not be empty.");
        }
    }

    private static String checkLongOption(String longOption) {
        if (!LONG_OPTION_PATTERN.matcher(longOption).matches()) {
            throw new IllegalArgumentException("Invalid long option: " + longOption);
        }
        return longOption;
    }

    private static Character checkShortOption(Character shortOption) {
        if (!SHORT_OPTION_PATTERN.matcher(shortOption.toString()).matches()) {
            throw new IllegalArgumentException("Invalid short option: " + shortOption);
        }
        return shortOption;
    }

    @Override
    public List<String> getLongOptions() {
        return this.longOptions;
    }

    @Override
    public List<String> getShortOptions() {
        return this.shortOptions;
    }

    @Override
    public String toString() {
        return Strings.format(
                "short option->{}, long option->{}",
                this.shortOptions.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining("|")),
                String.join("|", this.longOptions));
    }

    public static class Builder {
        private List<String> longOptions;
        private List<Character> shortOptions;

        private Builder() {}

        BaseOption.Builder withLongOptions(List<String> longOptions) {
            this.longOptions = longOptions.stream()
                    .distinct().collect(Collectors.toList());
            return this;
        }

        BaseOption.Builder withShortOptions(List<Character> shortOptions) {
            this.shortOptions = shortOptions.stream()
                    .distinct().collect(Collectors.toList());
            return this;
        }

        BaseOption build() {
            return new BaseOption(longOptions, shortOptions);
        }
    }
}
