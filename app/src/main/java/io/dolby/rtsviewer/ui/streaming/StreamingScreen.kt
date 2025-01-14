package io.dolby.rtsviewer.ui.streaming

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.millicast.VideoRenderer
import io.dolby.rtscomponentkit.ui.DolbyBackgroundBox
import io.dolby.rtsviewer.MainActivity
import io.dolby.rtsviewer.R
import io.dolby.rtsviewer.utils.KeepScreenOn
import io.dolby.rtsviewer.utils.SetupVolumeControlAudioStream
import io.dolby.rtsviewer.utils.findActivity
import org.webrtc.RendererCommon

@Composable
fun StreamingScreen(viewModel: StreamingViewModel = hiltViewModel(), onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showSettings = viewModel.showSettings.collectAsState()
    val showStatistics = viewModel.showStatistics.collectAsState()
    val showSimulcastSettings = viewModel.showSimulcastSettings.collectAsState()

    val screenContentDescription = stringResource(id = R.string.streaming_screen_contentDescription)
    DolbyBackgroundBox(
        modifier = Modifier.semantics {
            contentDescription = screenContentDescription
        }
    ) {
        val context = LocalContext.current
        when {
            uiState.error != null -> {
                ErrorView(error = uiState.error!!)
            }
            uiState.subscribed -> {
                Box(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    AndroidView(
                        factory = { context -> VideoRenderer(context) },
                        update = { view ->
                            view.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
                            uiState.videoTrack?.setRenderer(view)
                        }
                    )
                    SetupVolumeControlAudioStream()
                }
            }
            uiState.disconnected -> {
                (context.findActivity() as? MainActivity?)?.unregisterVolumeObserverIfExists()
            }
        }

        uiState.audioTrack?.let {
            (context.findActivity() as? MainActivity?)?.addVolumeObserver(it)
        }

        if (uiState.subscribed) {
            KeepScreenOn(enabled = true)
        } else {
            KeepScreenOn(enabled = false)
        }

        StreamingToolbarView(viewModel = viewModel)

        if (showSimulcastSettings.value) {
            SimulcastScreen(viewModel)
        } else if (showSettings.value) {
            SettingsScreen(viewModel)
        }

        if (showStatistics.value && uiState.subscribed) {
            StatisticsView(
                viewModel = viewModel,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 22.dp, vertical = 15.dp)
            )
        }

        BackHandler {
            if (showSimulcastSettings.value) {
                viewModel.updateShowSimulcastSettings(false)
            } else if (showSettings.value) {
                viewModel.settingsVisibility(false)
            } else {
                onBack.invoke()
            }
        }
    }
}
