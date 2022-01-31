package com.redhat.quarkus.pmtools.extensionsgenerator.template;

import com.redhat.quarkus.pmtools.extensionsgenerator.utils.VersionUtils;
import io.quarkus.logging.Log;
import io.quarkus.qute.TemplateExtension;
import io.quarkus.registry.catalog.ExtensionCatalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
@TemplateExtension
public class SupportEntryExtensions {

    //com.redhat.quarkus.platform:quarkus-bom-quarkus-platform-descriptor:2.2.3.SP2-redhat-00001:json:2.2.3.SP2-redhat-00001
    //com.redhat.quarkus.platform:quarkus-camel-bom-quarkus-platform-descriptor:2.2.3.SP2-redhat-00001:json:2.2.3.SP2-redhat-00001
    private static final Pattern memberNamePattern = Pattern.compile("^.*:(.*)-bom.*");




    public static String shortVersion(String fullVersion) {
        return VersionUtils.shortVersion(fullVersion);
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

    public static String memberName(String member) {
        Matcher matcher = memberNamePattern.matcher(member);
        if(matcher.matches() && matcher.groupCount()==1) {
            return matcher.group(1);
        } else {
            return member;
        }
    }

    public static List<String> members(ExtensionCatalog ec) {
        Map<String,Object> metaData = ec.getMetadata();
        Log.debugf("Meta data for %s contains:%n%s",ec.getBom().getVersion(),metaData);
        Log.debugf("Meta data contains members %b ",metaData.containsKey("members"));
        Log.debugf("Meta data contains %s",metaData.keySet().toString());
        Log.debugf("Metadata length %d", metaData.keySet().size());
        if(metaData.containsKey("platform-release")) {
            Object platformRelease = metaData.get("platform-release");
            if(platformRelease instanceof Map) {
                Object members = ((Map<String, Object>) platformRelease).get("members");
                if(members instanceof Collection) {
                    return new ArrayList<>(((Collection<String>) members));
                }
            }
        }
        return null;
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

