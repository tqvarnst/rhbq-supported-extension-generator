package com.redhat.quarkus.pmtools.extensionsgenerator.utils;

import java.util.Comparator;

public class VersionComparator implements Comparator<String> {

    @Override
    public int compare(String thisKey, String otherKey) {
        String[] thisVersions = thisKey.split("\\.");
        String[] otherVersions = otherKey.split("\\.");
        int thisMajorVersion = Integer.parseInt(thisVersions[0]);
        int thisMinorVersion = Integer.parseInt(thisVersions[1]);
        int thisMicroVersion = Integer.parseInt(thisVersions[2]);
        String thisQualifier = "";
        if (thisVersions.length >= 4) {
            thisQualifier = thisVersions[3]; // Qualifier can be Final, SP1, SP3 etc.
        }
        int otherMajorVersion = Integer.parseInt(otherVersions[0]);
        int otherMinorVersion = Integer.parseInt(otherVersions[1]);
        int otherMicroVersion = Integer.parseInt(otherVersions[2]);
        String otherQualifier = "";
        if (otherVersions.length >= 4) {
            otherQualifier = otherVersions[3];
        }

        if (thisMajorVersion == otherMajorVersion) {
            if (thisMinorVersion == otherMinorVersion) {
                if (thisMicroVersion == otherMicroVersion) {
                    if (thisQualifier.equals(otherQualifier)) {
                        return 0;
                    } else if (thisQualifier.equals("Final") && otherQualifier.startsWith("SP")) {
                        return 1;
                    } else if (otherQualifier.equals("Final") && thisQualifier.startsWith("SP")) {
                        return -1;
                    } else {
                        return otherQualifier.compareTo(thisQualifier);
                    }
                } else {
                    return Integer.compare(otherMicroVersion,thisMicroVersion);
                }
            } else {
                return Integer.compare(otherMinorVersion,thisMinorVersion);
            }
        } else {
            return Integer.compare(otherMajorVersion,thisMajorVersion);
        }
    }
}
