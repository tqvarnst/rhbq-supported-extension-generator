package com.redhat.quarkus.pmtools.extensionsgenerator.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtils {

    private static final Pattern pattern = Pattern.compile("^(\\d+\\.\\d+\\.\\d+\\.\\w+).*");

    public static String shortVersion(String fullVersion) {
        Matcher matcher = pattern.matcher(fullVersion);
        if(matcher.matches() && matcher.groupCount()==1) {
            return matcher.group(1);
        } else {
            return fullVersion;
        }
    }


}
