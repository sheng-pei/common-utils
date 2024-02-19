package ppl.common.utils.filesystem.obs;

import ppl.common.utils.filesystem.core.FileSystemProperties;
import ppl.common.utils.filesystem.path.Path;
import ppl.common.utils.filesystem.sftp.SftpProperties;

public class ObsProperties implements FileSystemProperties {

    private static final boolean DEFAULT_AUTO_CREATE_WORKING = false;
    private static final String DEFAULT_WORKING = Path.C_CURRENT_DIR;

    private final String endpoint;
    private final String ak;
    private final String sk;
    private final String bucket;
    private final String working;
    private final boolean autoCreateWorking;

    private ObsProperties(Builder builder) {
        this.endpoint = builder.endpoint;
        this.ak = builder.ak;
        this.sk = builder.sk;
        this.bucket = builder.bucket;
        this.working = builder.working;
        this.autoCreateWorking = builder.autoCreateWorking;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getAk() {
        return ak;
    }

    public String getSk() {
        return sk;
    }

    public String getBucket() {
        return bucket;
    }

    public String getWorking() {
        return working;
    }

    public boolean isAutoCreateWorking() {
        return autoCreateWorking;
    }

    public static class Builder {
        private String endpoint;
        private String ak;
        private String sk;
        private String bucket;
        private boolean autoCreateWorking;
        private String working;

        public Builder withEndpoint(String endpoint) {
            endpoint = (endpoint == null ? "" : endpoint.trim());
            if (endpoint.isEmpty()) {
                throw new IllegalArgumentException("Endpoint is required.");
            }
            this.endpoint = endpoint;
            return this;
        }

        public Builder withAk(String ak) {
            ak = (ak == null ? "" : ak.trim());
            if (ak.isEmpty()) {
                throw new IllegalArgumentException("Ak is required.");
            }
            this.ak = ak;
            return this;
        }

        public Builder withSk(String sk) {
            sk = (sk == null ? "" : sk.trim());
            if (sk.isEmpty()) {
                throw new IllegalArgumentException("Sk is required.");
            }
            this.sk = sk;
            return this;
        }

        public Builder withBucket(String bucket) {
            bucket = (bucket == null ? "" : bucket.trim());
            if (bucket.isEmpty()) {
                throw new IllegalArgumentException("Bucket is required.");
            }
            this.bucket = bucket;
            return this;
        }

        public Builder withAutoCreateWorking(Boolean autoCreateWorking) {
            this.autoCreateWorking = (autoCreateWorking == null ? DEFAULT_AUTO_CREATE_WORKING : autoCreateWorking);
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

        public ObsProperties build() {
            return new ObsProperties(this);
        }
    }

}
