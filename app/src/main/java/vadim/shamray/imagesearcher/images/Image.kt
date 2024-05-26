package vadim.shamray.imagesearcher.images

import android.graphics.BitmapFactory
import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.modules.SerializersModule
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class Image(
    val title: String,
    val imageUrl: String,
    val imageWidth: Int,
    val imageHeight: Int,
    val thumbnailUrl: String,
    val thumbnailWidth: Int,
    val thumbnailHeight: Int,
    val source: String,
    val domain: String,
    val link: String,
    val googleUrl: String,
    val position: Int,
)

class SearchQuery(
    val query: String,
    val country: String = "ru",
    val location: String = "Russia",
    val language: String = "ru",
    val resultsCount: Int = 33,
    var page: Int = 1
) {

    private fun quote(str: String): String {
        return "\"$str\""
    }

    fun toJson(): String {
        var result = "{"
        result += "${quote("q")}:${quote(query)},"
        result += "${quote("gl")}:${quote(country)},"
        result += "${quote("location")}:${quote(location)},"
        result += "${quote("hl")}:${quote(language)},"
        result += "${quote("num")}:$resultsCount,"
        result += "${quote("page")}:$page"
        result += "}"

        return result
    }
}

suspend fun loadImageByUrl(imageView: ImageView, imageUrl: String) = withContext(Dispatchers.IO) {
    with(URL(imageUrl).openConnection() as HttpURLConnection) {
        connect()
        val bitmap = BitmapFactory.decodeStream(inputStream)
        imageView.post {
            imageView.setImageBitmap(bitmap)
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
private fun parseImages(response: String?): List<Image?> {
    val format = Json {
        ignoreUnknownKeys = true
        serializersModule = SerializersModule {
            contextual(ImagesWrapper::class, ImagesWrapperSerializer)
        }
    }

    return when (response) {
        null -> emptyList<Image>()
        else -> format.decodeFromString<ImagesWrapper>(response).images
    }
}

suspend fun searchImages(query: SearchQuery): List<Image?> = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val requestBody = query.toJson().toRequestBody("application/json".toMediaType())
    val request = Request.Builder()
        .url("https://google.serper.dev/images")
        .addHeader("X-API-KEY", "e3f84fbb9eef556f780630cdc5fb55097dbc8e5b")
        .post(requestBody)
        .build()
    val response = client.newCall(request).execute().body?.string()

    parseImages(response)
}