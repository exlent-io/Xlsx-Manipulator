package my.service.resource;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static my.service.ErrorPrinter.getStackTraceString;
import static tw.inspect.googledriver.GoogleDriverKt.getMail;

@Path("/gserviceaccount")
public class ServiceMail {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {

        Map<String, String> pong = new HashMap<>();

        try {
            pong.put("mail", getMail());
        } catch (Throwable t) {
            t.printStackTrace();
            return Response.status(500).entity(getStackTraceString(t)).build();
        }
        return Response.status(200).entity(pong).build();
    }
}