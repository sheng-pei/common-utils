package ppl.common.utils.config.nodes.scalar;

import ppl.common.utils.config.nodes.AbstractValueNode;

import java.util.Objects;

public final class ScalarNode extends AbstractValueNode {
    ScalarNode(String path, Object scalar) {
        super(path, Objects.requireNonNull(scalar));
    }
}
