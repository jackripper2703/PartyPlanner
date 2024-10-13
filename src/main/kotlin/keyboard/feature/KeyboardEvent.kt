package org.example.keyboard.feature

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import org.example.helpers.DatabaseHelper.getEvents
import org.example.keyboard.Keyboard.buttonMain
import java.time.LocalDate

object KeyboardEvent {

    // Генерация клавиатуры с кнопками для событий
    fun createEventsKeyboard(): InlineKeyboardMarkup {
        val buttons = getEvents().map { event ->
            InlineKeyboardButton.CallbackData(
                text = event.name,
                callbackData = "eventID:${event.id}"
            )
        }.map { listOf(it) }

        val buttonsFull = buttons + listOf(listOf(buttonMain))
        return InlineKeyboardMarkup.create(buttonsFull)
    }


    fun createDateKeyboard(selectedDate: LocalDate): InlineKeyboardMarkup {
        val day = selectedDate.dayOfMonth
        val month = selectedDate.monthValue
        val year = selectedDate.year

        return InlineKeyboardMarkup.create(
            listOf(
                listOf(
                    InlineKeyboardButton.CallbackData("+", "incrementDay:$day:$month:$year"),
                    InlineKeyboardButton.CallbackData("+", "incrementMonth:$day:$month:$year"),
                    InlineKeyboardButton.CallbackData("+", "incrementYear:$day:$month:$year")
                ),
                listOf(
                    InlineKeyboardButton.CallbackData("$day", "noop"),
                    InlineKeyboardButton.CallbackData("$month", "noop"),
                    InlineKeyboardButton.CallbackData("$year", "noop")
                ),
                listOf(
                    InlineKeyboardButton.CallbackData("-", "decrementDay:$day:$month:$year"),
                    InlineKeyboardButton.CallbackData("-", "decrementMonth:$day:$month:$year"),
                    InlineKeyboardButton.CallbackData("-", "decrementYear:$day:$month:$year")
                ),
                listOf(
                    InlineKeyboardButton.CallbackData("Главное меню", "keyboardMain"),
                    InlineKeyboardButton.CallbackData("ОК", "confirmDate:$day:$month:$year")
                )
            )
        )
    }

}