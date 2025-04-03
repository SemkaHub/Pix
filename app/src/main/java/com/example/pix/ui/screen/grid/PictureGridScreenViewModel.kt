package com.example.pix.ui.screen.grid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pix.domain.error.DomainError
import com.example.pix.domain.usecase.GetPicturesUseCase
import com.example.pix.domain.usecase.ObservePicturesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

private const val ROOM_UNKNOWN_EXCEPTION = "Database read error"
private const val UNKNOWN_ERROR = "Unknown error"

@HiltViewModel
class PictureGridScreenViewModel @Inject constructor(
    private val getPicturesUseCase: GetPicturesUseCase,
    private val observePicturesUseCase: ObservePicturesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<PictureGridUiState>(PictureGridUiState.Loading)
    val state = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _transientErrorFlow = MutableSharedFlow<DomainError>()
    val transientErrorFlow = _transientErrorFlow.asSharedFlow()

    init {
        observeDatabase()
        refreshPictures()
    }

    fun observeDatabase() {
        viewModelScope.launch {
            observePicturesUseCase()
                .catch { e ->
                    if (e is CancellationException) throw e
                    _state.value = PictureGridUiState.Error(
                        DomainError.Unknown(
                            Throwable(
                                ROOM_UNKNOWN_EXCEPTION
                            )
                        )
                    )
                }
                .collect { pictures ->
                    _state.value = PictureGridUiState.Success(pictures)
                }
        }
    }

    fun refreshPictures() {
        if (_isRefreshing.value) {
            return
        }

        viewModelScope.launch {
            _isRefreshing.value = true
            val refreshResult = getPicturesUseCase()
            _isRefreshing.value = false

            if (refreshResult.isFailure) {
                val error = refreshResult.exceptionOrNull() as? DomainError
                    ?: DomainError.Unknown(Throwable(UNKNOWN_ERROR))

                // Проверяем, есть ли сейчас данные на экране (из БД)
                val currentData = (_state.value as? PictureGridUiState.Success)?.pictures

                if (!currentData.isNullOrEmpty()) {
                    // Данные есть -> показываем ошибку как Toast
                    _transientErrorFlow.emit(error)
                } else {
                    // Данных нет (или они пустые) -> показываем ошибку на весь экран
                    _state.value = PictureGridUiState.Error(error)
                }
            } else {
                val currentData = (_state.value as? PictureGridUiState.Success)?.pictures
                if (currentData?.isEmpty() == true && _state.value !is PictureGridUiState.Error) {
                    _state.value = PictureGridUiState.Error(DomainError.Server.NotFound)
                }
            }
        }
    }
}