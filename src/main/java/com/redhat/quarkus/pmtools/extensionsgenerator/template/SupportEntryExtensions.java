package com.redhat.quarkus.pmtools.extensionsgenerator.template;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class SupportEntryExtensions {

    private static final Pattern pattern = Pattern.compile("^(\\d+\\.\\d+\\.\\d+\\.\\w+).*");

    public static String shortVersion(String fullVersion) {
        Matcher matcher = pattern.matcher(fullVersion);
        if(matcher.matches() && matcher.groupCount()==1) {
            return matcher.group(1);
        } else {
            return fullVersion;
        }
    }

    public static String sectionLink(String s) {
        int pointPosition = s.indexOf(".");
        if(pointPosition>0 ) {
            String majorVersion = s.substring(0,pointPosition);
            if(isInteger(majorVersion)) {
                String link = String.format("#RHBQ_%s",majorVersion);
                return String.format("[%s](%s)",s,link);
            }
        }
        return s;
    }



    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
}

