package com.redhat.quarkus.pmtools.extensionsgenerator.services;

import com.redhat.quarkus.pmtools.extensionsgenerator.models.Platform;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PlatformVersionService {

    @RestClient
    PlatformVersionRestClient restClient;

    public List<String> getVersions() {
        List<Platform> platforms = restClient.getAllPlatforms().getPlatforms();
        List<String> versions = new ArrayList<>();

        platforms.forEach(p -> p.getStreams().forEach(s -> s.getReleases().forEach(r ->  versions.add(r.getVersion()))));

        return versions;
    }
}



