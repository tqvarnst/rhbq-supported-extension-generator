package com.redhat.quarkus.pmtools.extensionsgenerator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;


@ApplicationScoped
public class ExtensionHelper {
    
    @Inject
    @RestClient
    ExtensionService service;

    static Comparator<Extension> compareByArtifactId= Comparator.comparing(Extension::getArtifactId);

    Map<String,List<Extension>> extensionMap = new HashMap<>();


    public List<Extension> getSupportedExtensions(String stream) {
        return getAllExtensions(stream).stream()
            .filter(e -> e.id.startsWith("io.quarkus:"))
            .filter(e -> Arrays.asList(e.tags).contains("supported"))
            .collect(Collectors.toMap(Extension::getArtifactId, Function.identity(), (oldValue, newValue) -> oldValue)).values().stream() //To avoid duplicates
            .sorted(compareByArtifactId)
            .collect(Collectors.toList());
    }

    public List<Extension> getSupportedInJVMExtensions(String stream) {
        return getAllExtensions(stream).stream()
            .filter(e -> e.id.startsWith("io.quarkus:"))
            .filter(e -> Arrays.asList(e.tags).contains("supported-in-jvm"))
            .collect(Collectors.toMap(Extension::getArtifactId, Function.identity(), (oldValue, newValue) -> oldValue)).values().stream() //To avoid duplicates
            .sorted(compareByArtifactId)
            .collect(Collectors.toList());
    }

    public List<Extension> getTechpreviewExtensions(String stream) {
        return getAllExtensions(stream).stream()
            .filter(e -> e.id.startsWith("io.quarkus:"))
            .filter(e -> Arrays.asList(e.tags).contains("tech-preview"))
            .collect(Collectors.toMap(Extension::getArtifactId, Function.identity(), (oldValue, newValue) -> oldValue)).values().stream() //To avoid duplicates
            .sorted(compareByArtifactId)
            .collect(Collectors.toList());
    }

    public List<Extension> getDevSupportedExtensions(String stream) {
        return getAllExtensions(stream).stream()
            .filter(e -> e.id.startsWith("io.quarkus:"))
            .filter(e -> Arrays.asList(e.tags).contains("dev-support"))
            .collect(Collectors.toMap(Extension::getArtifactId, Function.identity(), (oldValue, newValue) -> oldValue)).values().stream() //To avoid duplicates
            .sorted(compareByArtifactId)
            .collect(Collectors.toList());
    }

    public List<Extension> getProductExtensions(String stream) {
        return getAllExtensions(stream).stream()
            .filter(e -> e.id.startsWith("io.quarkus:"))
            .filter(e -> e.version.contains("-redhat-"))
            .collect(Collectors.toMap(Extension::getArtifactId, Function.identity(), (oldValue, newValue) -> oldValue)).values().stream() //To avoid duplicates
            .sorted(compareByArtifactId)
            .collect(Collectors.toList());
    }

    public Extension getExtension(String stream, String artifactId) {
        return new Extension();
    }

    private List<Extension> getAllExtensions(String stream) {
        if(!extensionMap.containsKey(stream)) {
            List<Extension> allExtensions = service.getByStream(stream);
            extensionMap.put(stream, allExtensions);
            
        } 
        return extensionMap.get(stream);

    }

}
