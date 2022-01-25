package com.redhat.quarkus.pmtools.extensionsgenerator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class TemplateExtensions {

    private static final Pattern pattern = Pattern.compile("^(\\d+\\.\\d+\\.\\d+).*");
    
    public static String printSupportStatusHTML(Extension extension) {
        List<String> tags = Arrays.asList(extension.tags);
        
        if(tags.contains("supported")) {
            return String.format("<font color='green'><strong>S</strong></font> [%s]('' '%s')",shortVersion(extension.version),extension.version);
        } else if(tags.contains("supported-in-jvm")) {
            return String.format("<font color='green'><strong>JVM</strong></font> [%s]('' '%s')",shortVersion(extension.version),extension.version);
        } else if(tags.contains("tech-preview")) {
            return String.format("<font color='orange'><strong>TP</strong></font> [%s]('' '%s')",shortVersion(extension.version),extension.version);
        } else if(tags.contains("dev-support")) {
            return String.format("<font color='blue'><strong>DEV</strong></font> [%s]('' '%s')",shortVersion(extension.version),extension.version);
        } else {
            return String.format("[%s]('' '%s')",shortVersion(extension.version),extension.version);
        }
    }

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
