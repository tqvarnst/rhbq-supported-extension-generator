package com.redhat.quarkus.pmtools.extensionsgenerator.services;

import com.redhat.quarkus.pmtools.extensionsgenerator.models.Platform;
import com.redhat.quarkus.pmtools.extensionsgenerator.utils.VersionUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PlatformVersionService {

    @RestClient
    PlatformVersionRestClient restClient;

    public List<String> getVersions() {
        List<Platform> platforms = restClient.getAllPlatforms().getPlatforms();
        List<String> versions = new ArrayList<>();

        platforms.forEach(p -> p.getStreams().forEach(s -> s.getReleases().forEach(r ->  versions.add(r.getVersion()))));

        return filterPlatformVersionsByShortNames(versions);
    }

    private List<String> filterPlatformVersionsByShortNames(List<String> versionList) {
        Map<String,String> shortVersionMap = new HashMap<>();

        versionList.forEach( v -> {
            String shortVersion = VersionUtils.shortVersion(v);
            if(shortVersionMap.containsKey(shortVersion)) {
                if(largerThanExistingVersion(v,shortVersionMap.get(shortVersion))) {
                    shortVersionMap.replace(shortVersion,v);
                }
            } else {
                shortVersionMap.put(shortVersion,v);
            }
        });

        return new ArrayList<>(shortVersionMap.values());

    }

    private boolean largerThanExistingVersion(String v, String s) {
        String shortName = VersionUtils.shortVersion(v);
        String vBuildNumber = v.replace(shortName+"-redhat-","");
        String sBuildNumber = s.replace(shortName+"-redhat-","");
        return Integer.parseInt(vBuildNumber) > Integer.parseInt(sBuildNumber);
    }
}



