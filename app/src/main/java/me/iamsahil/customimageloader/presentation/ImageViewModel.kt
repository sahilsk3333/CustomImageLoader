package me.iamsahil.customimageloader.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.iamsahil.customimageloader.data.ImageRepository
import me.iamsahil.customimageloader.data.dto.ImageResponseItem
import me.iamsahil.customimageloader.util.Result
import me.iamsahil.customimageloader.util.UiText
import me.iamsahil.customimageloader.util.asUiText
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _images = MutableStateFlow(emptyList<ImageResponseItem>())
    val images = _images.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val errorChannel = Channel<UiText>()
    val error = errorChannel.receiveAsFlow()

    init {
      fetchImages()
    }

    private fun fetchImages(){
        viewModelScope.launch {
            _isLoading.value = true
            when(val result = imageRepository.getImages()){
                is Result.Error -> errorChannel.send(result.error.asUiText())
                is Result.Success -> {
                    _images.update { result.data }
                }
            }
            _isLoading.value = false
        }
    }

}