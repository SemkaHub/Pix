package com.example.pix.ui.screen.picture

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.example.pix.R
import com.example.pix.domain.model.Picture
import com.example.pix.ui.states.ErrorState
import com.example.pix.ui.states.LoadingIndicator
import kotlinx.coroutines.launch

private const val MIN_SCALE = 1f
private const val MAX_SCALE = 5f
private const val START_ROTATION = 0f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PictureDetailScreen(
    navController: NavController,
    viewModel: PictureDetailScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_second_screen)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.button_back_description)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = uiState) {
                is PictureDetailUiState.Loading -> LoadingIndicator()
                is PictureDetailUiState.Success -> {
                    Log.d("PictureDetailScreen", "Success state: ${state.picture}")
                    ZoomableImage(
                        picture = state.picture,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is PictureDetailUiState.Error -> ErrorState(
                    error = state.error,
                    onRetry = viewModel::loadPicture
                )
            }
        }
    }
}

@Composable
fun ZoomableImage(
    picture: Picture,
    modifier: Modifier = Modifier,
    minScale: Float = MIN_SCALE,
    maxScale: Float = MAX_SCALE
) {

    var scale by remember { mutableFloatStateOf(MIN_SCALE) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var rotation by remember { mutableFloatStateOf(START_ROTATION) }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        // При двойном тапе сбрасываем зум и поворот
                        coroutineScope.launch {
                            scale = minScale
                            offset = Offset.Zero
                            rotation = START_ROTATION
                        }
                    },
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, gestureRotation ->
                    val newScale = (scale * zoom).coerceIn(minScale, maxScale)
                    val actualZoom = newScale / scale

                    offset = (offset + pan) * actualZoom

                    scale = newScale
                    rotation += gestureRotation
                }
            }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(picture.url)
                .crossfade(true)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .build(),
            contentDescription = stringResource(R.string.text_zoomable_picture) + picture.id,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                    rotationZ = rotation
                )
                .fillMaxSize()
        )
    }
}