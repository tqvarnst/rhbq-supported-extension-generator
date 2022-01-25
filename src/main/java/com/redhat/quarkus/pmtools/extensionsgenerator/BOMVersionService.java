package com.redhat.quarkus.pmtools.extensionsgenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

@ApplicationScoped
public class BOMVersionService {

    private static final Pattern lineMatchPattern = Pattern.compile("(?m)^.*-redhat-.*$");
    private static final Pattern versionPattern = Pattern.compile("<td><a href=\".*\">(.*)/</a></td>");

    @Inject Vertx vertx;

    private List<String> filterVersionsFromHTML(String html) {
        List<String> versions = new ArrayList<>();
        Matcher lines = lineMatchPattern.matcher(html);

        while( lines.find() ) {
            String line = lines.group();
            Matcher versionMatcher = versionPattern.matcher(line);
            if(versionMatcher.find()) {
                versions.add(versionMatcher.group(1));
            }    
        }
        Collections.reverse(versions);
        Map<String, String> versionMap = new LinkedHashMap<>();
        versions.forEach( version -> {
            String key = version.indexOf('-')>0 ? version.substring(0, version.indexOf('-')) : version;
            if(!versionMap.containsKey(key)) {
                versionMap.put(key, version);
            }
        });

        
        return new ArrayList<>(versionMap.values());
    }

    public Uni<List<String>> getVersions() {
        WebClientOptions options = new WebClientOptions()
            .setUserAgent("pm-tools/1.2.3")
            .setKeepAlive(false);
        WebClient client = WebClient.create(vertx, options);

        return Uni.createFrom().<List<String>> emitter( em ->
                client.get(443, "maven.repository.redhat.com", "/ga/io/quarkus/quarkus-bom/")
                    .ssl(true)
                    .send()
                    .onSuccess(response -> {
                        System.out.println("Received response with status code " + response.statusCode());
                        List<String> extensions = filterVersionsFromHTML(response.bodyAsString());
                        em.complete(extensions);
                    })
                    .onFailure(response -> em.fail(response.getCause()))
        );
    }


}
