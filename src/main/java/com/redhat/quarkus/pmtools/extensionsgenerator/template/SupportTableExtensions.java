package com.redhat.quarkus.pmtools.extensionsgenerator.template;

import com.redhat.quarkus.pmtools.extensionsgenerator.utils.ExtensionComparator;
import io.quarkus.qute.TemplateExtension;
import io.quarkus.registry.catalog.Extension;
import io.quarkus.registry.catalog.ExtensionCatalog;
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.*;

import static com.redhat.quarkus.pmtools.extensionsgenerator.template.SupportEntryExtensions.shortVersion;

@SuppressWarnings("unused")
@TemplateExtension(namespace = "support_table")
public class SupportTableExtensions {

    private static final Boolean SHOW_ONLY_SUPPORTED_EXTENSIONS = ConfigProvider.getConfig().getValue("pm-tool.supported-extensions-table.show-only-supported-extensions",Boolean.class);


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
        Set<String> extensions = new TreeSet<>(new ExtensionComparator());
        extensionCatalogList.forEach(
                extensionCatalog -> extensionCatalog.getExtensions().forEach(extension -> {
                    String id = extension.getArtifact().getArtifactId();
                    if(!SHOW_ONLY_SUPPORTED_EXTENSIONS) {
                        extensions.add(id);
                    } else if(hasSupportMetadata(extension)) {
                        extensions.add(id);
                    }
                })
        );
        return extensions;
    }

    private static boolean hasSupportMetadata(Extension e) {
        List<String> metadata = (List<String>) e.getMetadata().get("redhat-support");
        return metadata != null && !metadata.contains("unsupported");

    }

    private static String printSupportStatusHTML(Extension extension) {
        @SuppressWarnings("unchecked")
        List<String> metadata = (List<String>) extension.getMetadata().get("redhat-support");
        String version = extension.getArtifact().getVersion();

        if(metadata!=null) {
            if (metadata.contains("supported-in-jvm")) {
                return String.format("<font color='green'><strong>JVM</strong></font>");
            } else if (metadata.contains("supported")) {
                return String.format("<font color='green'><strong>S</strong></font>");
            } else if (metadata.contains("deprecated")) {
                return String.format("<font color='red'><strong>DEP</strong></font>");
            } else if (metadata.contains("tech-preview")) {
                return String.format("<font color='orange'><strong>TP</strong></font>");
            } else if (metadata.contains("dev-support")) {
                return String.format("<font color='blue'><strong>DEV</strong></font>");
            } else if (metadata.contains("dev-preview")) {
                return String.format("<font color='teal'><strong>DP</strong></font>");
            } else if (metadata.contains("unsupported")) {
                return " - ";
            } else {
                return metadata.toString();
            }
        }
        return " - ";
    }
}
