package test.dropwizard.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/manager")
public class HelloResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String greeting() {
        return "HelloResource world!";
    }

    @Path("/greetings")
    public HelloSubResource getHelloSubResource() {
        return new HelloSubResource();
    }
}
