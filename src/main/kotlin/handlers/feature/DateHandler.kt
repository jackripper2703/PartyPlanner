package org.example.handlers.feature

import com.github.kotlintelegrambot.Bot
import org.example.handlers.editMessage
import org.example.keyboard.feature.KeyboardEvent.createDateKeyboard
import java.time.LocalDate

fun handleDateAdjustment(
    bot: Bot, data: String, userId: Long, messageId: Long?
) {
    val splitData = data.split(":")
    val action = splitData[0]
    var day = splitData[1].toInt()
    var month = splitData[2].toInt()
    var year = splitData[3].toInt()

    val maxDaysInMonth = LocalDate.of(year, month, 1).lengthOfMonth()

    when (action) {
        "incrementDay" -> if (day < maxDaysInMonth) day++ else day = 1
        "decrementDay" -> if (day > 1) day-- else day = maxDaysInMonth

        "incrementMonth" -> {
            month = if (month < 12) month + 1 else 1
            day = minOf(day, LocalDate.of(year, month, 1).lengthOfMonth())
        }

        "decrementMonth" -> {
            month = if (month > 1) month - 1 else 12
            day = minOf(day, LocalDate.of(year, month, 1).lengthOfMonth())
        }

        "incrementYear" -> {
            year++
            day = minOf(day, LocalDate.of(year, month, 1).lengthOfMonth())
        }

        "decrementYear" -> {
            year--
            day = minOf(day, LocalDate.of(year, month, 1).lengthOfMonth())
        }
    }

    editMessage(bot, userId, messageId, "Дата: $day:$month:$year", createDateKeyboard(LocalDate.of(year, month, day)))
}