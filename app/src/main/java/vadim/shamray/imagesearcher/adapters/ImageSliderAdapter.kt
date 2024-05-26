package vadim.shamray.imagesearcher.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import vadim.shamray.imagesearcher.fragments.ARG_IMAGE
import vadim.shamray.imagesearcher.fragments.ImageFragment

class ImageSliderAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    private val images = mutableListOf<String>()

    override fun createFragment(position: Int): Fragment {
        val fragment = ImageFragment(position, images.size)
        fragment.arguments = Bundle().apply {
            putString(ARG_IMAGE, images[position])
        }

        return fragment
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun addImage(image: String) {
        images.add(image)
        notifyItemInserted(images.lastIndex)
    }
}