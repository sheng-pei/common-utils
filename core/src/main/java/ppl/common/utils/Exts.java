package ppl.common.utils;

import java.util.*;

public class Exts {
    private final Set<String> exts;
    private final int max;

    private Exts(Collection<String> exts) {
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

    public static Exts.Builder builder() {
        return new Builder();
    }

    public Ext getExt(String string) {
        int periodIdx = string.lastIndexOf('.');
        if (periodIdx == -1) {
            return null;
        }
        String first = string.substring(periodIdx + 1);
        if (max == 0) {
            return new Ext(exts.contains(first), first);
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

    public static class Builder {
        private final Set<String> exts = new HashSet<>();

        public Builder add(String ext) {
            this.exts.add(ext);
            return this;
        }

        public Exts build() {
            return new Exts(exts);
        }
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
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Ext ext1 = (Ext) o;
            return known == ext1.known && Objects.equals(ext, ext1.ext);
        }

        @Override
        public int hashCode() {
            return Objects.hash(known, ext);
        }

        @Override
        public String toString() {
            return (isKnown() ? "known" : "unknown") +
                    " extension name: '" + getExt() + "'.";
        }
    }
}
