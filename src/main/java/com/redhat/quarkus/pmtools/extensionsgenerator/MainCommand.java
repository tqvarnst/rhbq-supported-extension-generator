package com.redhat.quarkus.pmtools.extensionsgenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.redhat.quarkus.pmtools.extensionsgenerator.services.PlatformVersionService;
import com.redhat.quarkus.pmtools.extensionsgenerator.services.ExtensionCatalogService;
import com.redhat.quarkus.pmtools.extensionsgenerator.utils.ExtensionCatalogComparator;
import com.redhat.quarkus.pmtools.extensionsgenerator.utils.VersionComparator;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.registry.catalog.ExtensionCatalog;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@TopCommand
@Command(mixinStandardHelpOptions = true)
public class MainCommand implements Runnable {

    @Option(names = {"-o","--output"}, description = "The file to write the generated mark down to, leave empty to print to console", defaultValue = "unspecified")
    String outputFile;

    @Inject
    PlatformVersionService bomVersionService;

    @Inject
    ExtensionCatalogService extensionCatalogService;

    @SuppressWarnings("all")
    @Inject
    Template output;


    @Override
    public void run() {
        Uni<List<String>> bomVersionsUni = bomVersionService.getVersions();


        List<String> platformVersions = bomVersionsUni.await()
                .atMost(Duration.ofSeconds(30))
                .stream()
                    .filter(v -> !v.startsWith("1."))
                    .sorted(new VersionComparator())
                .collect(Collectors.toList());

        Uni<List<ExtensionCatalog>> extensionCatalogListUni = Multi.createFrom().iterable(platformVersions)
                .onItem()
                .transformToUniAndMerge(v -> extensionCatalogService.getExtensionCatalogForVersion(v))
                .collect().asList();

        List<ExtensionCatalog> extensionCatalogs = extensionCatalogListUni
                .await()
                .atMost(Duration.ofSeconds(30))
                .stream()
                    .sorted(new ExtensionCatalogComparator())
                .collect(Collectors.toList());


        TemplateInstance data = output
                .data("extensionCatalogs",extensionCatalogs)
                .data("platformVersions",platformVersions);

        if("unspecified".equals(outputFile)) {
            System.out.println("=========== Output =============");
            System.out.println(data.render());
            System.out.println("================================");
        } else {
            System.out.printf("Saving the generated content into the markdown to file %s%n",outputFile);
            writeToFile(data.render());
            System.out.println("DONE!");
        }
    }

//    private static String getShortVersionFromFullVersion(String fullVersion) {
//        return fullVersion.contains("-redhat") ? fullVersion.split("-redhat")[0] : fullVersion;
//    }

    private void writeToFile(String output) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
