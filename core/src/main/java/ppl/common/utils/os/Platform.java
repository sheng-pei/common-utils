package ppl.common.utils.os;

public enum Platform {
    LINUX("Linux"),
    WINDOWS("Windows"),
    MAC_OS("Mac OS"),
    MAC_OS_X("Mac OS X"),
    OTHERS("others");

    private final String name;

    Platform(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
