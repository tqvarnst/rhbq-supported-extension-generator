package com.redhat.quarkus.pmtools.extensionsgenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import io.quarkus.logging.Log;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@TopCommand
@Command(mixinStandardHelpOptions = true)
public class MainCommand implements Runnable {

    @Option(names = {"-S","--stream"}, description = "The stream (X.Y) to generate the supported artifacts for", defaultValue = "unspecified")
    String stream;

    @Option(names = {"-n","--full-version"}, description = "The exact full version to", defaultValue = "unspecified")
    String version;

    @Option(names = {"-o","--output"}, description = "The file to write the generated mark down to, leave empty to print to console", defaultValue = "unspecified")
    String outputFile;

    @Option(names = {"-l","--latest"}, description = "Use the latest exact version in a stream")
    boolean latestVersion;

    @Inject
    ExtentionHelper extHelper;

    @Inject
    RegistryClientHelper registryHelper;

    @Inject
    Template output;


    @Override
    public void run() {
        List<String> availableStreams = registryHelper.getAvailableStreams();

        // If there is only one available stream we will default to use that one.
        if(availableStreams.size()==1 && "unspecified".equals(stream)) {
            stream=availableStreams.get(0);
            System.out.printf("Only one available stream (%s) so generating the output based on that stream\n",stream);
        }


        while(!availableStreams.contains(stream)) {
            System.out.println("Available streams are:");
            availableStreams.forEach(s -> System.out.println(String.format(" - %s", s)));
            stream = System.console().readLine("Please enter the stream to use? ");
        }

        List<String> exactVersions = registryHelper.getExactVersions();




        String fullVersion = "";
        if(latestVersion || exactVersions.size()==1) {
            fullVersion = exactVersions.get(0);
        } else {
            while(!exactVersions.contains(fullVersion)) {
                System.out.println(String.format("Available versions in stream %s are:", stream));
                exactVersions.forEach(v -> System.out.println(String.format(" - %s", v)));
                fullVersion = stream = System.console().readLine("Please enter the full version to use? ");
            }
        }
        String shortVersion = getShortVersionFromFullVersion(fullVersion);
 

        TemplateInstance data = output.data("supportedExtensions",extHelper.getSupportedExtensions(stream))
                                    .data("supportedInJvmExtensions",extHelper.getSupportedInJVMExtensions(stream))
                                    .data("techpreviewExtensions",extHelper.getTechpreviewExtensions(stream))
                                    .data("devSupportedExtensions",extHelper.getDevSupportedExtensions(stream))
                                    .data("productExtensions",extHelper.getProductExtensions(stream))
                                    .data("shortVersion",shortVersion)
                                    .data("fullVersion",fullVersion);

        if("unspecified".equals(outputFile)) {
            System.out.println("=========== Output =============");
            System.out.println(data.render());
            System.out.println("================================");
        } else {
            System.out.println(String.format("Saving the generated content into the markdown to file %s",outputFile));
            writeToFile(data.render()); 
            System.out.println("DONE!");
        }
    }

    private static String getShortVersionFromFullVersion(String fullVersion) {
        return fullVersion.contains("-redhat") ? fullVersion.split("-redhat")[0] : fullVersion;
    }

    private void writeToFile(String output) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null)
                    writer.close();
            } catch (IOException e) {}
        }
    }
}
