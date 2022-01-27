package ppl.common.utils.config;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class NodesTest {
    @Test
    public void test() {
        Node node = Nodes.root(new ArrayList<>());
        System.out.println(node.getClass().getName());
    }
}
