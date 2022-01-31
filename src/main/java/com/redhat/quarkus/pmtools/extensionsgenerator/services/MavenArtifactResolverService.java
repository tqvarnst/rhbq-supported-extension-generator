package com.redhat.quarkus.pmtools.extensionsgenerator.services;

import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.eclipse.aether.repository.RemoteRepository;

import io.quarkus.bootstrap.resolver.maven.BootstrapMavenContext;
import io.quarkus.bootstrap.resolver.maven.BootstrapMavenException;
import io.quarkus.bootstrap.resolver.maven.MavenArtifactResolver;

@Singleton
public class MavenArtifactResolverService {

	private static final String RH_GA_ID = "redhat";
	private static final String RH_GA_URL = "https://maven.repository.redhat.com/ga";

	private static volatile MavenArtifactResolver resolver;

	@Produces
	public MavenArtifactResolver getArtifactResolver() {
		if (resolver == null) {
			try {
				final BootstrapMavenContext mavenContext = new BootstrapMavenContext(
						BootstrapMavenContext.config().setWorkspaceDiscovery(false));
				return resolver = MavenArtifactResolver.builder()
						.setRepositorySystem(mavenContext.getRepositorySystem())
						.setRepositorySystemSession(mavenContext.getRepositorySystemSession())
						.setRemoteRepositories(ensureRedHatGaRepository(mavenContext))
						.setRemoteRepositoryManager(mavenContext.getRemoteRepositoryManager())
						.setWorkspaceDiscovery(false).build();
			} catch (BootstrapMavenException e) {
				throw new IllegalStateException("Failed to initialize Maven artifact resolver", e);
			}
		}
		return resolver;
	}

	private static List<RemoteRepository> ensureRedHatGaRepository(BootstrapMavenContext mavenContext)
			throws BootstrapMavenException {
		if (isRedHatGaConfigured(mavenContext.getRemoteRepositories())) {
			return mavenContext.getRemoteRepositories();
		}
		// add the RedHat Maven repo
		final RemoteRepository redhatGa = new RemoteRepository.Builder(RH_GA_ID, "default", RH_GA_URL).build();
		// move it before Central, although it's not critical
		final List<RemoteRepository> dominant = mavenContext.getRemoteRepositoryManager()
				.aggregateRepositories(mavenContext.getRepositorySystemSession(), List.of(), List.of(redhatGa), true);
		return mavenContext.getRemoteRepositoryManager().aggregateRepositories(
				mavenContext.getRepositorySystemSession(), dominant, mavenContext.getRemoteRepositories(), false);
	}

	private static boolean isRedHatGaConfigured(List<RemoteRepository> repos) {
		for (RemoteRepository repo : repos) {
			if (isRedHatGaConfigured(repo)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isRedHatGaConfigured(RemoteRepository repo) {
		if (repo.getUrl().startsWith(RH_GA_URL)) {
			return true;
		}
		return !repo.getMirroredRepositories().isEmpty() && isRedHatGaConfigured(repo.getMirroredRepositories());
	}
}
