package com.example.pix.ui.screen.grid

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.example.pix.R
import com.example.pix.domain.error.DomainError
import com.example.pix.domain.model.Picture
import com.example.pix.ui.states.ErrorState
import com.example.pix.ui.states.LoadingIndicator
import com.example.pix.ui.utils.getFriendlyMessage
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PictureGridScreen(
    viewModel: PictureGridScreenViewModel = hiltViewModel(),
    onPictureClick: (pictureId: String) -> Unit
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Показываем Toast при фоновых ошибках
    ShowToastOnError(errorEventFlow = viewModel.transientErrorFlow, context = context)

    PullToRefreshBox(
        modifier = Modifier
            .fillMaxSize(),
        isRefreshing = isRefreshing,
        onRefresh = viewModel::refreshPictures
    ) {
        when (val state = uiState) {
            is PictureGridUiState.Loading -> LoadingIndicator()
            is PictureGridUiState.Empty -> EmptyState()
            is PictureGridUiState.Success -> {
                Log.d("PictureGridScreen", "Success state: ${state.pictures}")
                PictureGrid(
                    pictures = state.pictures,
                    onPictureClick = onPictureClick
                )
            }

            is PictureGridUiState.Error -> ErrorState(
                error = state.error,
                onRetry = viewModel::refreshPictures
            )
        }
    }
}

@Composable
private fun ShowToastOnError(
    errorEventFlow: SharedFlow<DomainError>,
    context: Context
) {
    LaunchedEffect(key1 = Unit) {
        errorEventFlow.collectLatest { error ->
            val message = error.getFriendlyMessage(context)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.error_title),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PictureGrid(pictures: List<Picture>, onPictureClick: (pictureId: String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = dimensionResource(id = R.dimen.cell_min_size)),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(dimensionResource(id = R.dimen.padding_small)),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.spacer_medium)
        ),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacer_medium))
    ) {
        items(pictures, key = { it.id }) { picture ->
            GridItem(
                picture = picture,
                onPictureClick = onPictureClick
            )
        }
    }
}

@Composable
fun GridItem(picture: Picture, onPictureClick: (pictureId: String) -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onPictureClick(picture.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_small))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(picture.url)
                .crossfade(true)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .build(),
            contentDescription = "Picture: ${picture.id}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true, name = "Grid Success Preview")
@Composable
fun PictureGridSuccessPreview() {
    MaterialTheme {
        PictureGrid(
            pictures = listOf(
                Picture("1", "url1", "title1"),
                Picture("2", "url2", "title2"),
                Picture("3", "url3", "title3")
            ),
            onPictureClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Error Preview")
@Composable
fun PictureGridErrorPreview() {
    MaterialTheme {
        ErrorState(error = DomainError.Network, onRetry = {})
    }
}

@Preview(showBackground = true, name = "Loading Preview")
@Composable
fun PictureGridLoadingPreview() {
    MaterialTheme {
        LoadingIndicator()
    }
}

@Preview(showBackground = true, name = "Empty Preview")
@Composable
fun PictureGridEmptyPreview() {
    MaterialTheme {
        EmptyState()
    }
}