package com.example.util

import java.text.NumberFormat
import java.util.Locale

enum class AppLanguage {
    BANGLA,
    ENGLISH
}

object LanguageHelper {

    private val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    fun toBanglaDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(banglaDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun formatNumber(number: Int, isBangla: Boolean): String {
        val formatted = NumberFormat.getNumberInstance(Locale.US).format(number)
        return if (isBangla) toBanglaDigits(formatted) else formatted
    }

    fun formatTaka(amount: Int, isBangla: Boolean): String {
        val numStr = formatNumber(amount, isBangla)
        return "৳ $numStr"
    }

    fun formatWeight(kg: Int, isBangla: Boolean): String {
        val kgStr = if (isBangla) toBanglaDigits(kg.toString()) else kg.toString()
        val unit = if (isBangla) "কেজি" else "kg"
        return "$kgStr $unit"
    }
}
