package vadim.shamray.imagesearcher.serializers

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
import vadim.shamray.imagesearcher.images.Image

const val ARG_IMAGES_ELEMENT = "images"

@Serializable
data class ImagesWrapper(val images: List<Image>)

object ImagesWrapperSerializer :  KSerializer<ImagesWrapper> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("DataWrapper") {
            element(ARG_IMAGES_ELEMENT, ListSerializer(Image.serializer()).descriptor)
        }

    override fun deserialize(decoder: Decoder): ImagesWrapper {
        val input = decoder as JsonDecoder
        val tree = input.decodeJsonElement()

        val data = tree.jsonObject[ARG_IMAGES_ELEMENT] ?: throw SerializationException("No images found")

        return Json.decodeFromJsonElement(
            ImagesWrapper.serializer(),
            JsonObject(mapOf(ARG_IMAGES_ELEMENT to data)))
    }

    override fun serialize(encoder: Encoder, value: ImagesWrapper) {
        val jsonOutput = encoder as JsonEncoder
        jsonOutput.encodeJsonElement(
            JsonObject(mapOf(ARG_IMAGES_ELEMENT to Json.encodeToJsonElement(ListSerializer(Image.serializer()),
                value.images))))
    }
}