package vadim.shamray.imagesearcher.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import vadim.shamray.imagesearcher.R
import vadim.shamray.imagesearcher.images.Image
import vadim.shamray.imagesearcher.searcher.loadImageByUrl

const val ARG_IMAGE = "Image"

class ImageFragment(private val position: Int, private val amount: Int) : Fragment() {
    lateinit var image: Image

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageJson = arguments?.getString(ARG_IMAGE)
        val imageView = view.findViewById<ImageView>(R.id.image_fullscreen)

        view.findViewById<TextView>(R.id.current_position).text =
            getString(R.string.image_position, (position + 1).toString(), amount.toString())

        try {
            image = Json.decodeFromString<Image>(imageJson!!)
        }
        catch (e: Exception) {
            unsuccessfulImageDecode(view, imageView)
            return@onViewCreated
        }

        loadImage(view, imageView)

        initOpenInBrowserButton(view)
    }

    private fun loadImage(view: View, imageView: ImageView) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                loadImageByUrl(imageView, image.imageUrl)
            } catch (e: Exception) {
                unsuccessfulImageLoad(view, imageView)
            }
        }
    }

    private fun CoroutineScope.unsuccessfulImageLoad(
        view: View,
        imageView: ImageView
    ) {
        launch(Dispatchers.Main) {
            view.findViewById<Button>(R.id.open_in_browser)
                .setText(R.string.image_not_viewed)

            imageView.setImageResource(R.drawable.blank_image)
        }
    }

    private fun unsuccessfulImageDecode(view: View, imageView: ImageView) {
        view.findViewById<Button>(R.id.open_in_browser).apply {
            setText(R.string.no_image)
            isClickable = false
        }

        imageView.setImageResource(R.drawable.blank_image)
    }

    private fun initOpenInBrowserButton(view: View) {
        view.findViewById<Button>(R.id.open_in_browser).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, image.imageUrl.toUri())
            startActivity(intent)
        }
    }
}