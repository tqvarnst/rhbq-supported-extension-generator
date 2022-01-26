package com.redhat.quarkus.pmtools.extensionsgenerator.services;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.redhat.quarkus.pmtools.extensionsgenerator.utils.VersionComparator;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

@SuppressWarnings("all")
@ApplicationScoped
public class PlatformVersionService {

    private static final Pattern lineMatchPattern = Pattern.compile("(?m)^.*-redhat-.*$");
    private static final Pattern versionPattern = Pattern.compile("<td><a href=\".*\">(.*)/</a></td>");


    @Inject Vertx vertx;

    private List<String> filterBOMVersionsFromHTML(String html) {
        List<String> versions = new ArrayList<>();
        Matcher lines = lineMatchPattern.matcher(html);

        while( lines.find() ) {
            String line = lines.group();
            Matcher versionMatcher = versionPattern.matcher(line);
            if(versionMatcher.find()) {
                String version = versionMatcher.group(1);
                if(version.startsWith("1.")) {
                    versions.add(version);
                }
            }    
        }
        return versions;
    }

    private List<String> filterPlatformVersionsFromHTML(String html) {
        List<String> versions = new ArrayList<>();
        Matcher lines = lineMatchPattern.matcher(html);

        while( lines.find() ) {
            String line = lines.group();
            Matcher versionMatcher = versionPattern.matcher(line);
            if(versionMatcher.find()) {
                versions.add(versionMatcher.group(1));
            }
        }
        return versions;
    }

    private Uni<List<String>> getVersionsPre2x() {
        WebClientOptions options = new WebClientOptions()
            .setUserAgent("pm-tools/1.2.3")
            .setKeepAlive(false);
        WebClient client = WebClient.create(vertx, options);

        return Uni.createFrom().<List<String>> emitter( em ->
                client.get(443, "maven.repository.redhat.com", "/ga/io/quarkus/quarkus-bom/")
                    .ssl(true)
                    .send()
                    .onSuccess(response -> {
                        List<String> extensions = filterBOMVersionsFromHTML(response.bodyAsString());
                        em.complete(extensions);
                    })
                    .onFailure(response -> em.fail(response.getCause()))
        );
    }

    private Uni<List<String>> getPlatformVersions() {
        WebClientOptions options = new WebClientOptions()
                .setUserAgent("pm-tools/1.2.3")
                .setKeepAlive(false);
        WebClient client = WebClient.create(vertx, options);

        //https://maven.repository.redhat.com/
        return Uni.createFrom().<List<String>> emitter( em ->
                client.get(443, "maven.repository.redhat.com", "/ga/com/redhat/quarkus/platform/quarkus-bom-quarkus-platform-descriptor/")
                        .ssl(true)
                        .send()
                        .onSuccess(response -> {
                            List<String> extensions = filterPlatformVersionsFromHTML(response.bodyAsString());
                            Collections.sort(extensions);
                            em.complete(extensions);
                        })
                        .onFailure(response -> em.fail(response.getCause()))
        );
    }

    public Uni<List<String>> getVersions() {
        return getPlatformVersions();
//        Uni<List<String>> versionsPre2x = getVersionsPre2x();
//        Uni<List<String>> versionsPost2x = getPlatformVersions();
//        return Uni.combine()
//                .all()
//                .unis(versionsPre2x,versionsPost2x)
//                .combinedWith((l1, l2) -> { l2.addAll(l1); return l2; } )
//                .flatMap(versions -> {
//                    Map<String, String> versionMap = new TreeMap<>(new VersionComparator());
//                    versions.forEach( version -> {
//                        String key = version.indexOf('-')>0 ? version.substring(0, version.indexOf('-')) : version;
//                        versionMap.put(key, version);
//                    });
//                    return Uni.createFrom().item(new ArrayList<String>(versionMap.values()));
//                });
    }




}

