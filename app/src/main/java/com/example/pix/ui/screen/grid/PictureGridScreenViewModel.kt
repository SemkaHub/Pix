package com.example.pix.ui.screen.grid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pix.domain.error.DomainError
import com.example.pix.domain.usecase.GetPicturesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PictureGridScreenViewModel @Inject constructor(
    private val getPicturesUseCase: GetPicturesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<PictureGridUiState>(PictureGridUiState.Loading)
    val state = _state.asStateFlow()

    init {
        loadPictures()
    }

    fun loadPictures() {

        if (_state.value !is PictureGridUiState.Success) {
            _state.value = PictureGridUiState.Loading
        }

        viewModelScope.launch {
            getPicturesUseCase()
                .onSuccess { pictures ->
                    _state.update {
                        if (pictures.isEmpty()) {
                            PictureGridUiState.Empty
                        } else {
                            PictureGridUiState.Success(pictures)
                        }
                    }
                }
                .onFailure { throwable ->
                    val domainError = throwable as? DomainError ?: DomainError.Unknown(throwable)
                    _state.update { PictureGridUiState.Error(domainError) }
                }
        }
    }
}