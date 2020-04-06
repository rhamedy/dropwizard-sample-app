package test.dropwizard.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class HelloSubResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String greeting() {
        return "HelloSubResource world!";
    }

    @Path("/{greetingId}/sub-greetings")
    public HelloSubSubResource getHelloSubSubResource(@PathParam("greetingId") final Integer greetingId) {
        return new HelloSubSubResource();
    }
}
