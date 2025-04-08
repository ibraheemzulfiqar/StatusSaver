package com.shady.language

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

internal class UpdateLocaleDelegate {

    internal fun applyLocale(context: Context, locale: Locale): Context {
        val updatedContext = updateResources(context, locale)
        val appContext = context.applicationContext
        if (appContext !== context) {
            updateResources(appContext, locale)
        }
        return updatedContext
    }

    @Suppress("DEPRECATION")
    private fun updateResources(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)

        val res = context.resources
        val current = res.configuration.getLocaleCompat()

        if (current == locale) return context

        val config = Configuration(res.configuration)
        when {
            isAtLeastSdkVersion(Build.VERSION_CODES.N) -> setLocaleForApi24(config, locale)
            isAtLeastSdkVersion(Build.VERSION_CODES.JELLY_BEAN_MR1) -> config.setLocale(locale)
            else -> config.locale = locale
        }

        res.updateConfiguration(config, res.displayMetrics)

        return context.createConfigurationContext(config) ?: context
    }

    @SuppressLint("NewApi")
    @Suppress("SpreadOperator")
    private fun setLocaleForApi24(config: Configuration, locale: Locale) {
        // bring the target locale to the front of the list
        val set = linkedSetOf(locale)

        val defaultLocales = LocaleList.getDefault()
        val all = List<Locale>(defaultLocales.size()) { defaultLocales[it] }
        // append other locales supported by the user
        set.addAll(all)

        config.setLocales(LocaleList(*set.toTypedArray()))
    }
}
