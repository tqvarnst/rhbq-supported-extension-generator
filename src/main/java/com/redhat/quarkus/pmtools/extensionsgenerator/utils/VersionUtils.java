package com.redhat.quarkus.pmtools.extensionsgenerator.utils;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtils {

    private static final Pattern pattern = Pattern.compile("^(\\d+\\.\\d+\\.\\d+\\.\\w+).*");

    private static final Pattern mainVersionPattern = Pattern.compile("^(\\d+)\\..*");

    public static String shortVersion(String fullVersion) {
        /**
         * Because there is an issue where a number of 2.7.6 releases that was SP release was named Final but with
         * a new build number we need to treat 2.7.6 as a special case.
         */
        String shortVersion=fullVersion; //default to return the full version if pattern doesn't match
        switch(fullVersion) {
            case "2.7.6.Final-redhat-00006" -> shortVersion="2.7.6.Final";
            case "2.7.6.Final-redhat-00009" -> shortVersion="2.7.6.SP1";
            case "2.7.6.Final-redhat-00011" -> shortVersion="2.7.6.SP2";
            case "2.7.6.Final-redhat-00012" -> shortVersion="2.7.6.SP3";
            default -> {
                Matcher matcher = pattern.matcher(fullVersion);
                if(matcher.matches() && matcher.groupCount()==1) {
                    shortVersion=matcher.group(1);
                }
            }
        }
        return shortVersion;

    }


    public static String mainVersion(String fullVersion) {
        Matcher matcher = mainVersionPattern.matcher(fullVersion);
        if(matcher.matches() && matcher.groupCount()==1) {
            return matcher.group(1);
        }
        throw new RuntimeException("Failed to parse Main version");
    }
}
