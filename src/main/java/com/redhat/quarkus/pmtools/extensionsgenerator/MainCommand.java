package com.redhat.quarkus.pmtools.extensionsgenerator;

import com.redhat.quarkus.pmtools.extensionsgenerator.services.ExtensionCatalogService;
import com.redhat.quarkus.pmtools.extensionsgenerator.services.PlatformVersionService;
import com.redhat.quarkus.pmtools.extensionsgenerator.utils.ExtensionCatalogComparator;
import io.quarkus.logging.Log;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.registry.catalog.ExtensionCatalog;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@TopCommand
@Command(mixinStandardHelpOptions = true)
@SuppressWarnings("unused")
public class MainCommand implements Runnable {

    @Option(names = {"-o","--output"}, description = "The file to write the generated mark down to, leave empty to print to console", defaultValue = "unspecified")
    String outputFile;

    @Option(names = {"-t","--timeout"}, description = "Increases the timeout waiting for remote registries. Unit it seconds and the default timeout is 30 seconds", defaultValue = "30")
    int timoutSeconds;



    @Inject
    PlatformVersionService platformVersionService;

    @Inject
    ExtensionCatalogService extensionCatalogService;

    @SuppressWarnings("all")
    @Inject
    Template output;


    @Override
    public void run() {
        List<String> platformVersions;
        try {
            platformVersions = platformVersionService.getVersions(timoutSeconds);
        } catch (Exception e) {
            Log.debug(e);
            return;
        }

        Log.debug("Sucessfully retrieved platform versions");
        Uni<List<ExtensionCatalog>> extensionCatalogListUni = Multi.createFrom().iterable(platformVersions)
                .onItem()
                .transformToUniAndMerge(v -> extensionCatalogService.getExtensionCatalogForVersion(v))
                .collect().asList();

        List<ExtensionCatalog> extensionCatalogs = extensionCatalogListUni
                .await()
                .atMost(Duration.ofSeconds(timoutSeconds))
                .stream()
                    .sorted(new ExtensionCatalogComparator())
                .collect(Collectors.toList());


        TemplateInstance data = output.data("extensionCatalogs",extensionCatalogs);

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


    private void writeToFile(String output) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
