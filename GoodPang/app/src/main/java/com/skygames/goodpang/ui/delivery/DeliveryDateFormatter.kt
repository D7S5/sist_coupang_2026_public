package com.skygames.goodpang.ui.delivery

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

private val sourceFormat = SimpleDateFormat("MMM d, yyyy, h:mm:ss a", Locale.ENGLISH)
private val displayFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

private const val NARROW_NO_BREAK_SPACE = " "
private const val NO_BREAK_SPACE = " "

fun formatDeliveryStartDate(raw: String): String {
    // Depending on the JDK/ICU version, the server may render a narrow no-break
    // space or a no-break space before AM/PM instead of a regular space.
    val normalized = raw
        .replace(NARROW_NO_BREAK_SPACE, " ")
        .replace(NO_BREAK_SPACE, " ")
    return try {
        val parsed = sourceFormat.parse(normalized) ?: return raw
        displayFormat.format(parsed)
    } catch (e: ParseException) {
        raw
    }
}
