package my.service;

import com.amazonaws.serverless.proxy.jersey.JerseyLambdaContainerHandler;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class StreamLambdaHandler implements RequestStreamHandler {


//    static class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
//        final ObjectMapper objectMapper = new ObjectMapper()
//                .enable(SerializationFeature.INDENT_OUTPUT) // pretty-print
//                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // use iso-8601 for dates
//                .registerModule(new KotlinModule()); // this doesn't seem to be required to serialize our data class, but it is required to deserialize
//
//        @Override
//        public ObjectMapper getContext(Class<?> type) {
//            return objectMapper;
//
//        }
//    }

    public static ResourceConfig getResourceConfig() {
        return new ResourceConfig()
                .packages("my.service.resource")
                .register(JacksonFeature.class)
                //.register(ObjectMapperProvider.class)
                .register(new CorsFilter());
    }

    private static final ResourceConfig jerseyApplication = getResourceConfig();

    private static final JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler
            = JerseyLambdaContainerHandler.getAwsProxyHandler(jerseyApplication);

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {

//        String readLine;
//        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//
//        while (((readLine = br.readLine()) != null)) {
//            System.out.println(readLine);
//        }

        handler.proxyStream(inputStream, outputStream, context);

        // just in case it wasn't closed by the mapper
        outputStream.close();
    }
}