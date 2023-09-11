package ppl.common.utils.hdfs.agent;

public class TemporaryRedirectException extends RuntimeException {
    private final String location;
    public TemporaryRedirectException(String location) {
        super("Location: " + location);
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
