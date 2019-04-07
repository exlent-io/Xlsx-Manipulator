package my.service


import com.amazonaws.serverless.proxy.internal.LambdaContainerHandler
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext
import com.amazonaws.serverless.proxy.model.AwsProxyResponse
import com.amazonaws.services.lambda.runtime.Context

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import javax.ws.rs.HttpMethod
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


class StreamLambdaHandlerTest {

    @Test
    fun ping_streamRequest_respondsWithHello() {
        val requestStream = AwsProxyRequestBuilder("/xlsxmanipulator/compose", HttpMethod.POST)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .buildStream()
        val responseStream = ByteArrayOutputStream()

        handle(requestStream, responseStream)

        val response = readResponse(responseStream)
        assertNotNull(response)
        assertEquals(Response.Status.OK.statusCode.toLong(), response!!.statusCode.toLong())

        assertFalse(response.isBase64Encoded)

        assertTrue(response.body.contains("pong"))
        assertTrue(response.body.contains("Hello, World!"))

        assertTrue(response.multiValueHeaders.containsKey(HttpHeaders.CONTENT_TYPE))

        assertTrue(response.multiValueHeaders[HttpHeaders.CONTENT_TYPE]!!.fold(false) { a, b ->
            a || b.startsWith(
                MediaType.APPLICATION_JSON
            )
        })
    }

    private fun handle(`is`: InputStream, os: ByteArrayOutputStream) {
        try {
            handler!!.handleRequest(`is`, os, lambdaContext)
        } catch (e: IOException) {
            e.printStackTrace()
            fail<Exception>(e.message)
        }

    }

    private fun readResponse(responseStream: ByteArrayOutputStream): AwsProxyResponse? {
        try {
            return LambdaContainerHandler.getObjectMapper()
                .readValue(responseStream.toByteArray(), AwsProxyResponse::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            fail<Exception>("Error while parsing response: " + e.message)
        }

        return null
    }

    companion object {

        private var handler: StreamLambdaHandler? = null
        private var lambdaContext: Context? = null

        @JvmStatic
        @BeforeAll
        fun setUp() {
            handler = StreamLambdaHandler()
            lambdaContext = MockLambdaContext()
        }
    }
}
