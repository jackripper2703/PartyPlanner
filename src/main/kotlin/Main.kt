package org.example

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import org.example.helpers.ConfigHelper

fun main() {
    val mock = "Я еще не вырос"

    val chatId = ConfigHelper.getProperty("chat_id")!!.toLong()


    val keyboard00 = InlineKeyboardMarkup.create(
        listOf(
            listOf(InlineKeyboardButton.CallbackData("Мероприятий", "events")),
            listOf(InlineKeyboardButton.CallbackData("Создания голосования", "vote")),
            listOf(InlineKeyboardButton.CallbackData("Запуск тайного Санты", "secretSanta")),
            listOf(InlineKeyboardButton.CallbackData("Список желаний", "wishList")),
        )
    )

    val keyboard01 = InlineKeyboardMarkup.create(
        listOf(
            listOf(InlineKeyboardButton.CallbackData("Актуальные мероприятия", "events_active")),
            listOf(InlineKeyboardButton.CallbackData("Создать новое мероприятие", "events_create")),
            listOf(InlineKeyboardButton.CallbackData("Отменить мероприятие", "events_cancel")),
            listOf(InlineKeyboardButton.CallbackData("Главное меню", "main")),
        )
    )

    val keyboard02 = InlineKeyboardMarkup.create(
        listOf(
            listOf(InlineKeyboardButton.CallbackData("Новое желание", "wishList_create")),
            listOf(InlineKeyboardButton.CallbackData("Список желаний", "wishList_list")),
            listOf(InlineKeyboardButton.CallbackData("Удалить желание", "wishList_delete")),
            listOf(InlineKeyboardButton.CallbackData("Главное меню", "main")),
        )
    )
    val bot = bot {
        token = ConfigHelper.getProperty("bot.token")
            ?: throw IllegalArgumentException("Токен бота не найден в конфигурационном файле.")

        dispatch {
            text {
                val command = message.text ?: return@text

                if (message.chat.type.toString() == "private") {
                    when (command) {
                        "/start" -> {
                            bot.sendMessage(
                                chatId = ChatId.fromId(message.chat.id),
                                text = "Выберите действие:\n",
                                replyMarkup = keyboard00
                            )
                        }

                        else -> {
                            bot.sendMessage(
                                chatId = ChatId.fromId(message.chat.id),
                                text = "Нажмите /start, чтобы увидеть доступные команды."
                            )
                        }
                    }
                } else {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Я могу обрабатывать команды только в личных сообщениях."
                    )
                }
            }


            callbackQuery {
                val userId = callbackQuery.from.id

                // Получаем текущее сообщение, на которое нажали кнопку
                val messageId = callbackQuery.message?.messageId ?: return@callbackQuery

                when (callbackQuery.data) {
                    "main" -> {
                        bot.editMessageText(
                            chatId = ChatId.fromId(userId),
                            messageId = messageId,
                            text = "Выберите действие:\n",
                            replyMarkup = keyboard00
                        )
                    }
                    "events" -> {
                        bot.editMessageText(
                            chatId = ChatId.fromId(userId),
                            messageId = messageId,
                            text = "Вы выбрали: Список мероприятий",
                            replyMarkup = keyboard01
                        )
                    }

                    "vote" -> {
                        bot.editMessageText(
                            chatId = ChatId.fromId(userId),
                            messageId = messageId,
                            text = "Вы выбрали: Голосование",
                            replyMarkup = keyboard00
                        )
                    }

                    "secretSanta" -> {
                        bot.editMessageText(
                            chatId = ChatId.fromId(userId),
                            messageId = messageId,
                            text = "Вы выбрали: Запуск тайного Санты",
                            replyMarkup = keyboard00
                        )
                    }

                    "wishList" -> {
                        bot.editMessageText(
                            chatId = ChatId.fromId(userId),
                            messageId = messageId,
                            text = "Вы выбрали: Список желаний",
                            replyMarkup = keyboard02
                        )
                    }
                }
                bot.answerCallbackQuery(callbackQuery.id)
            }
        }
    }
    bot.startPolling()
}