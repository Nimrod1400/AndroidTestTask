package vadim.shamray.imagesearcher.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import vadim.shamray.imagesearcher.images.Image
import vadim.shamray.imagesearcher.imageslider.ARG_IMAGE
import vadim.shamray.imagesearcher.imageslider.ImageFragment

class ImageSliderAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    val images = mutableListOf<String>()

    override fun getItemCount(): Int {
        return images.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = ImageFragment(position, images.size)
        fragment.arguments = Bundle().apply {
            putString(ARG_IMAGE, images[position])
        }

        return fragment
    }

    fun addImage(image: String) {
        images.add(image)

        notifyItemInserted(images.lastIndex)
    }
}