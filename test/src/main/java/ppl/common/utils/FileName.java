package ppl.common.utils;

import ppl.common.utils.ext.Ext;
import ppl.common.utils.ext.Exts;
import ppl.common.utils.ext.Name;
import ppl.common.utils.pair.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileName {
    private static final Pattern P_NUMBER_LABEL = Pattern.compile("[　\\s]*[（(]([0-9]+)[)）][　\\s]*$");
    private static final Pattern P_EXACT_SECRET_FLAG = Pattern.compile(
            "[(（][　\\s]*(公[　\\s]*开|内[　\\s]*部|秘[　\\s]*密|机[　\\s]*密)[　\\s]*(?:★[　\\s]*(?:[0-9]*[　\\s]*年)?)?[　\\s]*[）)][　\\s]*$");

    private final List<String> parts;
    private final int secretIndex;
    private final Integer seq;

    private FileName(List<String> parts, int secretIndex, Integer seq) {
        this.parts = Collections.unmodifiableList(new ArrayList<>(parts));
        this.secretIndex = secretIndex;
        this.seq = seq;
    }

    public String noSecret() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.size(); i++) {
            if (i != secretIndex) {
                builder.append(parts.get(i));
            } else if (seq != null) {
                builder.append("(").append(seq).append(")");
            }
        }
        return builder.toString();
    }

    public Integer seq() {
        return seq;
    }

    public String getSecret() {
        return parts.get(secretIndex);
    }

    public String getBase() {
        return parts.get(secretIndex - 1);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.size(); i++) {
            if (i != secretIndex) {
                builder.append(parts.get(i));
            } else {
                if (seq != null) {
                    builder.append("(").append(seq).append(")");
                }
                builder.append("(").append(parts.get(i)).append(")");
            }
        }
        return builder.toString();
    }

    public String toString(int num) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.size(); i++) {
            if (i != secretIndex) {
                builder.append(parts.get(i));
            } else {
                builder.append("(").append(num).append(")")
                        .append("(").append(parts.get(i)).append(")");
            }
        }
        return builder.toString();
    }

    public static FileName create(Exts exts, String name) {
        return create(name, exts.parseExt(name));
    }

    public static FileName create(String name, Ext ext) {
        Name nm = ext == null ? new Name(name) : ext.getName();

        Integer seq = null;
        if (ext == null) {
            String base = nm.getBase();
            int idx = base.length();
            while (true) {
                Matcher m = FileName.P_EXACT_SECRET_FLAG.matcher(base.substring(0, idx));
                if (m.find()) {
                    Pair<List<String>, Integer> pair = processSecret(m);
                    List<String> parts = new ArrayList<>(pair.getFirst());
                    parts.add(nm.getBase().substring(idx));
                    return new FileName(parts, 1, pair.getSecond());
                }

                int newIdx = base.lastIndexOf('.', idx != base.length() ? idx - 1 : idx);
                if (newIdx >= 0) {
                    idx = newIdx;
                } else {
                    break;
                }
            }
        } else {
            Matcher m = P_EXACT_SECRET_FLAG.matcher(nm.getBase());
            if (m.find()) {
                Pair<List<String>, Integer> pair = processSecret(m);
                Pair<List<String>, Integer> replaced = nm.baseReplaced(pair.getFirst());
                return new FileName(replaced.getFirst(), replaced.getSecond() + 1, pair.getSecond());
            }
        }

        throw new IllegalArgumentException("No secret flag.");
    }

    private static Pair<List<String>, Integer> processSecret(Matcher matcher) {
        Integer seq = null;
        String base = matcher.replaceFirst("");
        Matcher numberMatcher = P_NUMBER_LABEL.matcher(base);
        while (numberMatcher.find()) {
            base = numberMatcher.replaceFirst("");
            seq = Integer.parseInt(numberMatcher.group(1));
        }
        return Pair.create(Arrays.asList(base,
                matcher.group(1).replaceAll("[　\\s]+", "")), seq);
    }
}
