package mainPackage.tmanager.config;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import io.swagger.jaxrs.config.BeanConfig;


public class SwaggerConfig extends Application {
    public SwaggerConfig() {
        // Swagger configuration
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.0");
        beanConfig.setBasePath("/api");

        beanConfig.setResourcePackage("mainPackage.tmanager.controllers");// Configuring the base package for controller scanning

        beanConfig.setResourcePackage("org.jazzteam");
        beanConfig.setScan(true);
    }

    @Override
    public Set<Class<?>> getClasses() {
        // Connecting Swagger
        Set<Class<?>> resources = new HashSet<>();
        resources.add(ApiListingResource.class);
        resources.add(SwaggerSerializers.class);
        return resources;
    }
}




