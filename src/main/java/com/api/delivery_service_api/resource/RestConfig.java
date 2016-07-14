package com.api.delivery_service_api.resource;

import java.util.Set;
import javax.ws.rs.core.Application;

@javax.ws.rs.ApplicationPath("api")
public class RestConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.api.delivery_service_api.resource.AuthResource.class);
        resources.add(com.api.delivery_service_api.resource.CityResource.class);
        resources.add(com.api.delivery_service_api.resource.ClientResource.class);
        resources.add(com.api.delivery_service_api.resource.ClientServiceProviderFavoriteResource.class);
        resources.add(com.api.delivery_service_api.resource.ServiceProviderResource.class);
        resources.add(com.api.delivery_service_api.resource.ServiceTypeResource.class);
        resources.add(com.api.delivery_service_api.resource.projectResource.class);
    }
    
}
