package com.redhat.quarkus.pmtools.extensionsgenerator.model;

import io.quarkus.logging.Log;

public class PlatformMemberFactory {

    public static PlatformMember create(String memberString) {
        PlatformMember platformMember = new PlatformMember();
        try {
            String[] split = memberString.split(":");
            if(split.length == 5) {
                platformMember.setGroup(split[0]);
                platformMember.setArtifactId(split[1]);
                platformMember.setVersion(split[2]);
                platformMember.setType(split[3]);
                platformMember.setPlatformVersion(split[4]);
            } else {
                Log.errorf("Lenght of member string is %d, it should be 5. Failed to parse member details for string %s",split.length,memberString);
            }

        } catch(RuntimeException e) {
            Log.errorf("Failed to parse member string %",memberString);
            Log.error(e);
        }
        return platformMember;
    }
}
