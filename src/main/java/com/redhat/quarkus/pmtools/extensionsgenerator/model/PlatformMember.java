package com.redhat.quarkus.pmtools.extensionsgenerator.model;

public class PlatformMember {

    String group;
    String artifactId;
    String version;
    String type;
    String platformVersion;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }

    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    @Override
    public String toString() {
        return "PlatformMember{" +
                "group='" + group + '\'' +
                ", artifact='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", type='" + type + '\'' +
                ", platformVersion='" + platformVersion + '\'' +
                '}';
    }
}
