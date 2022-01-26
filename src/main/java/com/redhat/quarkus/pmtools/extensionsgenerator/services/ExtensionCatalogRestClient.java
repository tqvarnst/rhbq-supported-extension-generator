package com.redhat.quarkus.pmtools.extensionsgenerator.services;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/ga/com/redhat/quarkus/platform/quarkus-bom-quarkus-platform-descriptor")
@RegisterRestClient(configKey = "extension-catalog")
@Produces(MediaType.TEXT_PLAIN)
public interface ExtensionCatalogRestClient {
    @GET
    @Path("/{version1}/quarkus-bom-quarkus-platform-descriptor-{version2}-{version3}.json")
    Uni<String> getExtensionCatalogJsonFromVersion(@PathParam("version1") String version1,
                                                          @PathParam("version2") String version2,
                                                          @PathParam("version3") String version3);
}
