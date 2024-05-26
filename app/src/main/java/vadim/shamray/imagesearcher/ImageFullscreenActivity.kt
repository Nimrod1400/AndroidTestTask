package vadim.shamray.imagesearcher

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import vadim.shamray.imagesearcher.adapters.ImageSliderAdapter
import vadim.shamray.imagesearcher.images.Image

const val ARG_POSITION = "Position"
const val ARG_AMOUNT = "Amount"

class ImageFullscreenActivity : AppCompatActivity() {
    private lateinit var sliderAdapter: ImageSliderAdapter
    private lateinit var viewPager: ViewPager2
    private var images = mutableListOf<String>()
    private var currentPosition: Int = 0
    private var imagesCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_image_fullscreen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initImages()

        sliderAdapter = ImageSliderAdapter(this)
        viewPager = findViewById(R.id.image_pager)
        viewPager.adapter = sliderAdapter

        for (image in images) {
            sliderAdapter.addImage(image)
        }
        viewPager.setCurrentItem(currentPosition, false)
    }

    private fun initImages() {
        val bundle = intent.extras

        currentPosition = bundle?.getInt(ARG_POSITION) ?: 1
        imagesCount = bundle?.getInt(ARG_AMOUNT) ?: 1

        for (i in 0..<imagesCount) {
            val image = bundle?.getString("image$i") ?: ""
            images.add(image)
        }
    }
}