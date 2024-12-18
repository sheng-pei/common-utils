package ppl.common.utils;

import ppl.common.utils.ext.ExtPosition;
import ppl.common.utils.ext.Exts;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileName {
    public static final Pattern P_EXACT_SECRET_FLAG = Pattern.compile("[(（](公开|内部|秘密|机密)[）)]$");

    private final String base;
    private final String secret;
    private final String ext;
    private final ExtPosition position;

    private FileName(String base, String secret, String ext, ExtPosition position) {
        this.base = base;
        this.secret = secret;
        this.ext = ext;
        this.position = position;
    }

    public String getBase() {
        return base;
    }

    public String getSecret() {
        return secret;
    }

    public String getExt() {
        return ext;
    }

    public ExtPosition getPosition() {
        return position;
    }

    @Override
    public String toString() {
        if (position == ExtPosition.LEFT) {
            return String.format("%s%s(%s)", ext, base, secret);
        } else {
            return String.format("%s(%s)%s", base, secret, ext);
        }
    }

    public String toString(int num) {
        if (position == ExtPosition.LEFT) {
            return String.format("%s%s(%d)(%s)", ext.endsWith(".") ? ext : ext + ".", base, num, secret);
        } else {
            return String.format("%s(%d)(%s)%s", base, num, secret, ext.startsWith(".") ? ext : "." + ext);
        }
    }

    public static FileName create(Exts exts, String name) {
        return create(exts.parseExt(name));
    }

    public static FileName create(Exts.ParsedName parsedName) {
        String base = parsedName.getBase();
        ExtPosition position = parsedName.hasExt() ? parsedName.getPosition() : ExtPosition.RIGHT;
        String ext = parsedName.hasExt() ? parsedName.getExt().getExt() : "";
        String baseOffSecret = base;
        String secret = null;

        if (!parsedName.hasExt()) {
            int idx = base.length();
            while (true) {
                String n = base.substring(0, idx);
                Matcher m = FileName.P_EXACT_SECRET_FLAG.matcher(n.trim());
                if (m.find()) {
                    secret = m.group(1);
                    ext = base.substring(idx);
                    baseOffSecret = m.replaceAll("");
                    break;
                }

                int newIdx = base.lastIndexOf('.', idx != base.length() ? idx - 1 : idx);
                if (newIdx >= 0) {
                    idx = newIdx;
                } else {
                    break;
                }
            }
        } else {
            Matcher m = P_EXACT_SECRET_FLAG.matcher(base.trim());
            if (m.find()) {
                secret = m.group(1);
                baseOffSecret = m.replaceAll("");
            }
        }

        if (secret == null) {
            throw new IllegalArgumentException("No secret flag.");
        }

        return new FileName(baseOffSecret, secret, ext, position);
    }
}
