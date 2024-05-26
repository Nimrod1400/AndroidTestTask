package vadim.shamray.imagesearcher.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vadim.shamray.imagesearcher.R
import vadim.shamray.imagesearcher.databinding.ImageTemplateBinding
import vadim.shamray.imagesearcher.images.Image
import vadim.shamray.imagesearcher.images.loadImageByUrl

class ImageAdapter(private val scope: LifecycleCoroutineScope,
                   private val onItemClick: (position: Int) -> Unit,
                   private val onPageEnd: () -> Unit)
    : RecyclerView.Adapter<ImageAdapter.ImageHolder>() {
    private val images: MutableList<Image> = mutableListOf()

    class ImageHolder(private val scope: LifecycleCoroutineScope,
                      template: View,
                      private val onItemClick: (position: Int) -> Unit)
        : RecyclerView.ViewHolder(template) {
        private val binding = ImageTemplateBinding.bind(template)

        fun bind(image: Image) {
            scope.launch(Dispatchers.IO) {
                try {
                    binding.title.post {
                        binding.title.text = image.title
                    }

                    loadImageByUrl(binding.image, image.thumbnailUrl)

                    binding.imageCard.post {
                        binding.imageCard.setOnClickListener {
                            onItemClick(adapterPosition)
                        }
                    }
                }
                catch (e: Exception) {
                    binding.title.post {
                        binding.title.setText(R.string.no_image)
                    }
                    binding.image.post {
                        binding.image.setImageResource(R.drawable.blank_image)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_template, parent, false)
        return ImageHolder(scope, view, onItemClick)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(images[position])

        if (position + 1 == images.size) onPageEnd()
    }

    fun addImage(image: Image) {
        images.add(image)

        notifyItemInserted(images.lastIndex)
    }

    fun clear() {
        images.clear()

        notifyItemRangeRemoved(0, images.lastIndex)
    }
}