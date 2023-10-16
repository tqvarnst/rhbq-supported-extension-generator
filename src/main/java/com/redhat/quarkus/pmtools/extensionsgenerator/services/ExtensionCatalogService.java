package com.redhat.quarkus.pmtools.extensionsgenerator.services;

import io.quarkus.bootstrap.resolver.maven.MavenArtifactResolver;
import io.quarkus.logging.Log;
import io.quarkus.maven.ArtifactCoords;
import io.quarkus.registry.ExtensionCatalogResolver;
import io.quarkus.registry.RegistryResolutionException;
import io.quarkus.registry.catalog.ExtensionCatalog;
import io.quarkus.registry.catalog.PlatformCatalog;
import io.quarkus.registry.config.RegistriesConfig;
import io.quarkus.registry.config.RegistriesConfigLocator;
import io.quarkus.registry.config.RegistryConfig;
import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ExtensionCatalogService {

	private static final String RH_QUARKUS_REGISTRY = "registry.quarkus.redhat.com";
	
	private volatile RegistriesConfig config;
	
	private RegistriesConfig getConfig() {
		return config == null ? config = initConfig() : config;
	}
		
	@Inject
	MavenArtifactResolver artifactResolver;

	private volatile ExtensionCatalogResolver catalogResolver;

    @SuppressWarnings("all")
    public Uni<ExtensionCatalog> getExtensionCatalogForVersion(String version) {
        final ArtifactCoords platformQuarkusBom = ArtifactCoords.fromString(String.format("com.redhat.quarkus.platform:quarkus-bom::pom:%s",version));
        Log.debugf("Trying to resolve extension catalog for version %s",version);
        try {
            ExtensionCatalog catalog = getCatalogResolver().resolveExtensionCatalog(List.of(platformQuarkusBom));
            return Uni.createFrom().item(catalog);
        } catch (RegistryResolutionException e) {
            Log.debugf("Failed to resolve extension catalog for version %s",version);
            return Uni.createFrom().failure(e);
        }
    }

	private ExtensionCatalogResolver getCatalogResolver() throws RegistryResolutionException {
		if (catalogResolver == null) {
			catalogResolver = ExtensionCatalogResolver.builder()
					.artifactResolver(artifactResolver)
					.config(getConfig())
					.build();
		}
		return catalogResolver;
	}

	private RegistriesConfig initConfig() {
		RegistriesConfig config = RegistriesConfigLocator.resolveConfig();
		for (RegistryConfig registry : config.getRegistries()) {
			if (registry.getId().equals(RH_QUARKUS_REGISTRY)) {
				if(registry.isEnabled()) {
				    return config;
				}
				final StringBuilder sb = new StringBuilder();
				sb.append(RH_QUARKUS_REGISTRY).append(" was found disabled in ");
				if(config.getSource() != null && config.getSource().getFilePath() != null) {
					sb.append(config.getSource().getFilePath());
				} else {
					sb.append("the configuration");
				}
				sb.append(", it will be automatically enabled for this session");
				Log.warn(sb.toString());
				//Logger.getLogger(ExtensionCatalogService.class).warn(sb.toString());
				break;
			}
		}
		final List<RegistryConfig> registries = new ArrayList<>(config.getRegistries().size() + 1);
		registries.add(RegistryConfig.builder().setId(RH_QUARKUS_REGISTRY).build());
		registries.addAll(config.getRegistries());
		return config.mutable().setRegistries(registries).build();
	}
}
