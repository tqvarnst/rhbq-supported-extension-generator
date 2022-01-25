package com.redhat.quarkus.pmtools.extensionsgenerator;


import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


@Path("/client")
@RegisterRestClient(configKey="registry-api")
public interface RegistryClientService {
    
    @Path("/platforms")
    @GET
    public Uni<Platforms> getPlatforms();

}
