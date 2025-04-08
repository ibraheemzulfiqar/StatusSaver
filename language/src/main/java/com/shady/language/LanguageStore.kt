package com.shady.language

import kotlinx.coroutines.flow.Flow


interface LanguageStore {

    val language: Flow<Language?>

    suspend fun setLanguage(language: Language?)

}