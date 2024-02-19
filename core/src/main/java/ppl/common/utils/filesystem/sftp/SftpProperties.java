package ppl.common.utils.filesystem.sftp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.filesystem.core.FileSystemProperties;
import ppl.common.utils.filesystem.ftp.FtpProperties;
import ppl.common.utils.filesystem.path.Path;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SftpProperties implements FileSystemProperties {

    private static final Logger log = LoggerFactory.getLogger(SftpProperties.class);

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 22;
    private static final String DEFAULT_WORKING = Path.C_CURRENT_DIR;
    private static final boolean DEFAULT_AUTO_CREATE_WORKING = false;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final boolean DEFAULT_CHANNEL_FIRST = true;
    private static final int DEFAULT_CORE_SESSION = 1;
    private static final int DEFAULT_MAX_CHANNEL = 5;

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String working;
    private final boolean autoCreateWorking;
    private final Charset charset;
    private final boolean channelFirst;
    private final int coreSession;
    private final int maxChannel;

    private SftpProperties(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.username = builder.username;
        this.password = builder.password;
        this.working = builder.working;
        this.autoCreateWorking = builder.autoCreateWorking;
        this.charset = builder.charset;
        this.channelFirst = builder.channelFirst;
        this.coreSession = builder.coreSession;
        this.maxChannel = builder.maxChannel;
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

    public String getWorking() {
        return working;
    }

    public boolean isAutoCreateWorking() {
        return autoCreateWorking;
    }

    public Charset getCharset() {
        return charset;
    }

    public boolean isChannelFirst() {
        return channelFirst;
    }

    public int getCoreSession() {
        return coreSession;
    }

    public int getMaxChannel() {
        return maxChannel;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String host;
        private int port;
        private String username;
        private String password;
        private String working;
        private boolean autoCreateWorking;
        private Charset charset;
        private boolean channelFirst;
        private int coreSession;
        private int maxChannel;

        public SftpProperties.Builder withHost(String host) {
            host = (host == null ? "" : host.trim());
            if (host.isEmpty()) {
                host = DEFAULT_HOST;
            }
            this.host = host;
            return this;
        }

        public SftpProperties.Builder withPort(Integer port) {
            port = (port == null ? DEFAULT_PORT : port);
            if (port < 0 || port >65535) {
                throw new IllegalArgumentException("Invalid tcp port: '" + port + "'.");
            }
            this.port = port;
            return this;
        }

        public SftpProperties.Builder withUsername(String username) {
            this.username = (username == null ? "" : username.trim());
            return this;
        }

        public SftpProperties.Builder withPassword(String password) {
            this.password = (password == null ? "" : password);
            return this;
        }

        public SftpProperties.Builder withWorking(String working) {
            working = (working == null ? "" : working);
            if (working.isEmpty()) {
                working = DEFAULT_WORKING;
            }
            this.working = working;
            return this;
        }

        public SftpProperties.Builder withAutoCreateWorking(Boolean autoCreateWorking) {
            this.autoCreateWorking = (autoCreateWorking == null ? DEFAULT_AUTO_CREATE_WORKING : autoCreateWorking);
            return this;
        }

        public SftpProperties.Builder withCharset(String charset) {
            charset = (charset == null ? "" : charset.trim());
            this.charset = charset.isEmpty() ? DEFAULT_CHARSET : Charset.forName(charset);
            return this;
        }

        public SftpProperties.Builder withCharset(Charset charset) {
            this.charset = (charset == null ? DEFAULT_CHARSET : charset);
            return this;
        }

        public SftpProperties.Builder withChannelFirst(Boolean channelFirst) {
            this.channelFirst = (channelFirst == null ? DEFAULT_CHANNEL_FIRST : channelFirst);
            return this;
        }

        public SftpProperties.Builder withCoreSession(Integer coreSession) {
            coreSession = (coreSession == null ? DEFAULT_CORE_SESSION : coreSession);
            if (coreSession < 1) {
                throw new IllegalArgumentException("The amount of core sessions must be positive.");
            }
            this.coreSession = coreSession;
            return this;
        }

        public SftpProperties.Builder withMaxChannel(Integer maxChannel) {
            maxChannel = (maxChannel == null ? DEFAULT_MAX_CHANNEL : maxChannel);
            if (maxChannel < 1) {
                throw new IllegalArgumentException("The max amount of channels per session must be positive.");
            }
            this.maxChannel = maxChannel;
            return this;
        }

        public SftpProperties build() {
            if (channelFirst && coreSession > 1) {
                log.warn("The amount of core sessions is useless if channel first. Set to one.");
                coreSession = 1;
            }
            if (!channelFirst && coreSession == 1) {
                log.warn("The mode (session first and core session is one) is the same as channel first.");
                channelFirst = true;
            }
            return new SftpProperties(this);
        }
    }
}
