package statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui

import android.content.Context
import android.telephony.TelephonyManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringArrayResource
import statussaver.videodownloader.videoimagesaver.downloadstatus.R

data class Country(
    val code: String,
    val iso: String,
    val country: String,
    val emoji: String,
) {
    fun contains(query: String): Boolean {
        val sQuery = query.lowercase()

        return country.lowercase().startsWith(sQuery) ||
                code.lowercase().startsWith(sQuery) ||
                iso.lowercase().startsWith(sQuery) ||
                emoji.contains(sQuery)
    }
}

@Composable
fun rememberCountries(): List<Country> {
    val countriesStr = stringArrayResource(R.array.countries)

    return remember {
        countriesStr.map { string ->
            val (code, iso, country, emoji) = string.split(",")

            Country(
                code = "+$code",
                iso = iso,
                country = country,
                emoji = emoji,
            )
        }
    }
}

fun getSimCountryCode(
    context: Context,
    countries: List<Country>,
): String {
    return runCatching {
        val manager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        val networkISO = manager?.networkCountryIso
        val simISO = manager?.simCountryIso

        val deviceCountryCode = countries.firstOrNull {
            it.iso.lowercase() == simISO || it.iso.lowercase() == networkISO
        }

        deviceCountryCode?.code!!
    }.getOrDefault("+1")
}