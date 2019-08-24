package tw.inspect.googledriver

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Bucket
import com.google.cloud.storage.BucketInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import java.nio.charset.Charset
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.Base64.getDecoder



fun saveFile() :String{
    val str = String(getDecoder().decode(buildconfig.BuildConfig.GOOGLE_CREDENTIALS), Charset.forName("UTF-8"))
    println(str)
    val credentials = GoogleCredentials.fromStream(ByteArrayInputStream(str.toByteArray(Charset.forName("UTF-8"))) as InputStream?)
    val storage = StorageOptions.newBuilder().setCredentials(credentials).build().service
    return str
}
