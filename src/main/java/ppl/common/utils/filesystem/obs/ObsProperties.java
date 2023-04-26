package ppl.common.utils.filesystem.obs;

import ppl.common.utils.filesystem.FileSystemProperties;

public class ObsProperties implements FileSystemProperties {
    private final String endpoint;
    private final String ak;
    private final String sk;
    private final String bucket;

    private ObsProperties(String endpoint, String ak, String sk, String bucket) {
        if (endpoint.isEmpty()) {
            throw new IllegalArgumentException("Endpoint is required.");
        }
        if (ak.isEmpty()) {
            throw new IllegalArgumentException("Ak is required.");
        }
        if (sk.isEmpty()) {
            throw new IllegalArgumentException("Sk is required.");
        }
        if (bucket.isEmpty()) {
            throw new IllegalArgumentException("Bucket is required.");
        }
        this.endpoint = endpoint;
        this.ak = ak;
        this.sk = sk;
        this.bucket = bucket;
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

    public static class Builder {
        private String endpoint;
        private String ak;
        private String sk;
        private String bucket;

        public Builder withEndpoint(String endpoint) {
            this.endpoint = endpoint == null ? "" : endpoint.trim();
            return this;
        }

        public Builder withAk(String ak) {
            this.ak = ak == null ? "" : ak.trim();
            return this;
        }

        public Builder withSk(String sk) {
            this.sk = sk == null ? "" : sk.trim();
            return this;
        }

        public Builder withBucket(String bucket) {
            this.bucket = bucket == null ? "" : bucket.trim();
            return this;
        }

        public ObsProperties build() {
            return new ObsProperties(this.endpoint, this.ak, this.sk, this.bucket);
        }
    }

}
