package com.redhat.quarkus.pmtools.extensionsgenerator.services;

import com.redhat.quarkus.pmtools.extensionsgenerator.models.ExtensionList;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/extensions")
@RegisterRestClient(configKey="community-registry-client")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface CommunityExtensionRestClient {

    @Path("/all")
    @GET
    ExtensionList getAllExtensions();
}
