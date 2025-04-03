package com.example.pix.ui.screen.grid

import com.example.pix.domain.error.DomainError
import com.example.pix.domain.model.Picture

sealed interface PictureGridUiState {
    data object Loading : PictureGridUiState
    data object Empty : PictureGridUiState
    data class Success(val pictures: List<Picture>) : PictureGridUiState
    data class Error(val error: DomainError) : PictureGridUiState
}