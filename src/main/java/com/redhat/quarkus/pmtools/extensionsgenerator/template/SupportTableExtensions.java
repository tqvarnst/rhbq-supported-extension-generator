package com.redhat.quarkus.pmtools.extensionsgenerator.template;

import com.redhat.quarkus.pmtools.extensionsgenerator.utils.AppConfig;
import com.redhat.quarkus.pmtools.extensionsgenerator.utils.ExtensionComparator;
import io.quarkus.qute.TemplateExtension;
import io.quarkus.registry.catalog.Extension;
import io.quarkus.registry.catalog.ExtensionCatalog;
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.*;
import java.util.stream.Collectors;

import static com.redhat.quarkus.pmtools.extensionsgenerator.template.SupportEntryExtensions.shortVersion;

@SuppressWarnings("unused")
@TemplateExtension(namespace = "support_table")
public class SupportTableExtensions {

    private static final Boolean SHOW_ONLY_SUPPORTED_EXTENSIONS = ConfigProvider.getConfig().getValue("pm-tool.supported-extensions-table.show-only-supported-extensions",Boolean.class);


    public static String render(List<ExtensionCatalog> extensionCatalogs, AppConfig appConfig) {
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
                    List<String> supportList = (List<String>) extensionOptional.get().getMetadata().get("redhat-support");
                    // For the product "redhat-support" there is never more than one value
                    if(supportList == null || supportList.size() == 0) {
                        str.append("-");
                    } else {
                        String supportHtml = supportList.stream().map(s -> supportStatusHtml(s, appConfig)).collect(Collectors.joining(","));
                        str.append(supportHtml);
                    }
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

     public static String supportStatusHtml(String metadata, AppConfig appConfig) {
        AppConfig.LabelConfig labelConfig = appConfig.label();
        AppConfig.LabelConfig.Label label = null;
        switch (metadata) {
            case "supported-in-jvm" -> label = labelConfig.supportedInJvm();
            case "supported" ->  label = labelConfig.supported();
            case "deprecated" ->  label = labelConfig.deprecated();
            case "tech-preview" ->  label = labelConfig.techPreview();
            case "dev-support" ->  label = labelConfig.devSupport();
            case "dev-preview" ->  label = labelConfig.devPreview();
            default -> label = labelConfig.unsupported();
        }
        return String.format("<font color='%s'><strong>%s/strong></font>",label.color(),label.classifier());
    }
}
