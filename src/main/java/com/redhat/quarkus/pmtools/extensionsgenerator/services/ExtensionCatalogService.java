package com.redhat.quarkus.pmtools.extensionsgenerator.services;

import com.redhat.quarkus.pmtools.extensionsgenerator.model.Platform;
import com.redhat.quarkus.pmtools.extensionsgenerator.model.PlatformMember;
import io.quarkus.logging.Log;
import io.quarkus.registry.catalog.ExtensionCatalog;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@ApplicationScoped
public class ExtensionCatalogService {

    @Inject
    @RestClient
    ExtensionCatalogRestClient restClient;

    @SuppressWarnings("all")
    public Uni<ExtensionCatalog> getExtensionCatalogForVersion(String version) {
        return restClient.getExtensionCatalogJsonFromVersion(version,version,version)
                .onItem()
                .transform(s -> {
                    Log.debugf("Getting ExtensionCatalog for version %s.",version);
                    try {
                        return ExtensionCatalog.fromStream(IOUtils.toInputStream(s, Charset.defaultCharset()));
                    } catch (IOException e) {
                        Log.errorf("Failed to create Input Stream for %s.",version,e);
                        return null;
                    }
                })
                .onFailure().recoverWithNull();
    }

    public Uni<ExtensionCatalog> getExtensionCatalogForMember(PlatformMember member) {
        return restClient.getExtensionCatalogJsonForMember(member.getPlatformVersion(),
                member.getArtifactId(),
                member.getVersion(),
                member.getVersion())
                    .onItem()
                    .transform(s -> {
                        Log.debugf("Getting ExtensionCatalog for member %s:%s:%s.",member.getGroup(),member.getArtifactId(),member.getVersion());
                        try {
                            return ExtensionCatalog.fromStream(IOUtils.toInputStream(s, Charset.defaultCharset()));
                        } catch (IOException e) {
                            Log.errorf("Failed to create Input Stream for %s:%s:%s",member.getGroup(),member.getArtifactId(),member.getVersion());
                            Log.error(e);
                            return null;
                        }
                    })
                    .onFailure().recoverWithNull();
    }


    private ExtensionCatalog fromString(String s) {
        try {
            return ExtensionCatalog.fromStream(new ByteArrayInputStream(s.getBytes()));
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
