package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import statussaver.videodownloader.videoimagesaver.downloadstatus.datastore.UserPreference
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.splash.SplashState.Loading
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.splash.SplashState.Success
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preference: UserPreference,
) : ViewModel() {

    val state = preference.languageBoardingDone
        .map { Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = Loading,
        )

}


sealed interface SplashState {

    data object Loading : SplashState

    data class Success(
        val languageBoardingDone: Boolean,
    )

}