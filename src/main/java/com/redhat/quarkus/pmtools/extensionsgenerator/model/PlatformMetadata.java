package com.redhat.quarkus.pmtools.extensionsgenerator.model;

import java.util.List;

public class PlatformMetadata {

    String platformKey;

    String stream;

    String version;

    List<PlatformMember> members;

    public String getPlatformKey() {
        return platformKey;
    }

    public void setPlatformKey(String platformKey) {
        this.platformKey = platformKey;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<PlatformMember> getMembers() {
        return members;
    }

    public void setMembers(List<PlatformMember> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "PlatformMetadata{" +
                "platformKey='" + platformKey + '\'' +
                ", stream='" + stream + '\'' +
                ", version='" + version + '\'' +
                ", members=" + members +
                '}';
    }
}
