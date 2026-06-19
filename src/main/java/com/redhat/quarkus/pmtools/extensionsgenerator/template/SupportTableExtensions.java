package com.redhat.quarkus.pmtools.extensionsgenerator.template;

import com.redhat.quarkus.pmtools.extensionsgenerator.utils.AppConfig;
import com.redhat.quarkus.pmtools.extensionsgenerator.utils.ExtensionComparator;
import io.quarkus.qute.TemplateExtension;
import io.quarkus.registry.catalog.Extension;
import io.quarkus.registry.catalog.ExtensionCatalog;
import io.quarkus.registry.catalog.ExtensionOrigin;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.stream.Collectors;

import static com.redhat.quarkus.pmtools.extensionsgenerator.template.SupportEntryExtensions.shortVersion;

@SuppressWarnings("unused")
@TemplateExtension(namespace = "support_table")
public class SupportTableExtensions {

    private static final Logger log = Logger.getLogger(SupportTableExtensions.class.getName());

//    private static final Boolean SHOW_ONLY_SUPPORTED_EXTENSIONS = ConfigProvider.getConfig().getValue("pm-tool.supported-extensions-table.show-only-supported-extensions",Boolean.class);


    public static String render(List<ExtensionCatalog> extensionCatalogs, AppConfig appConfig, String offering) {
        StringBuilder str = new StringBuilder();
        str.append("| Artifact |");
        extensionCatalogs.forEach(extensionCatalog -> str.append(" ").append(shortVersion(extensionCatalog.getBom().getVersion())).append(" |"));
        str.append(System.lineSeparator());
        str.append("| ----- |");
        extensionCatalogs.forEach(e -> str.append(" ----- |"));
        str.append(System.lineSeparator());

        Set<String> extensions = getUniqueAndSortedExtensions(extensionCatalogs,appConfig,offering);
        extensions.forEach(id -> {
            str.append("| ").append(id).append(" |");
            extensionCatalogs.forEach(extensionCatalog -> {
                str.append(" ");
                Optional<Extension> extensionOptional = extensionCatalog.getExtensions().stream().filter(extension -> {
                    String currentId = extension.getArtifact().getArtifactId();
                    return id.equals(currentId);
                }).findFirst();
                if(extensionOptional.isPresent()) {
                    Extension e = extensionOptional.get();

                    if(e.getArtifact().getArtifactId().equals("camel-quarkus-core") || e.getArtifact().getArtifactId().equals("quarkus-cxf")) {
                        log.infof("%s:%s",e.getArtifact().getArtifactId(),e.getOrigins().stream().map(ExtensionOrigin::getId).collect(Collectors.joining(",")));
                        log.infof("%s:%s [%s]",e.getArtifact().getArtifactId(),e.getArtifact().getVersion(),e.getMetadata().toString());

                    }

                    List<String> supportList = (List<String>) e.getMetadata().get(offering + "-support");
                    // For backward compatibility with previous versions we will fall back to the ´redhat-support´ tag if there isn't any value for offering + "-support"


                    if(supportList == null || supportList.isEmpty()) {
                        supportList = (List<String>) e.getMetadata().get("redhat-support");
                    }

                    if(supportList == null || supportList.isEmpty()) {
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

    private static Set<String> getUniqueAndSortedExtensions(List<ExtensionCatalog> extensionCatalogList,AppConfig appConfig, String offering) {
        Set<String> extensions = new TreeSet<>(new ExtensionComparator());
        Logger log = Logger.getLogger("default");
        extensionCatalogList.forEach(
                extensionCatalog -> extensionCatalog.getExtensions().forEach(extension -> {
                    String id = extension.getArtifact().getArtifactId();
                    //ExtensionOrigin extensionOrigin = extension.getOrigins().getFirst();
                    if(!appConfig.showOnlySupportedExtensions()) {
                        extensions.add(id);
                    } else if(hasSupportMetadata(extension,offering)) {
                        extensions.add(id);
                    }
                })
        );
        return extensions;
    }

    private static boolean hasSupportMetadata(Extension e, String offering) {
        List<String> metadata = (List<String>) e.getMetadata().getOrDefault(offering + "-support",e.getMetadata().getOrDefault("redhat-support",List.of("unsupported")));
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
        return String.format("<font color='%s'><strong>%s</strong></font>",label.color(),label.classifier());
    }
}
