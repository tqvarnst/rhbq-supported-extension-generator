package com.redhat.quarkus.pmtools.extensionsgenerator.models;


import java.util.List;

public class AllPlatforms {

    List<Platform> platforms;

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
    }

    @Override
    public String toString() {
        return "AllPlatforms{" +
                "platforms=" + platforms +
                '}';
    }
}
