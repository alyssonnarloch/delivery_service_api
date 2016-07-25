package com.api.delivery_service_api.auth;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class FilterAuthentication implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext containerRequest) throws IOException {
        
        String path = containerRequest.getUriInfo().getPath();
        
        String token = containerRequest.getHeaderString("Authorization");
        
        System.out.println(token);
        System.out.println("UIAAAAAQAAA");
    }

}
