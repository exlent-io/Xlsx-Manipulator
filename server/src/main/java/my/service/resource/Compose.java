package my.service.resource;


import my.service.models.ComposeRequest;
import tw.inspect.googledriver.GoogleDriverKt;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static my.service.ErrorPrinter.getStackTraceString;
import static tw.inspect.poi.HiKt.digestFromInputStream;

@Path("/xlsx/compose")
public class Compose {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response compose(final ComposeRequest composeRequest) {

        Map<String, String> pong = new HashMap<>();

        System.out.println(composeRequest.sections.size());
        try {
            final InputStream inputStream = (composeRequest.templateUrl != null) ?
                    new BufferedInputStream(new URL(composeRequest.templateUrl).openStream())
                    : new ByteArrayInputStream(Base64.getDecoder().decode(composeRequest.templateBase64));

            final byte[] out = digestFromInputStream(inputStream, composeRequest.sections).toByteArray();
            if (composeRequest.gdrivePath != null && composeRequest.gdriveFilename != null) {
                try {

                    final String fileId = GoogleDriverKt.uploadFile(
                            GoogleDriverKt.getDriveService(),
                            composeRequest.gdriveFilename,
                            composeRequest.gdrivePath,
                            new ByteArrayInputStream(out)
                    );
                    pong.put("file_id", fileId);
                } catch (Exception e) {
                    pong.put("file_id", null);
                    pong.put("exception", e.getMessage());
                    e.printStackTrace();
                }
            }
            pong.put("out", new String(Base64.getEncoder().encode(out)));

        } catch (Throwable t) {
            t.printStackTrace();
            return Response.status(500).entity(getStackTraceString(t)).build();
        }
        return Response.status(200).entity(pong).build();
    }
}