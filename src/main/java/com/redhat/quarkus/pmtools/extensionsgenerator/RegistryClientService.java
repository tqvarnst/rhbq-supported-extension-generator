package com.redhat.quarkus.pmtools.extensionsgenerator;


import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


@Path("/client")
@RegisterRestClient(configKey="registry-api")
public interface RegistryClientService {
    
    @Path("/platforms")
    @GET
    public Platforms getPlatforms();

}
