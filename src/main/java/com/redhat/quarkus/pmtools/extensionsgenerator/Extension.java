package com.redhat.quarkus.pmtools.extensionsgenerator;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Extension {
    public String id;
    public String version;
    public String[] tags;
    public boolean platform;
    public String bom;

    
    public String getGroupId() {
        return id.split(":")[0];
    }

    public String getArtifactId() {
        return id.split(":")[1];
    }
    
    @Override
    public String toString() {
        return String.format("%s:%s\t%s", id, version, String.join(",", tags));
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Extension)
            return this.id.equals(((Extension) obj).id);    
        else
            return false;
    }


}