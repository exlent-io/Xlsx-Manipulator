package tw.inspect.googledriver


import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class GoogleDriverTest {

    @Test
    fun ping_streamRequest_respondsWithHello() {
        val drive = getDriveService()
        createSubDir(drive, "1RaI1MdsdnAwxmjqE2HRlw10Ne03wUT9U")
        assertTrue(true)
        println("end")
    }

}
