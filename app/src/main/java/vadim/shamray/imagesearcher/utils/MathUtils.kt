package vadim.shamray.imagesearcher.utils

fun clamp(minValue: Int, value: Int, maxValue: Int): Int {
    return when {
        value < minValue -> minValue
        value > maxValue -> maxValue
        else -> value
    }
}