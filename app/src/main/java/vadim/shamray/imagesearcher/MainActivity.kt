package vadim.shamray.imagesearcher

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import vadim.shamray.imagesearcher.adapters.ImageAdapter
import vadim.shamray.imagesearcher.images.Image
import vadim.shamray.imagesearcher.images.SearchQuery
import vadim.shamray.imagesearcher.images.searchImages
import vadim.shamray.imagesearcher.utils.clamp

class MainActivity : AppCompatActivity() {
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var imagesDeferred: CompletableDeferred<List<Image?>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initRecyclerView()
        initSearchField()
    }

    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.images_container)

        imageAdapter = ImageAdapter(lifecycleScope) { position: Int ->
            GlobalScope.launch(Dispatchers.Main) {
                openFullscreen(position)
            }
        }

        recyclerView.layoutManager = GridLayoutManager(this@MainActivity, 3)
        recyclerView.adapter = imageAdapter

        lifecycleScope.launch { // TODO : move into separate func
            val images = searchImages(SearchQuery(query = "Greeting word"))
            for (image in images) {
                if (image != null) {
                    imageAdapter.addImage(image)
                }
            }
            imagesDeferred = CompletableDeferred(images)
            imagesDeferred.complete(images)
        }
    }

    private fun initSearchField() {
        val searchField = findViewById<TextInputEditText>(R.id.search_field)
        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId != EditorInfo.IME_ACTION_SEARCH) return@setOnEditorActionListener false
            lifecycleScope.launch { // TODO : move into separate func
                val images = searchImages(SearchQuery(query = searchField.text.toString()))
                imageAdapter.clear()
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(searchField.windowToken, 0)
                searchField.clearFocus()
                for (image in images) {
                    if (image != null) {
                        imageAdapter.addImage(image)
                    }
                }
                imagesDeferred = CompletableDeferred(images)
                imagesDeferred.complete(images)
            }
            return@setOnEditorActionListener true
        }
    }

    private suspend fun openFullscreen(position: Int) = withContext(Dispatchers.Main) {
        val fullscreenIntent = Intent(this@MainActivity, ImageFullscreenActivity::class.java)
        val bundle = formBundle(position)
        fullscreenIntent.putExtras(bundle)
        startActivity(fullscreenIntent)
    }

    private suspend fun formBundle(position: Int): Bundle = withContext(Dispatchers.Main) {
        val itemsTotal = 33
        val itemsLeft = itemsTotal / 2
        val itemsRight = itemsTotal / 2 + 1

        val totalAmount = imagesDeferred.await().lastIndex
        val bundle = Bundle()

        var startIndex = clamp(0, position - itemsLeft, position)
        var endIndex = clamp(position, (position + itemsRight), totalAmount)

        bundle.putInt(ARG_POSITION, position - startIndex)

        val amount = endIndex - startIndex + 1

        bundle.putInt(ARG_AMOUNT, amount)

        var c = 0
        imagesDeferred.await().subList(startIndex, endIndex).forEach {
            bundle.putString("$c", Json.encodeToString(it))
            c += 1
        }

        bundle
    }
}
