package vadim.shamray.imagesearcher.utils

import kotlin.math.max
import kotlin.math.min


fun clamp(minValue: Int, value: Int, maxValue: Int): Int {
    return when {
        value < minValue -> minValue
        value > maxValue -> maxValue
        else -> value
    }
}