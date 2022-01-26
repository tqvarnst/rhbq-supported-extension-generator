package com.redhat.quarkus.pmtools.extensionsgenerator.services;

import io.quarkus.registry.catalog.ExtensionCatalog;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.apache.commons.io.IOUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@ApplicationScoped
public class ExtensionCatalogService {

    @SuppressWarnings("all")
    @Inject
    Vertx vertx;

    @SuppressWarnings("all")
    public Uni<ExtensionCatalog> getExtensionCatalogForVersion(String version) {
        WebClientOptions options = new WebClientOptions()
                .setUserAgent("pm-tools/1.2.3")
                .setKeepAlive(false);
        WebClient client = WebClient.create(vertx, options);

        //https://maven.repository.redhat.com/ga/com/redhat/quarkus/platform/quarkus-bom-quarkus-platform-descriptor/2.2.3.SP2-redhat-00001/quarkus-bom-quarkus-platform-descriptor-2.2.3.SP2-redhat-00001-2.2.3.SP2-redhat-00001.json
        return Uni.createFrom().<ExtensionCatalog> emitter( em ->
                client.get(443, "maven.repository.redhat.com", String.format("/ga/com/redhat/quarkus/platform/quarkus-bom-quarkus-platform-descriptor/%s/quarkus-bom-quarkus-platform-descriptor-%s-%s.json",version,version,version))
                        .ssl(true)
                        .send()
                        .onSuccess(response -> {
                            try {
                                ExtensionCatalog extensionCatalog = ExtensionCatalog.fromStream(IOUtils.toInputStream(response.bodyAsString(Charset.defaultCharset().name()), Charset.defaultCharset()));
                                em.complete(extensionCatalog);
                            } catch (IOException e) {
                                em.fail(e);
                            }
                        })
                        .onFailure(response -> em.fail(response.getCause()))
        );
    }
}
