package com.csgo.upload;

/**
 * @author admin
 * @since 2018/6/19
 */
public class AliOSSConfig extends StorageConfig {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String publicEndpoint;

    public static class Builder {

        private final AliOSSConfig aliOSSConfig;

        private Builder() {
            this.aliOSSConfig = new AliOSSConfig();
        }

        public static Builder config() {
            return new Builder();
        }

        public Builder endpoint(String endpoint) {
            this.aliOSSConfig.setEndpoint(endpoint);
            return this;
        }

        public Builder accessKeyId(String accessKeyId) {
            this.aliOSSConfig.setAccessKeyId(accessKeyId);
            return this;
        }

        public Builder accessKeySecret(String accessKeySecret) {
            this.aliOSSConfig.setAccessKeySecret(accessKeySecret);
            return this;
        }

        public Builder bucketName(String bucketName) {
            this.aliOSSConfig.setBucketName(bucketName);
            return this;
        }

        public Builder publicEndpoint(String publicEndpoint) {
            this.aliOSSConfig.setPublicEndpoint(publicEndpoint);
            return this;
        }

        public Builder directory(String directory) {
            this.aliOSSConfig.setDirectory(directory);
            return this;
        }

        public AliOSSConfig build() {
            return this.aliOSSConfig;
        }
    }

    AliOSSConfig() {
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getPublicEndpoint() {
        return publicEndpoint;
    }

    public void setPublicEndpoint(String publicEndpoint) {
        this.publicEndpoint = publicEndpoint;
    }

}
