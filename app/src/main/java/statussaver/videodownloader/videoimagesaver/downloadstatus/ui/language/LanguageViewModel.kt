package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.language

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import statussaver.videodownloader.videoimagesaver.downloadstatus.datastore.UserPreference
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val preference: UserPreference,
) : ViewModel() {

    fun saveLanguageBoardingDone() {
        preference.setLanguageBoardingDone(true)
    }

}