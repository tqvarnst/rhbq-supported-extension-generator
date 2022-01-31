package com.redhat.quarkus.pmtools.extensionsgenerator.model;

import com.redhat.quarkus.pmtools.extensionsgenerator.utils.VersionUtils;
import io.quarkus.registry.catalog.ExtensionCatalog;

import java.util.ArrayList;
import java.util.Map;

public class PlatformFactory {
    public static Platform create(ExtensionCatalog ec) {
        Platform platform = new Platform();
        platform.setFullVersion(ec.getBom().getVersion());
        platform.setShortVersion(VersionUtils.shortVersion(platform.getFullVersion()));
        platform.setQuarkusCoreVersion(ec.getQuarkusCoreVersion());
        platform.setUpstreamQuarkusVersion(ec.getUpstreamQuarkusCoreVersion());
        platform.setExtensions(new ArrayList<>(ec.getExtensions()));
        Map<String, Object> metaData = (Map<String, Object>) ec.getMetadata().get("platform-release");
        platform.setPlatformMetadata(PlatformMetadataFactory.create(metaData));
        return platform;
    }
}
