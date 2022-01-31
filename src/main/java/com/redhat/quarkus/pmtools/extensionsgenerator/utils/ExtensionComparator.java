package com.redhat.quarkus.pmtools.extensionsgenerator.utils;

import java.util.Comparator;


/**
 * Comparator to place camel extensions below quarkus extensions
 */
@SuppressWarnings("unused")
public class ExtensionComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        if(o1.startsWith("camel") && o2.startsWith("quarkus")) {
            return 1;
        } else if(o1.startsWith("quarkus") && o2.startsWith("camel")) {
            return -1;
        } else {
            return o1.compareTo(o2);
        }
    }
}
