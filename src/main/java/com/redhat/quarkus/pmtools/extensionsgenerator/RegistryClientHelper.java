package com.redhat.quarkus.pmtools.extensionsgenerator;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RegistryClientHelper {
    
    @Inject
    @RestClient
    RegistryClientService registry;

    Uni<List<Platform>> platforms;

    public Uni<List<String>> getAvailableStreams() {
        return getStreams().onItem().transform(streams -> streams.stream().map(s -> s.id).collect(Collectors.toList()));
        //return getStreams().stream().map(s -> s.id).collect(Collectors.toList());
    }

    public Uni<List<String>> getExactVersions() {
        //return getStreams().stream().map(s -> s.releases).flatMap(List::stream).map(r -> r.version).collect(Collectors.toList());
        return getStreams().onItem().transform(streams -> streams.stream().map(s->s.releases).flatMap(List::stream).map(r -> r.version).collect(Collectors.toList()));
    }

    private Uni<List<Stream>> getStreams() {
        //return getPlatforms().onItem().stream().map(p -> p.streams).flatMap(List::stream).collect(Collectors.toList());
        return getPlatforms().onItem().transform(ps -> ps.stream().map( p -> p.streams ).flatMap(List::stream).collect(Collectors.toList()));
    }

    private Uni<List<Platform>> getPlatforms() {
        if( this.platforms == null) {
            platforms = registry.getPlatforms().onItem().transformToUni(l ->  Uni.createFrom().item(l.platforms));
        }
        return platforms;
    }
}
