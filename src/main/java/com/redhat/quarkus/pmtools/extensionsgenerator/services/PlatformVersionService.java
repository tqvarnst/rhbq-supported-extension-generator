package com.redhat.quarkus.pmtools.extensionsgenerator.services;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.quarkus.pmtools.extensionsgenerator.utils.VersionComparator;
import io.smallrye.mutiny.TimeoutException;
import io.smallrye.mutiny.Uni;
import io.quarkus.logging.Log;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@SuppressWarnings("all")
@ApplicationScoped
public class PlatformVersionService {

    private static final Pattern lineMatchPattern = Pattern.compile("(?m)^.*-redhat-.*$");
    private static final Pattern versionPattern = Pattern.compile("<td><a href=\".*\">(.*)/</a></td>");

    @Inject
    @RestClient
    PlatformVersionRestClient restClient;

    private List<String> filterPlatformVersionsFromHTML(String html) {
        List<String> versions = new ArrayList<>();
        Matcher lines = lineMatchPattern.matcher(html);
        while( lines.find() ) {
            String line = lines.group();
            Matcher versionMatcher = versionPattern.matcher(line);
            if(versionMatcher.find()) {
                String versionLine = versionMatcher.group(1);
                Log.debugf("Line matching the versionMatcher: %s",versionLine);
                versions.add(versionLine);
            }
        }
        return versions;
    }

    private Uni<List<String>> getPlatformVersions() {
        return restClient.getPlatformVersionHTML().onItem().transformToUni(response -> Uni.createFrom().item(filterPlatformVersionsFromHTML(response)));
    }

    public Uni<List<String>> getVersionsAsync() {
        return getPlatformVersions();
    }

    public List<String> getVersions(int timoutSeconds) throws RegistryRestServiceException {
        String mavenRepoUrl = ConfigProvider.getConfig().getConfigValue("quarkus.rest-client.platform-version.url").getValue();
        Log.debugf("Getting platform version from %s/ga/com/redhat/quarkus/platform/quarkus-bom", mavenRepoUrl);

        Uni<List<String>> platformVersionsUni = this.getVersionsAsync().ifNoItem().after(Duration.ofSeconds(timoutSeconds)).fail().onFailure().invoke(t -> Log.error("Failed to get platform versions", t));

        try {
            return platformVersionsUni.await().indefinitely()
                    .stream()
                    .filter(v -> !v.startsWith("1."))
                    .sorted(new VersionComparator())
                    .collect(Collectors.toList());
        } catch (Throwable e) {
            Log.debug("Failed to retrive platform versions", e);
            if(e instanceof TimeoutException){
                System.out.printf("The registry %s didn't respond within %d seconds. Please verify that the registry is responding and consder increasing the timeout %n", mavenRepoUrl, timoutSeconds);
            } else {
                System.out.printf("The registry %s didn't respond or responded with an error. Aborting%n", mavenRepoUrl);
            }
            throw new RegistryRestServiceException(e);
        }
    }

}

class RegistryRestServiceException extends Exception {
    @SuppressWarnings("CdiInjectionPointsInspection")
    RegistryRestServiceException(Throwable e) {
        super("Failed to retrive platform version from the registry",e);
    }
}

