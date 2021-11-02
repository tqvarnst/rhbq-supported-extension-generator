package com.redhat.quarkus.pmtools.extensionsgenerator;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Platforms {
    public List<Platform> platforms;
    
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Platform {
    public String name;
    public List<Stream> streams;
    
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Stream {
    public String id;
    public List<Release> releases;
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Release {
    public String version;
    
    @JsonProperty("quarkus-core-version")
    public String quarkusCoreVersion;
    
    @JsonProperty("upstream-quarkus-core-version")
    public String upstreamQuarkusCoreVersion; 
}
