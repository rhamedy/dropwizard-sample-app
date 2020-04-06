package test.dropwizard.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class HelloSubSubResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getGreeting() {
        return "HelloSubSubResource world!";
    }
}
