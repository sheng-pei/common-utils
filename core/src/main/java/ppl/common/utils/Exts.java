package ppl.common.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Exts {
    private final Set<String> exts;
    private final int max;

    public Exts(Collection<String> exts) {
        Set<String> tmp = new HashSet<>(exts);
        int max = 0;
        for (String ext : tmp) {
            int cnt = countPeriod(ext);
            if (max < cnt) {
                max = cnt;
            }
        }
        this.exts = Collections.unmodifiableSet(tmp);
        this.max = max;
    }

    private int countPeriod(String string) {
        int cnt = 0;
        char[] chars = string.toCharArray();
        for (char t : chars) {
            if (t == '.') {
                cnt++;
            }
        }
        return cnt;
    }

    public Ext getExt(String string) {
        int periodIdx = string.lastIndexOf('.');
        if (periodIdx == -1) {
            return null;
        }
        String first = string.substring(periodIdx + 1);
        if (max == 0) {
            return new Ext(false, first);
        }

        int idx = 0;
        String ret = exts.contains(first) ? first : null;
        while (idx < max) {
            String remain = string.substring(0, periodIdx);
            periodIdx = remain.lastIndexOf('.');
            if (periodIdx == -1) {
                return ret == null ? new Ext(false, first) : new Ext(true, ret);
            }
            String ext = string.substring(periodIdx + 1);
            ret = exts.contains(ext) ? ext : ret;
            idx++;
        }
        return ret == null ? new Ext(false, first) : new Ext(true, ret);
    }

    public static class Ext {
        private final boolean known;
        private final String ext;

        public Ext(boolean known, String ext) {
            this.known = known;
            this.ext = ext;
        }

        public boolean isKnown() {
            return known;
        }

        public String getExt() {
            return ext;
        }

        @Override
        public String toString() {
            return (isKnown() ? "known" : "unknown") +
                    " extension name: '" + getExt() + "'.";
        }
    }
}
