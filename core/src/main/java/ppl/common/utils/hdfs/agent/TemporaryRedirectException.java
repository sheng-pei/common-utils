package ppl.common.utils.hdfs.agent;

import ppl.common.utils.hdfs.HdfsException;

public class TemporaryRedirectException extends HdfsException {
    private final String location;
    public TemporaryRedirectException(String location) {
        super("Location: " + location);
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
