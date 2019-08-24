package my.service


import com.amazonaws.serverless.proxy.internal.LambdaContainerHandler
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext
import com.amazonaws.serverless.proxy.model.AwsProxyResponse
import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import my.service.models.ComposeRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import tw.inspect.poi.Rpc
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*
import javax.ws.rs.HttpMethod
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import java.io.FileOutputStream
import java.io.OutputStream




class StreamLambdaHandlerTest {

    @Test
    fun testJsonPropertyAnnotationCorrect() {

        val jsonDataString =
            ObjectMapper().writeValueAsString(
                ComposeRequest(
                    null,
                    "ee",
                    arrayListOf(
                        Rpc.RenameSheet("Sheet1", "Sheet111"),
                        Rpc.Fill("123", "456", "789", "101112", ""),
                        Rpc.AddSheet("456", "456"),
                        Rpc.DeleteSheet("456"),
                        Rpc.CopyRows("456", "456", "456", "456", "456")
                    ),
                    null,
                    null
                )
            )
        println(jsonDataString)
        val hh = ObjectMapper().readValue(jsonDataString, ComposeRequest::class.java)
        val qq =
            """{"template_base64":"ee","sections":[{"op":"COPY_ROWS","srcSheet":"Sheet1","srcRowRange":"1~24","dstSheet":"temp1","dstRow":"1","extra":"ADJUST_COLUMN_WIDTH"},{"op":"FILL","sheet":"temp1","co":"N6","title":"invoice NO.","extra":"手動","value":"invoice NO."},{"op":"FILL","sheet":"temp1","co":"N7","title":"DATE","extra":"自動產生 (格式：\"4/01\")","value":"4/01"},{"op":"FILL","sheet":"temp1","co":"N8","title":"TAX ID","extra":"手動","value":"TAX ID"},{"op":"FILL","sheet":"temp1","co":"G9","title":"TO","extra":"手動(格式：”TO : ${"$"}{input}\")","value":"TO : ${"$"}{input}"},{"op":"FILL","sheet":"temp1","co":"K10","title":"ON BOARD DATE","extra":"手動","value":"ON BOARD DATE"},{"op":"FILL","sheet":"temp1","co":"K11","title":"SHIPPED Via","extra":"手動","value":"SHIPPED Via"},{"op":"FILL","sheet":"temp1","co":"J15","title":"R/NO.(part1)","extra":"手動(格式： “R/NO. ${"$"}{input}”)","value":"R/NO. ${"$"}{input}"},{"op":"FILL","sheet":"temp1","co":"J16","title":"R/NO.(part2)","extra":"手動","value":"R/NO.(part2)"},{"op":"FILL","sheet":"temp1","co":"J17","title":"J17","extra":"自動產生","value":"MADE IN TAIWAN"},{"op":"FILL","sheet":"temp1","co":"J18","title":"J18","extra":"自動產生","value":"ROC"},{"op":"FILL","sheet":"temp1","co":"J24","title":"COUNTRY","extra":"手動","value":"COUNTRY"},{"op":"COPY_ROWS","srcSheet":"Sheet1","srcRowRange":"25~26","dstSheet":"temp1","dstRow":"25","extra":""},{"op":"FILL","sheet":"temp1","co":"C25","title":"SERIAL","extra":"自動(格式：兩位數 \"01.\" ~ \"99.\"","value":"01."},{"op":"FILL","sheet":"temp1","co":"D25","title":"PARTNO.","extra":"自動，根據DB","value":"1R900000000"},{"op":"FILL","sheet":"temp1","co":"E25","title":"PONO.","extra":"自動，根據DB","value":"4000000000"},{"op":"FILL","sheet":"temp1","co":"D26","title":"DESCRIPTION","extra":"自動，根據DB","value":"TBL-WL707_PU_0.7mm_AFTERDARK+2mmEVA  19-1101TPX"},{"op":"FILL","sheet":"temp1","co":"H26","title":"QUANTITY","extra":"手動(格式：5,000.0 )","value":"5,000.0"},{"op":"FILL","sheet":"temp1","co":"I26","title":"UNIT","extra":"FIXED (“FT²”)","value":"FT²"},{"op":"FILL","sheet":"temp1","co":"J26","title":"UNIT PRICE","extra":"自動，根據DB(格式：US${'$'}5,000.000)","value":"US${'$'}5,000.000"},{"op":"FILL","sheet":"temp1","co":"N26","title":"SUB. TOTAL","extra":"(自動，乘法，四捨五入(格式： US${'$'}5,000.00)","value":"US${'$'}25,000,000.00"},{"op":"COPY_ROWS","srcSheet":"Sheet1","srcRowRange":"33~34","dstSheet":"temp1","dstRow":"27","extra":""},{"op":"FILL","sheet":"temp1","co":"C27","title":"SERIAL","extra":"自動(格式：兩位數 \"01.\" ~ \"99.\"","value":"01."},{"op":"FILL","sheet":"temp1","co":"D27","title":"PARTNO.","extra":"自動，根據DB","value":"1R900000000"},{"op":"FILL","sheet":"temp1","co":"E27","title":"PONO.","extra":"自動，根據DB","value":"4000000000"},{"op":"FILL","sheet":"temp1","co":"D28","title":"DESCRIPTION","extra":"自動，根據DB","value":"TBL-WL707_PU_0.7mm_AFTERDARK+2mmEVA  19-1101TPX"},{"op":"FILL","sheet":"temp1","co":"H28","title":"QUANTITY","extra":"手動(格式：5,000.0 )","value":"5,000.0"},{"op":"FILL","sheet":"temp1","co":"I28","title":"UNIT","extra":"FIXED (“FT²”)","value":"FT²"},{"op":"FILL","sheet":"temp1","co":"J28","title":"UNIT PRICE","extra":"自動，根據DB(格式：US${'$'}5,000.000)","value":"US${'$'}5,000.000"},{"op":"FILL","sheet":"temp1","co":"N28","title":"SUB. TOTAL","extra":"(自動，乘法，四捨五入(格式： US${'$'}5,000.00)","value":"US${'$'}25,000,000.00"},{"op":"COPY_ROWS","srcSheet":"Sheet1","srcRowRange":"35~35","dstSheet":"temp1","dstRow":"30","extra":"ADJUST_COLUMN_WIDTH"},{"op":"FILL","sheet":"temp1","co":"H65","title":"TOTAL QUANTITY","extra":"自動，加法(格式：5,000.0)","value":"10,000.0"},{"op":"FILL","sheet":"temp1","co":"N65","title":"TOTAL","extra":"自動，加法(格式：US${'$'}5,000.00)","value":"US${'$'}50,000,000.00"},{"op":"COPY_ROWS","srcSheet":"Sheet1","srcRowRange":"37~37","dstSheet":"temp1","dstRow":"67","extra":""},{"op":"FILL","sheet":"temp1","co":"D103","title":"TOTAL IN ENGLISH FRAGMENT","extra":"自動(格式：SAY TOTAL US DOLLARS SIXTY EIGHT THOUSAND FOUR HUNDREDNINETY TWO AND CENTS FOUR ONLY.-)","value":"SAY TOTAL US DOLLARS SIXTY EIGHT THOUSAND FOUR HUNDREDNINETY"},{"op":"COPY_ROWS","srcSheet":"Sheet1","srcRowRange":"37~37","dstSheet":"temp1","dstRow":"68","extra":""},{"op":"FILL","sheet":"temp1","co":"D104","title":"TOTAL IN ENGLISH FRAGMENT","extra":"自動(格式：SAY TOTAL US DOLLARS SIXTY EIGHT THOUSAND FOUR HUNDREDNINETY TWO AND CENTS FOUR ONLY.-)","value":"TWO AND CENTS FOUR ONLY.-)"},{"op":"COPY_ROWS","srcSheet":"Sheet1","srcRowRange":"41~45","dstSheet":"temp1","dstRow":"72","extra":"ADJUST_COLUMN_WIDTH"}]}"""
        println(qq)
        val hh2 = ObjectMapper().readValue(qq, ComposeRequest::class.java)
        hh2.sections.forEach {
            println(ObjectMapper().writeValueAsString(it))
        }
    }


