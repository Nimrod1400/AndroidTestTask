package vadim.shamray.imagesearcher.images

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import vadim.shamray.imagesearcher.serializers.ImagesWrapper
import vadim.shamray.imagesearcher.serializers.ImagesWrapperSerializer

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
    val position: Int
)

fun parseImages(response: String?): List<Image?> {
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

