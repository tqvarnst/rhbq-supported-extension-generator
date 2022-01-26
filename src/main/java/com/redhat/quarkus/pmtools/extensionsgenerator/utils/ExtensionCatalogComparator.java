package com.redhat.quarkus.pmtools.extensionsgenerator.utils;

import io.quarkus.registry.catalog.ExtensionCatalog;

import java.util.Comparator;

public class ExtensionCatalogComparator implements Comparator<ExtensionCatalog> {

    VersionComparator versionComparator = new VersionComparator();

    @Override
    public int compare(ExtensionCatalog ec1, ExtensionCatalog ec2) {
        return versionComparator.compare(ec1.getBom().getVersion(),ec2.getBom().getVersion());
    }
}
