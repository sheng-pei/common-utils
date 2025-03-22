package ppl.common.utils.attire.proxy.server.param;

public class VariablePojo {
    private final String name;

    public VariablePojo(Variable variable) {
        this.name = variable.value();
    }

    public String name() {
        return name;
    }
}
