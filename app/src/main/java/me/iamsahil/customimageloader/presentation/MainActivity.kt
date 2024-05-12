package me.iamsahil.customimageloader.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.iamsahil.customimageloader.data.dto.ImageResponseItem
import me.iamsahil.customimageloader.image_loader.RemoteImage
import me.iamsahil.customimageloader.theme.CustomImageLoaderTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel by viewModels<ImageViewModel>()

        setContent {
            CustomImageLoaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val images by viewModel.images.collectAsStateWithLifecycle()
                    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

                    LaunchedEffect(Unit) {
                        repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.error.collect {
                                Toast.makeText(
                                    applicationContext,
                                    it.asString(applicationContext),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    if (isLoading) {
                        Box {
                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                        }
                    } else {
                        ImageGrid(modifier = Modifier.fillMaxSize(), urls = images)
                    }
                }
            }
        }
    }
}

@Composable
fun ImageGrid(
    modifier: Modifier, urls: List<ImageResponseItem>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // Sets the number of columns in the grid
        modifier = modifier.padding(4.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(urls) { image ->
            Card(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                RemoteImage(
                    url = image.thumbnail.getImageUrl(),
                    modifier = Modifier.fillMaxSize(),
                    loadingComposable = {
                        CircularProgressIndicator(
                            modifier = Modifier.align(
                                Alignment.Center
                            )
                        )
                    },
                    errorComposable = { Text("Failed to load", color = Color.Red) }
                )
            }

        }
    }
}


