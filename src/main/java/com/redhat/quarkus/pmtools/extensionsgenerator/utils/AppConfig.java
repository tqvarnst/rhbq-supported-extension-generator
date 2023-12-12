package com.redhat.quarkus.pmtools.extensionsgenerator.utils;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix="app")
public interface AppConfig {


    @WithName("show-only-supported-extensions")
    Boolean showOnlySupportedExtensions();
    @WithName("label")
    LabelConfig label();

    interface LabelConfig {

        @WithName("supported")
        Label supported();

        @WithName("supported-in-jvm")
        Label supportedInJvm();

        @WithName("deprecated")
        Label deprecated();

        @WithName("tech-preview")
        Label techPreview();

        @WithName("dev-support")
        Label devSupport();

        @WithName("dev-preview")
        Label devPreview();

        @WithName("unsupported")
        Label unsupported();


        interface Label {
            String color();
            String classifier();
        }
    }
}
