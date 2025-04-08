package com.shady.language

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.util.Locale


class LanguageManager private constructor(
    private val store: LanguageStore,
    private val delegate: UpdateLocaleDelegate
) {

    internal var systemLocale: Locale = defaultLocale

    val language = store.language

    fun setLanguage(context: Context, language: Language?) {
        persistAndApply(context, language)
    }

    fun applyLocale(context: Context): Context {
        return runBlocking {
            val locale = language.firstOrNull()?.locale ?: systemLocale

            delegate.applyLocale(context, locale)
        }
    }

    internal fun initialize(application: Application) {
        application.registerActivityLifecycleCallbacks(
            LingverActivityLifecycleCallbacks {
                applyForActivity(it)
            }
        )
        application.registerComponentCallbacks(
            LingverApplicationCallbacks {
                processConfigurationChange(application, it)
            }
        )

        applyLocale(application)
    }

    private fun persistAndApply(context: Context, language: Language?) {
        runBlocking {
            store.setLanguage(language)
            delegate.applyLocale(context, language?.locale ?: systemLocale)
        }
    }

    private fun processConfigurationChange(context: Context, config: Configuration) {
        systemLocale = config.getLocaleCompat()

        applyLocale(context)
    }

    private fun applyForActivity(activity: Activity) {
        applyLocale(activity)
        activity.resetTitle()
    }

    companion object {
        @SuppressLint("ConstantLocale")
        private val defaultLocale: Locale = Locale.getDefault()

        @Volatile
        private var INSTANCE: LanguageManager? = null

        @JvmStatic
        fun getInstance(): LanguageManager {
            return INSTANCE ?: error("LanguageManager should be initialized first")
        }

        @JvmStatic
        fun init(application: Application, store: LanguageStore): LanguageManager {
            return INSTANCE ?: synchronized(this) {
                LanguageManager(store, UpdateLocaleDelegate()).apply {
                    initialize(application)
                }.also {
                    INSTANCE = it
                }
            }
        }
    }
}
