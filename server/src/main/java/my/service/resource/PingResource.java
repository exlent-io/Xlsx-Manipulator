package my.service.resource;



import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static tw.inspect.poi.HiKt.myMain;

@Path("/xlsxmanipulator/compose")
public class PingResource {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.WILDCARD)
    public Response compose() {
        Map<String, String> pong = new HashMap<>();
        pong.put("pong", "Hello, World!");

        myMain(new String[]{"", ""} );


        return Response.status(200).entity(pong).build();
    }
}