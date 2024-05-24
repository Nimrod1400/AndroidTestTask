package vadim.shamray.imagesearcher.images

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URL

data class Image(
    val title: String,
    val imageUrl: URL,
    val imageWidth: Int,
    val imageHeight: Int,
    val thumbnailUrl: URL,
    val thumbnailWidth: Int,
    val thumbnailHeight: Int,
    val source: String,
    val domain: URL,
    val link: URL,
    val googleUrl: URL,
    val position: Int,
) { }

fun loadImageByUrl(imageView: ImageView, imageUrl: URL) {
    val connection = imageUrl.openConnection()
    connection.doInput = true
    connection.connect()
    val bitmap = BitmapFactory.decodeStream(connection.getInputStream())
    imageView.post {
        imageView.setImageBitmap(bitmap)
    }
}