package ppl.common.utils.attire.proxy.server.param;

public class ParamPojo {

    public static final ParamPojo DEFAULT_PARAM = new ParamPojo();

    private final String name;

    private ParamPojo() {
        this.name = "";
    }

    private ParamPojo(ParamPojo pojo, String name) {
        this.name = name;
    }

    public ParamPojo(Param param) {
        this.name = param.value();
    }

    public ParamPojo changeNameIfAbsent(String name) {
        if (!this.name.isEmpty()) {
            return null;
        }
        return new ParamPojo(this, name);
    }

    public String name() {
        return name;
    }
}
