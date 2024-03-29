package com.redhat.quarkus.pmtools.extensionsgenerator;

import com.redhat.quarkus.pmtools.extensionsgenerator.models.Extension;
import com.redhat.quarkus.pmtools.extensionsgenerator.models.ExtensionList;
import com.redhat.quarkus.pmtools.extensionsgenerator.services.CommunityExtensionRestClient;
import com.redhat.quarkus.pmtools.extensionsgenerator.services.ExtensionCatalogService;
import com.redhat.quarkus.pmtools.extensionsgenerator.services.ExtensionRestClient;
import com.redhat.quarkus.pmtools.extensionsgenerator.services.PlatformVersionService;
import com.redhat.quarkus.pmtools.extensionsgenerator.utils.AppConfig;
import com.redhat.quarkus.pmtools.extensionsgenerator.utils.ExtensionCatalogComparator;
import com.redhat.quarkus.pmtools.extensionsgenerator.utils.VersionUtils;
import io.quarkus.logging.Log;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.registry.catalog.ExtensionCatalog;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import jakarta.inject.Inject;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@TopCommand
@Command(mixinStandardHelpOptions = true)
@SuppressWarnings("unused")
public class MainCommand implements Runnable {

    @Option(names = {"-o", "--output"}, description = "The file to write the generated mark down to, leave empty to print to console", defaultValue = "unspecified")
    String outputFile;

    @Option(names = {"-t", "--timeout"}, description = "Increases the timeout waiting for remote registries. Unit it seconds and the default timeout is 30 seconds", defaultValue = "30")
    int timoutSeconds;

    @Option(names = {"-l", "--list-extesions"})
    Boolean listExtensions = false;

    @Option(names = {"-c", "--count-extesions"})
    Boolean countExtensions = false;

    @Option(names = {"-u", "--use-upstream-registry"})
    Boolean upstreamsRegistry = false;

    @Option(names = { "-S", "--stream"}, description = "Specify which streams to use. Use multiple times for multiple stream, e.g. -S 2.13 -S 2.7. At least one stream must be set and you can not combine streams from different major versions", required = true)
    List<String> streams;

    @Option(names = {"--no-sp"}, description = "Filter out SP releases")
    Boolean noSpReleases = false;


    @Inject
    PlatformVersionService platformVersionService;

    @Inject
    ExtensionCatalogService extensionCatalogService;

    @Inject
    @RestClient
    ExtensionRestClient extensionRestClient;

    @Inject
    @RestClient
    CommunityExtensionRestClient communityExtensionRestClient;

    @SuppressWarnings("all")
    @Inject
    Template output;

    @Inject
    AppConfig appConfig;


    @Override
    public void run() {

        if (listExtensions) {
            if(outputFile.equals("")) {
                System.out.println("You cannot set output file when listing extensions");
                System.exit(0);
            }
            listProductExtensions();
        } else {
            if(countExtensions || upstreamsRegistry) {
                if (countExtensions) {
                    System.out.println("You can only use -c,--count-extensions option in combination with -l,--list-extension");
                }
                if (upstreamsRegistry) {
                    System.out.println("You can only use -u,--use-upstream-registry option in combination with -l,--list-extension");
                }
                System.exit(0);
            }
            listPlatformsAndGenerateMarkdown();
        }


    }

    private void listPlatformsAndGenerateMarkdown() {

        boolean isStreamFilterActive= (streams!=null);

        List<String> platformVersions;
        String mainVersion;
        try {
            platformVersions = platformVersionService.getVersions();

            if(isStreamFilterActive) {
                platformVersions = platformVersions.stream().filter(s -> streamFilterMatch(s)).toList();
            }

            if(noSpReleases) {
                platformVersions = platformVersions.stream().filter(s -> !s.contains("SP")).toList();
            }


            Set<String> mainVersionSet = platformVersions.stream().map(s -> VersionUtils.mainVersion(s)).collect(Collectors.toSet());

            if(mainVersionSet.size()>1) {
                throw new Exception("Cannot create a page with multiple main versions on the same page. Aborting");
            }

            mainVersion = mainVersionSet.stream().findAny().orElseThrow();


        } catch (Exception e) {
            Log.debug(e.getMessage(), e);
            System.out.println("Failed with error message " + e.getMessage());
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


        TemplateInstance data = output.data("extensionCatalogs", extensionCatalogs)
                .data("mainVersion",mainVersion)
                .data("appConfig",appConfig);

        if ("unspecified".equals(outputFile)) {
            System.out.println("=========== Output =============");
            System.out.println(data.render());
            System.out.println("================================");
        } else {
            System.out.printf("Saving the generated content into the markdown to file %s%n", outputFile);
            writeToFile(data.render());
            System.out.println("DONE!");
        }
    }

    private boolean streamFilterMatch(String version) {
        boolean isPartOfRequestStreams = false;
        for (String requestedStream : streams) {
            if (version.startsWith(requestedStream)) {
                isPartOfRequestStreams = true;
            }
        }
        return isPartOfRequestStreams;
    }

    private void listProductExtensions() {
        ExtensionList extensionList = upstreamsRegistry ? communityExtensionRestClient.getAllExtensions() : extensionRestClient.getAllExtensions();
        if (countExtensions) {
            System.out.println("Number of extensions for latest version is " + extensionList.getExtensions().size());
        } else {
            extensionList.getExtensions().stream().map(Extension::getName).forEach(System.out::println);
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
