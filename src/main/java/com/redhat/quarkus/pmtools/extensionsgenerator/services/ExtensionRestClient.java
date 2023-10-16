package com.redhat.quarkus.pmtools.extensionsgenerator.services;

import com.redhat.quarkus.pmtools.extensionsgenerator.models.ExtensionList;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/extensions")
@RegisterRestClient(configKey="product-registry-client")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ExtensionRestClient {

    @Path("/all")
    @GET
    ExtensionList getAllExtensions();
}
