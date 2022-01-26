package com.redhat.quarkus.pmtools.extensionsgenerator.template;

import io.quarkus.qute.TemplateExtension;
import io.quarkus.registry.catalog.Extension;
import io.quarkus.registry.catalog.ExtensionCatalog;

import java.util.*;

import static com.redhat.quarkus.pmtools.extensionsgenerator.template.SupportEntryExtensions.shortVersion;

@TemplateExtension(namespace = "support_table")
public class SupportTableExtensions {
    static String render(List<ExtensionCatalog> extensionCatalogs) {
        StringBuilder str = new StringBuilder();
        str.append("| Artifact | ");
        extensionCatalogs.forEach(extensionCatalog -> str.append(shortVersion(extensionCatalog.getBom().getVersion())).append(" | "));
        str.append(System.lineSeparator());
        str.append("| ----- |");
        extensionCatalogs.forEach(e -> str.append(" ----- |"));
        str.append(System.lineSeparator());

        Set<String> extensions = getUniqueAndSortedExtensions(extensionCatalogs);
        extensions.forEach(id -> {
            str.append("| ").append(id).append(" |");
            extensionCatalogs.forEach(extensionCatalog -> {
                str.append(" ");
                Optional<Extension> extensionOptional = extensionCatalog.getExtensions().stream().filter(extension -> {
                    String currentId = extension.getArtifact().getArtifactId();
                    return id.equals(currentId);
                }).findFirst();
                if(extensionOptional.isPresent()) {
                    str.append(printSupportStatusHTML(extensionOptional.get()));
                } else {
                    str.append("-");
                }
                str.append(" |");
            });
            str.append(System.lineSeparator());
        });
        return str.toString();
    }

    private static Set<String> getUniqueAndSortedExtensions(List<ExtensionCatalog> extensionCatalogList) {
        Set<String> extensions = new TreeSet<>();
        extensionCatalogList.forEach(
                extensionCatalog -> extensionCatalog.getExtensions().forEach(extension -> {
                    if(extension.getMetadata().get("redhat-support")!=null && extension.getArtifact().getVersion().contains("-redhat-")) {
                        String id = extension.getArtifact().getArtifactId();
                        extensions.add(id);
                    }
                })
        );
        return extensions;
    }
    private static String printSupportStatusHTML(Extension extension) {
        @SuppressWarnings("unchecked")
        List<String> metadata = (List<String>) extension.getMetadata().get("redhat-support");
        String version = extension.getArtifact().getVersion();

        if(metadata!=null) {
            if (metadata.contains("supported-in-jvm")) {
                return String.format("[<font color='green'><strong>JVM</strong></font>]('' '%s')", version);
            } else if (metadata.contains("supported")) {
                return String.format("[<font color='green'><strong>S</strong></font>]('' '%s')", version);
            } else if (metadata.contains("tech-preview")) {
                return String.format("[<font color='orange'><strong>TP</strong></font>]('' '%s')", version);
            } else if (metadata.contains("dev-support")) {
                return String.format("[<font color='blue'><strong>DEV</strong></font>]('' '%s')", version);
            }
        }
        return " - ";
    }
}
