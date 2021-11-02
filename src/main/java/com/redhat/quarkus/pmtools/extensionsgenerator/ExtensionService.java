package com.redhat.quarkus.pmtools.extensionsgenerator;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/extensions/stream")
@RegisterRestClient(configKey="extension-api")
public interface ExtensionService {

    @GET
    @Path("/{streamKey}")
    List<Extension> getByStream(@PathParam("streamKey") String stream);
}