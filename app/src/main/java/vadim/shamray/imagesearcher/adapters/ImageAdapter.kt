package vadim.shamray.imagesearcher.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vadim.shamray.imagesearcher.R
import vadim.shamray.imagesearcher.databinding.ImageTemplateBinding
import vadim.shamray.imagesearcher.images.Image
import vadim.shamray.imagesearcher.searcher.loadImageByUrl

class ImageAdapter(private val scope: LifecycleCoroutineScope,
                   private val onPageEndCallback: () -> Unit,
                   private val onItemClick: (position: Int) -> Unit)
    : RecyclerView.Adapter<ImageAdapter.ImageHolder>() {
    private val images: MutableList<Image> = mutableListOf()

    class ImageHolder(private val scope: LifecycleCoroutineScope,
                      template: View,
                      private val onItemClick: (Int) -> Unit)
        : RecyclerView.ViewHolder(template) {
        private val binding = ImageTemplateBinding.bind(template)

        fun bind(image: Image) {
            scope.launch(Dispatchers.IO) {
                try {
                    successfulImageLoad(image)
                }
                catch (e: Exception) {
                    unsuccessfulImageLoad()
                }
            }
        }

        private suspend fun successfulImageLoad(image: Image) = withContext(Dispatchers.IO) {
            binding.title.post { binding.title.text = image.title }

            loadImageByUrl(binding.image, image.thumbnailUrl)

            binding.imageCard.post {
                binding.imageCard.setOnClickListener {
                    onItemClick(adapterPosition)
                }
            }
        }

        private suspend fun unsuccessfulImageLoad() = withContext(Dispatchers.Main) {
            binding.run {
                title.post { binding.title.setText(R.string.no_image) }
                image.post { binding.image.setImageResource(R.drawable.blank_image) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.image_template, parent, false)

        return ImageHolder(scope, view, onItemClick)
    }

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(images[position])
        if (position + 1 == images.size) onPageEndCallback()
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