package my.service.resource;



import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static tw.inspect.poi.HiKt.myMain;

@Path("/ping")
public class PingResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.WILDCARD)
    public Response createPet() {
        Map<String, String> pong = new HashMap<>();
        pong.put("pong", "Hello, World!");

        myMain(new String[]{"", ""} );


        return Response.status(200).entity(pong).build();
    }
}