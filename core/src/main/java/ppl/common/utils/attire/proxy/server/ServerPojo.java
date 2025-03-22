package ppl.common.utils.attire.proxy.server;

import ppl.common.utils.string.Strings;

import java.util.Objects;

public final class ServerPojo {
    private final String name;
    private final String version;

    public ServerPojo(Server server) {
        this.version = server.version();
        String name = server.name();
        if (name.isEmpty()) {
            name = server.value();
        } else if (!server.value().isEmpty()) {
            throw new IllegalArgumentException("Both name and value is specified. This is not allowed.");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        ServerPojo that = (ServerPojo) object;
        return Objects.equals(name, that.name) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version);
    }

    @Override
    public String toString() {
        return Strings.format("Name: {}, Version: {}", name, version);
    }
}
