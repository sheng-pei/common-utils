package ppl.common.utils.asserts;

import ppl.common.utils.net.URLDecoder;
import ppl.common.utils.string.Strings;

import java.util.regex.Pattern;

public final class Hostnames {
    private Hostnames() {}

    private static final Pattern DOMAIN_LABEL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9]");
    private static final Pattern TOP_LABEL_PATTERN = Pattern.compile(
            "[a-zA-Z]|[a-zA-Z][a-zA-Z0-9-]*[a-zA-Z0-9]");

    public static boolean isHostname(String hostname) {
        if (Strings.isEmpty(hostname)) {
            return false;
        }

        if (hostname.startsWith(".")) {
            return false;
        }

        if (hostname.endsWith(".")) {
            hostname = hostname.substring(0, hostname.length() - 1);
        }
        hostname = URLDecoder.decode(hostname);
        String[] labels = hostname.split("\\.");
        for (int i = 0; i < labels.length - 1; i++) {
            if (!DOMAIN_LABEL_PATTERN.matcher(labels[i]).matches()) {
                return false;
            }
        }
        return TOP_LABEL_PATTERN.matcher(labels[labels.length - 1]).matches();
    }
}
