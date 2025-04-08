package com.shady.language

import java.util.Locale

data class Language(
    val name: String,
    val code: String,
    val flagEmoji: String,
) {
    val locale: Locale get() = Locale(code)
}