package com.redhat.quarkus.pmtools.extensionsgenerator.services;

import com.redhat.quarkus.pmtools.extensionsgenerator.models.Platform;
import com.redhat.quarkus.pmtools.extensionsgenerator.utils.VersionUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.enterprise.context.ApplicationScoped;
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
            /**
             * Because there is an issue where a number of 2.7.6 releases that was SP release was named Final but with
             * a new build number we need to treat 2.7.6 as a special case.
             */
            if("2.7.6.Final".equals(shortVersion)) {
              switch(v) {
                  case "2.7.6.Final-redhat-00006" -> shortVersionMap.put("2.7.6.Final",v);
                  case "2.7.6.Final-redhat-00009" -> shortVersionMap.put("2.7.6.SP1",v);
                  case "2.7.6.Final-redhat-00011" -> shortVersionMap.put("2.7.6.SP2",v);
                  case "2.7.6.Final-redhat-00012" -> shortVersionMap.put("2.7.6.SP3",v);
              }
            } else if(shortVersionMap.containsKey(shortVersion)) {
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



