package statussaver.videodownloader.videoimagesaver.downloadstatus.datastore

import com.shady.language.Language
import com.shady.language.LanguageStore
import com.shady.language.Languages
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageDataStore @Inject constructor(
    private val preference: UserPreference,
) : LanguageStore {

    private val languages = Languages.common

    override val language: Flow<Language?> = preference.selectedLanguage.map { code ->
        if (code.isNullOrEmpty()) {
            null
        } else {
            languages.firstOrNull { it.code == code }
        }
    }

    override suspend fun setLanguage(language: Language?) {
        preference.setLanguage(language?.code)
    }

}