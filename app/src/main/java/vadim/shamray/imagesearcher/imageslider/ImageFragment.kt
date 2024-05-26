package vadim.shamray.imagesearcher.imageslider

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import vadim.shamray.imagesearcher.R
import vadim.shamray.imagesearcher.images.Image
import vadim.shamray.imagesearcher.images.loadImageByUrl

const val ARG_IMAGE = "Image"

class ImageFragment(private val position: Int, private val amount: Int) : Fragment() {
    lateinit var image: Image

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageStr = arguments?.getString(ARG_IMAGE)

        val imageView = view.findViewById<ImageView>(R.id.image_fullscreen)

        view.findViewById<TextView>(R.id.current_position).text =
            getString(R.string.image_position, (position + 1).toString(), amount.toString())

        try {
            image = Json.decodeFromString<Image>(imageStr!!)
        }
        catch (e: Exception) {
            view.findViewById<Button>(R.id.open_in_browser).apply {
                setText(R.string.no_image)
                isClickable = false
            }

            imageView.setImageResource(R.drawable.blank_image)
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                loadImageByUrl(imageView, image.imageUrl)
            }
            catch (e: Exception) {
                launch(Dispatchers.Main) {
                    view.findViewById<Button>(R.id.open_in_browser).
                        setText(R.string.image_not_viewed)

                    imageView.setImageResource(R.drawable.blank_image)
                }
            }
        }

        view.findViewById<Button>(R.id.open_in_browser).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, image.imageUrl.toUri())
            startActivity(intent)
        }
    }
}