package com.redhat.quarkus.pmtools.extensionsgenerator.services;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/ga/com/redhat/quarkus/platform/quarkus-bom")
@RegisterRestClient(configKey="platform-version")
@Produces(MediaType.TEXT_PLAIN)
public interface PlatformVersionRestClient {

    @Path("/")
    @GET
    Uni<String> getPlatformVersionHTML();
}
