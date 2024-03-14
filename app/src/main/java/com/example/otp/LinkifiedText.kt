package com.example.otp

import android.util.Log
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import kotlinx.collections.immutable.ImmutableList

data class LinkData(
    val text: String,
    val tag: String? = null,
    val link: String? = null
)

@Composable
fun LinkifiedText(linksData: ImmutableList<LinkData>) {
    val linkifiedString = buildLinkifiedString(linksData)

    ClickableText(
        text = linkifiedString,
        style = MaterialTheme.typography.bodyLarge,
        onClick = { offset ->
            linksData.forEach { data ->
                if (data.tag.isNullOrEmpty() || data.link.isNullOrEmpty()) {
                    return@forEach
                }

                linkifiedString.getStringAnnotations(
                    tag = data.tag,
                    start = offset,
                    end = offset,
                ).firstOrNull()?.let { annotation ->
                    Log.d("ClickableText", "Clicked on ${annotation.item} (tag: ${annotation.tag})")
                }
            }
        }
    )
}

@Composable
@ReadOnlyComposable
private fun buildLinkifiedString(linksData: ImmutableList<LinkData>): AnnotatedString {
    return buildAnnotatedString {
        linksData.forEach { data ->
            if (data.tag.isNullOrEmpty() || data.link.isNullOrEmpty()) {
                append(data.text)
            } else {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        letterSpacing = MaterialTheme.typography.bodyLarge.letterSpacing,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    pushStringAnnotation(tag = data.tag, annotation = data.link)
                    append(data.text)
                    pop()
                }
            }
        }
    }
}
