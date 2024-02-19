package ppl.common.utils.filesystem.ftp;

import ppl.common.utils.filesystem.core.FileSystemProperties;
import ppl.common.utils.filesystem.path.Path;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FtpProperties implements FileSystemProperties {
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 21;
    private static final boolean DEFAULT_ACTIVE = false;
    private static final String DEFAULT_WORKING = Path.C_CURRENT_DIR;
    private static final boolean DEFAULT_AUTO_CREATE_WORKING = false;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean active;
    private final String working;
    private final boolean autoCreateWorking;
    private final Charset charset;

    private FtpProperties(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.username = builder.username;
        this.password = builder.password;
        this.active = builder.active;
        this.working = builder.working;
        this.autoCreateWorking = builder.autoCreateWorking;
        this.charset = builder.charset;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isActive() {
        return active;
    }

    public String getWorking() {
        return working;
    }

    public boolean isAutoCreateWorking() {
        return autoCreateWorking;
    }

    public Charset getCharset() {
        return charset;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String host;
        private int port;
        private String username;
        private String password;
        private boolean active;
        private String working;
        private boolean autoCreateWorking;
        private Charset charset;

        public Builder withHost(String host) {
            host = (host == null ? "" : host.trim());
            if (host.isEmpty()) {
                host = DEFAULT_HOST;
            }
            this.host = host;
            return this;
        }

        public Builder withPort(Integer port) {
            port = (port == null ? DEFAULT_PORT : port);
            if (port < 0 || port >65535) {
                throw new IllegalArgumentException("Invalid tcp port: '" + port + "'.");
            }
            this.port = port;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = (username == null ? "" : username.trim());
            return this;
        }

        public Builder withPassword(String password) {
            this.password = (password == null ? "" : password);
            return this;
        }

        public Builder withActive(Boolean active) {
            this.active = (active == null ? DEFAULT_ACTIVE : active);
            return this;
        }

        public Builder withWorking(String working) {
            working = (working == null ? "" : working);
            if (working.isEmpty()) {
                working = DEFAULT_WORKING;
            }
            this.working = working;
            return this;
        }

        public Builder withAutoCreateWorking(Boolean autoCreateWorking) {
            this.autoCreateWorking = (autoCreateWorking == null ? DEFAULT_AUTO_CREATE_WORKING : autoCreateWorking);
            return this;
        }

        public Builder withCharset(String charset) {
            charset = (charset == null ? "" : charset.trim());
            this.charset = charset.isEmpty() ? DEFAULT_CHARSET : Charset.forName(charset);
            return this;
        }

        public Builder withCharset(Charset charset) {
            this.charset = (charset == null ? DEFAULT_CHARSET : charset);
            return this;
        }

        public FtpProperties build() {
            return new FtpProperties(this);
        }
    }
}