    //@Disabled
    @Test
    fun compose_streamRequest_respondsWithHello() {
        println(MediaType("application", "json", "utf-8").toString())
        val requestStream = AwsProxyRequestBuilder("/xlsx/compose", HttpMethod.POST)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            // Must add "charset=utf-8"
            .header(HttpHeaders.CONTENT_TYPE, MediaType("application", "json", "utf-8").toString())
            .body(File("flow1.json").readText(Charsets.UTF_8))
            .buildStream()
        val responseStream = ByteArrayOutputStream()

        handle(requestStream, responseStream)

        val response = readResponse(responseStream)
        assertNotNull(response)
        assertEquals(Response.Status.OK.statusCode.toLong(), response!!.statusCode.toLong())

        assertFalse(response.isBase64Encoded)

        val out = ObjectMapper().readTree(response.body).path("out").textValue()
        FileOutputStream("out.xlsx").use { stream -> stream.write(Base64.getDecoder().decode(out)) }


        assertTrue(response.multiValueHeaders.containsKey(HttpHeaders.CONTENT_TYPE))

        assertTrue(response.multiValueHeaders[HttpHeaders.CONTENT_TYPE]!!.fold(false) { a, b ->
            a || b.startsWith(
                MediaType.APPLICATION_JSON
            )
        })
    }

    @Test
    fun compose_streamRequest_respondsServiceMail() {
        println(MediaType("application", "json", "utf-8").toString())
        val requestStream = AwsProxyRequestBuilder("/gserviceaccount", HttpMethod.GET)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .buildStream()
        val responseStream = ByteArrayOutputStream()

        handle(requestStream, responseStream)

        val response = readResponse(responseStream)
        assertNotNull(response)
        assertEquals(Response.Status.OK.statusCode.toLong(), response!!.statusCode.toLong())

        assertFalse(response.isBase64Encoded)


        val mail = ObjectMapper().readTree(response.body).path("mail").textValue()
        println(mail)
        assertTrue(mail.contains("@"))

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
