package tw.inspect.googledriver


import java.nio.charset.Charset
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.Base64.getDecoder
import com.google.api.services.drive.Drive
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.model.File
import com.google.api.client.http.FileContent
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.DriveScopes


fun getConfig() = String(getDecoder().decode(buildconfig.BuildConfig.GOOGLE_CREDENTIALS), Charset.forName("UTF-8"))

fun getDriveService(): Drive {
    val credential = GoogleCredential
        .fromStream(ByteArrayInputStream(getConfig().toByteArray(Charset.forName("UTF-8"))) as InputStream?)
        .createScoped(listOf(DriveScopes.DRIVE))
    return Drive.Builder(NetHttpTransport(), JacksonFactory(), credential).setApplicationName("exlent").build()
}

fun getMail() = ObjectMapper().readTree(getConfig()).path("client_email").textValue()


fun createSubDir(service: Drive, folderId: String) {

    val fileMetadata = File()
    fileMetadata.name = "Invoices"
    fileMetadata.parents = listOf(folderId)
    fileMetadata.mimeType = "application/vnd.google-apps.folder"

    val file = service.files().create(fileMetadata)
        .setFields("id, parents")
        .execute()
    println("File ID: " + file.id)

}

fun uploadFile(service: Drive, filename: String, parent: String, inputStream: InputStream): String {
    val fileMetadata = File()
    fileMetadata.name = filename
    fileMetadata.parents = listOf(parent)
    val mediaContent =
        InputStreamContent("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", inputStream)
    val file = service.files().create(fileMetadata, mediaContent)
        .setFields("id, parents")
        .execute()
    return file.id
}
