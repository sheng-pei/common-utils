package ppl.common.utils.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PositionedTargets {

    private final List<Object> targets;
    private int pos = 0;

    public PositionedTargets(Object... targets) {
        this(Arrays.asList(targets));
    }

    public PositionedTargets(List<Object> targets) {
        if (targets == null) {
            targets = new ArrayList<>();
        }
        this.targets = new ArrayList<>(targets);
    }

    public Object consume() {
        Object res = get();
        this.pos++;
        return res;
    }

    public Object get() {
        if (this.pos >= this.targets.size()) {
            throw new IllegalStateException("Beyond targets list.");
        }
        return this.targets.get(this.pos);
    }

}
