package vadim.shamray.imagesearcher.images

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

@Serializable
data class ImagesWrapper(val images: List<Image>)

object ImagesWrapperSerializer :  KSerializer<ImagesWrapper> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("DataWrapper") {
            element("images", ListSerializer(Image.serializer()).descriptor)
        }

    override fun deserialize(decoder: Decoder): ImagesWrapper {
        val input = decoder as JsonDecoder
        val tree = input.decodeJsonElement()

        val data = tree.jsonObject["images"] ?: throw SerializationException("No images found")

        return Json.decodeFromJsonElement(ImagesWrapper.serializer(),
            JsonObject(mapOf("images" to data)))
    }

    override fun serialize(encoder: Encoder, value: ImagesWrapper) {
        val jsonOutput = encoder as JsonEncoder
        jsonOutput.encodeJsonElement(
            JsonObject(mapOf("images" to Json.encodeToJsonElement(ListSerializer(Image.serializer()),
                value.images))))
    }
}