package ppl.common.utils.string;

import java.util.Optional;

public final class Quote {
    private Quote() {}

    public static String dbLike(String string) {
        return Optional.ofNullable(string)
                .map(s -> s.replaceAll("\\\\", "\\\\\\\\\\\\\\\\"))
                .map(s -> s.replaceAll("_", "\\\\_"))
                .map(s -> s.replaceAll("%", "\\\\%"))
                .orElse(null);
    }
}
