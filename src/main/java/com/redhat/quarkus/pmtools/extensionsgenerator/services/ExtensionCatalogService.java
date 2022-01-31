package com.redhat.quarkus.pmtools.extensionsgenerator.services;

import io.quarkus.logging.Log;
import io.quarkus.maven.ArtifactCoords;
import io.quarkus.registry.ExtensionCatalogResolver;
import io.quarkus.registry.RegistryResolutionException;
import io.quarkus.registry.catalog.ExtensionCatalog;
import io.quarkus.registry.config.RegistriesConfig;
import io.quarkus.registry.config.RegistryConfig;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ExtensionCatalogService {

    @SuppressWarnings("all")
    public Uni<ExtensionCatalog> getExtensionCatalogForVersion(String version) {
        final ArtifactCoords platformQuarkusBom = ArtifactCoords.fromString(String.format("com.redhat.quarkus.platform:quarkus-bom::pom:%s",version));
        RegistriesConfig registries = RegistriesConfig.builder()
                .setRegistries(List.of(
                        RegistryConfig.builder().setId("registry.quarkus.redhat.com"),
                        RegistryConfig.builder().setId("registry.quarkus.io")
                                // disable non-platform extensions
                                .setNonPlatformExtensions(null)))
                .build();
        Log.debugf("Trying to resolve extension catalog for version %s",version);
        try {
            ExtensionCatalog catalog = ExtensionCatalogResolver.builder()
                    .config(registries)
                    .build()
                    .resolveExtensionCatalog(List.of(platformQuarkusBom));
            return Uni.createFrom().item(catalog);
        } catch (RegistryResolutionException e) {
            Log.debugf("Failed to resolve extension catalog for version %s",version);
            return Uni.createFrom().failure(e);
        }
    }
}
