package statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight

object SpanStyleUtils {

    fun emphasizeString(
        text: String,
        emphasize: String,
    ): AnnotatedString {
        return styledString(
            text = text,
            styledText = emphasize,
            SpanStyle(fontWeight = FontWeight.Bold),
        )
    }

    fun styledString(
        text: String,
        styledText: String,
        vararg styles: SpanStyle,
    ): AnnotatedString {
        val start = text.indexOf(styledText)
        val end = start + styledText.length

        return buildAnnotatedString {
            append(text)
            styles.forEach { style ->
                if (start >= 0 && end > start) {
                    addStyle(
                        style = style,
                        start = start,
                        end = end,
                    )
                }
            }
        }
    }

    fun styledString(
        text: String,
        style: SpanStyle,
        vararg styledText: String,
    ): AnnotatedString {
        val startToEnd = styledText.map {
            val start = text.indexOf(it)
            val end = start + it.length
            start to end
        }

        return buildAnnotatedString {
            append(text)

            startToEnd.forEach { (start, end) ->
                if (start >= 0 && end > start) {
                    addStyle(
                        style = style,
                        start = start,
                        end = end,
                    )
                }
            }
        }
    }
}