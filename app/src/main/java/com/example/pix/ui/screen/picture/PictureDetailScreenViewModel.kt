package com.example.pix.ui.screen.picture

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pix.domain.error.DomainError
import com.example.pix.domain.usecase.GetPictureByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ERROR_PICTURE_ID_NULL = "PictureId is null"

@HiltViewModel
class PictureDetailScreenViewModel @Inject constructor(
    private val getPictureByIdUseCase: GetPictureByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<PictureDetailUiState>(PictureDetailUiState.Loading)
    val state = _state.asStateFlow()

    private val pictureId: String? = savedStateHandle["pictureId"]

    init {
        loadPicture()
    }

    fun loadPicture() {
        Log.d("PictureDetailScreenViewModel", "loadPicture called: $pictureId")

        if (pictureId == null) {
            _state.value =
                PictureDetailUiState.Error(DomainError.Unknown(Throwable(ERROR_PICTURE_ID_NULL)))
            return
        }

        _state.value = PictureDetailUiState.Loading
        viewModelScope.launch {
            getPictureByIdUseCase(pictureId)
                .onSuccess { picture ->
                    _state.value = PictureDetailUiState.Success(picture)
                }
                .onFailure { throwable ->
                    val domainError = throwable as? DomainError ?: DomainError.Unknown(throwable)
                    _state.value = PictureDetailUiState.Error(domainError)
                }
        }
    }
}