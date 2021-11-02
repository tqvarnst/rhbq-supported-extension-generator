package com.redhat.quarkus.pmtools.extensionsgenerator;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class RegistryClientHelper {
    
    @Inject
    @RestClient
    RegistryClientService registry;

    List<Platform> platforms;

    public List<String> getAvailableStreams() {
        return getStreams().stream().map(s -> s.id).collect(Collectors.toList());
    }

    public List<String> getExactVersions() {
        return getStreams().stream().map(s -> s.releases).flatMap(List::stream).map(r -> r.version).collect(Collectors.toList());
    }

    private List<Stream> getStreams() {
        return getPlatforms().stream().map(p -> p.streams).flatMap(List::stream).collect(Collectors.toList());
    }

    private List<Platform> getPlatforms() {
        if(this.platforms==null)
            this.platforms = registry.getPlatforms().platforms;
        return this.platforms;
    }
}
