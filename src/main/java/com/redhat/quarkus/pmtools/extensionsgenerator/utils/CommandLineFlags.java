package com.redhat.quarkus.pmtools.extensionsgenerator.utils;

import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine;

import java.util.List;

@ApplicationScoped
public class CommandLineFlags {
    String outputFile;
    Boolean listExtensions = false;
    Boolean countExtensions = false;
    Boolean upstreamsRegistry = false;
    List<String> streams;
    String offering;
    Boolean noSpReleases = false;

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public Boolean getListExtensions() {
        return listExtensions;
    }

    public void setListExtensions(Boolean listExtensions) {
        this.listExtensions = listExtensions;
    }

    public Boolean getCountExtensions() {
        return countExtensions;
    }

    public void setCountExtensions(Boolean countExtensions) {
        this.countExtensions = countExtensions;
    }

    public Boolean getUpstreamsRegistry() {
        return upstreamsRegistry;
    }

    public void setUpstreamsRegistry(Boolean upstreamsRegistry) {
        this.upstreamsRegistry = upstreamsRegistry;
    }

    public List<String> getStreams() {
        return streams;
    }

    public void setStreams(List<String> streams) {
        this.streams = streams;
    }

    public Boolean getNoSpReleases() {
        return noSpReleases;
    }

    public void setNoSpReleases(Boolean noSpReleases) {
        this.noSpReleases = noSpReleases;
    }

    public String getOffering() {
        return offering;
    }

    public void setOffering(String offering) {
        this.offering = offering;
    }
}
