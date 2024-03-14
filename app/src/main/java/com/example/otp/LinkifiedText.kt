package com.example.otp

import android.util.Log
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

/**
 * A data structure representing text by default. With all properties specified represents link.
 *
 * @property text the text to be displayed
 * @property tag the tag used internally to distinguish the link (if present)
 * @property link the link to be opened (if present)
 */
data class LinkData(
    val text: String,
    val tag: String? = null,
    val link: String? = null
)

/**
 * A component based on [ClickableText] to be able to handle text with links.
 *
 * @param linksData list of [LinkData] representing both texts and links
 * @param modifier [Modifier] to apply to this node
 */
@Composable
fun LinkifiedText(
    linksData: ImmutableList<LinkData>,
    modifier: Modifier = Modifier
) {
    val linkifiedString = buildLinkifiedString(linksData)

    ClickableText(
        text = linkifiedString,
        modifier = modifier,
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

/**
 * Process the Markdown text and split it into [LinkData] with extracted text and link data for
 * further processing. Any other tags are not recognized.
 *
 * @param input the text with links in Markdown format
 */
@Composable
fun linkifyMarkdown(input: String): ImmutableList<LinkData> {
    val result: ImmutableList<LinkData> = remember(key1 = input) {
        val linkRegex = """\[([^\]]+)\]\(([^)]+)\)""".toRegex()

        var cursor = 0
        val linksData = mutableListOf<LinkData>()

        val links = linkRegex.findAll(input)
        links.forEach { match ->
            // Add text right before the current link
            linksData.add(
                LinkData(
                    text = input.substring(
                        startIndex = cursor,
                        endIndex = match.range.first
                    )
                )
            )
            cursor = match.range.last + 1

            // Add the current link
            val text = match.groups[1]!!.value
            val link = match.groups[2]!!.value
            linksData.add(
                LinkData(
                    text = text,
                    tag = text,
                    link = link
                )
            )
        }

        // Add remaining text or full text if no links were found
        linksData.add(
            LinkData(
                text = input.substring(
                    startIndex = cursor
                )
            )
        )

        linksData.toPersistentList()
    }

    return result
}

/**
 * Builds [AnnotatedString] from a list of the [LinkData].
 */
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
