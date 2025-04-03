package com.example.pix.ui.screen.picture

import com.example.pix.domain.error.DomainError
import com.example.pix.domain.model.Picture

sealed interface PictureDetailUiState {
    data object Loading : PictureDetailUiState
    data class Success(val picture: Picture) : PictureDetailUiState
    data class Error(val error: DomainError) : PictureDetailUiState
}