package my.service.resource;


import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static tw.inspect.poi.HiKt.digestFromBase64;

@Path("/xlsxmanipulator/compose")
public class PingResource {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response compose(final ComposeRequest composeRequest) {

        Map<String, String> pong = new HashMap<>();

        System.out.println(composeRequest.sections.size());
        try {
            pong.put("out", digestFromBase64(composeRequest.templateBase64, composeRequest.sections));
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
        return Response.status(200).entity(pong).build();
    }
}