package com.redhat.quarkus.pmtools.extensionsgenerator.services;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.mutiny.Uni;
import io.quarkus.logging.Log;
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

    public Uni<List<String>> getVersions() {
        return getPlatformVersions();
    }
}

