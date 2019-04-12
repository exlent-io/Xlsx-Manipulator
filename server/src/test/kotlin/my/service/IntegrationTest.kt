package my.service

import my.service.StreamLambdaHandler.getResourceConfig
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import javax.ws.rs.core.UriBuilder


class IntegrationTest {

    @Disabled
    @Test
    fun runForFiveMinutes() {

        val baseUri = UriBuilder.fromUri("http://localhost/").port(3001).build()
        val resourceConfig = getResourceConfig()
        GrizzlyHttpServerFactory.createHttpServer(baseUri, resourceConfig)
        Thread.sleep(5 * 60 * 1000)
    }


}