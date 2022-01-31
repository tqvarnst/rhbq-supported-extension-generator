package com.redhat.quarkus.pmtools.extensionsgenerator.model;

import com.redhat.quarkus.pmtools.extensionsgenerator.utils.VersionUtils;
import io.quarkus.registry.catalog.Extension;

import java.util.List;

public class Platform {

    private String fullVersion;
    private String shortVersion;
    private String quarkusCoreVersion;
    private String upstreamQuarkusVersion;
    private PlatformMetadata platformMetadata;
    private List<Extension> extensions;


    public Platform() {
    }



    public String getFullVersion() {
        return fullVersion;
    }

    public void setFullVersion(String fullVersion) {
        this.fullVersion = fullVersion;
    }

    public String getShortVersion() {
        return shortVersion;
    }

    public void setShortVersion(String shortVersion) {
        this.shortVersion = shortVersion;
    }

    public String getQuarkusCoreVersion() {
        return quarkusCoreVersion;
    }

    public void setQuarkusCoreVersion(String quarkusCoreVersion) {
        this.quarkusCoreVersion = quarkusCoreVersion;
    }

    public String getUpstreamQuarkusVersion() {
        return upstreamQuarkusVersion;
    }

    public void setUpstreamQuarkusVersion(String upstreamQuarkusVersion) {
        this.upstreamQuarkusVersion = upstreamQuarkusVersion;
    }

    public PlatformMetadata getPlatformMetadata() {
        return platformMetadata;
    }

    public void setPlatformMetadata(PlatformMetadata platformMetadata) {
        this.platformMetadata = platformMetadata;
    }

    public List<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<Extension> extensions) {
        this.extensions = extensions;
    }

    @Override
    public String toString() {
        return "Platform{" +
                "fullVersion='" + fullVersion + '\'' +
                ", shortVersion='" + shortVersion + '\'' +
                ", quarkusCoreVersion='" + quarkusCoreVersion + '\'' +
                ", upstreamQuarkusVersion='" + upstreamQuarkusVersion + '\'' +
                ", platformMetadata=" + platformMetadata +
                ", extensions=" + extensions +
                '}';
    }
}
