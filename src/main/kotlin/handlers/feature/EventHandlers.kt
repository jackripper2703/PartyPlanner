package org.example.handlers.feature

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import org.example.handlers.editMessage
import org.example.helpers.DatabaseHelper.deleteEvent
import org.example.helpers.DatabaseHelper.getEvent
import org.example.helpers.DatabaseHelper.getEvents
import org.example.helpers.EnvVariables.chatId
import org.example.keyboard.Keyboard.buttonEventsCreate
import org.example.keyboard.Keyboard.buttonMain
import org.example.keyboard.Keyboard.keyboardBack
import org.example.keyboard.feature.KeyboardEvent.createDateKeyboard
import org.example.keyboard.feature.KeyboardEvent.createEventsKeyboard
import org.example.state.EventState
import java.time.LocalDate

fun handleEventQuery(bot: Bot, data: String, userId: Long, messageId: Long?) {
    when {

        data == "eventsList" -> {
            if (getEvents().isNotEmpty()) {

                bot.editMessageText(
                    chatId = ChatId.fromId(userId),
                    messageId = messageId,
                    text = "Список мероприятий:\n",
                    replyMarkup = createEventsKeyboard()
                )
            } else {
                bot.editMessageText(
                    chatId = ChatId.fromId(userId),
                    messageId = messageId,
                    text = "Список мероприятий пустой!",
                    replyMarkup = InlineKeyboardMarkup.create(
                        listOf(
                            listOf(buttonEventsCreate),
                            listOf(buttonMain))
                    )
                )
            }
        }


        data.startsWith("eventID:") -> {
            val eventID = data.split(":")[1].toInt()
            val event = getEvent(eventID)
            val messageText = """
                |Дата события: ${event?.date ?: "Не найдено"}
                |Описание:
                |${event?.description ?: "Не найдено"}
                """.trimMargin()

            editMessage(
                bot, userId, messageId, messageText, InlineKeyboardMarkup.create(
                    listOf(
                        listOf(
                            InlineKeyboardButton.CallbackData(
                                text = "Удалить событие", callbackData = "deleteEvent:${event!!.id}"
                            ), buttonMain
                        )
                    )
                )
            )

        }

        data.startsWith("deleteEvent:") -> {
            val id = data.split(":")[1].toInt()
            val event = getEvent(id)

            bot.sendMessage(
                chatId = ChatId.fromId(chatId),
                text = """
                    |Событие отменилось:
                    |
                    |${event!!.name}
                    |
                    |Дата: ${event.date}
                    """.trimMargin()
            )

            deleteEvent(id)
            editMessage(
                bot, userId, messageId,
                "Событие с ID ${id} удалено.",
                keyboardBack
            )

        }

        data == "eventCreate" ->
            editMessage(
                bot, userId, messageId,
                "Дата: сейчас ) ",
                createDateKeyboard(LocalDate.now())
            )

        data.startsWith("incrementDay") || data.startsWith("decrementDay") ||
                data.startsWith("incrementMonth") || data.startsWith("decrementMonth") ||
                data.startsWith("incrementYear") || data.startsWith("decrementYear") -> {
            handleDateAdjustment(bot, data, userId, messageId)
        }

        data.startsWith("confirmDate:") -> {
            val splitData = data.split(":")
            val selectedDay = splitData[1]
            val selectedMonth = splitData[2]
            val selectedYear = splitData[3]
            val selectedDate = "$selectedDay:$selectedMonth:$selectedYear"
            EventState.selectedDate = selectedDate
            EventState.isWaitingForDescription = true

            editMessage(
                bot, userId, messageId, """
                    |Дата выбрана: $selectedDate.
                    |Пожалуйста, введите описание мероприятия:
                    |!!!Первое слово будет названием!!!
                """.trimMargin(), keyboardBack
            )
        }

    }
}
