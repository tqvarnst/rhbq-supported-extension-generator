package com.redhat.quarkus.pmtools.extensionsgenerator.models;

@SuppressWarnings("unused")
public class Extension {

    String name;
    String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Extension{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
