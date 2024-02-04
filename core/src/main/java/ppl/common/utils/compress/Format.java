package ppl.common.utils.compress;

import ppl.common.utils.argument.value.collector.ExCollectors;
import ppl.common.utils.enumerate.UnknownEnumException;

import java.util.Arrays;

public enum Format {
    ZIP("zip", true),
    TAR("tar", true),
    RAR("rar"),
    SEVEN_ZIP("7z");

    private final String name;
    private final boolean streamSupport;

    Format(String name) {
        this(name, false);
    }

    Format(String name, boolean streamSupport) {
        this.name = name;
        this.streamSupport = streamSupport;
    }

    public static Format enumOf(String name) {
        Format format = Arrays.stream(Format.values())
                .filter(f -> f.name.equalsIgnoreCase(name))
                .collect(ExCollectors.one());
        if (format == null) {
            throw new UnknownEnumException(Format.class, name);
        }
        return format;
    }

    public boolean isStreamSupport() {
        return streamSupport;
    }
}
