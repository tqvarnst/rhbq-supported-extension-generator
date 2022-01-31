package com.redhat.quarkus.pmtools.extensionsgenerator.model;

import io.quarkus.logging.Log;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class PlatformMetadataFactory {

    public static PlatformMetadata create(Map<String,Object> metaData) {
        PlatformMetadata metadata = new PlatformMetadata();

        if(metaData.containsKey("platform-key")) {
            metadata.platformKey = (String) metaData.get("platform-key");
        }
        if(metaData.containsKey("stream")) {
            metadata.stream = (String) metaData.get("stream");
        }
        if(metaData.containsKey("version")) {
            metadata.version = (String) metaData.get("version");
        }
        if(metaData.containsKey("members")) {
            Object members = metaData.get("members");
            if(members instanceof Collection) {
                metadata.setMembers(((Collection<String>)members).stream().map(PlatformMemberFactory::create).collect(Collectors.toList()));
            }
        }
        return metadata;

    }
}
