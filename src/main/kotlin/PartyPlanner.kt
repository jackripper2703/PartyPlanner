package org.example

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import org.example.handlers.handleCallbackQuery
import org.example.handlers.MessageHandler
import com.github.kotlintelegrambot.dispatcher.text
import org.example.helpers.DatabaseHelper
import org.example.helpers.EnvVariables.botToken


fun main() {
    DatabaseHelper.createTables()
    val bot = bot {
        token = botToken

        dispatch {
            text {
                MessageHandler.handleTextMessage(bot, message)
            }
            callbackQuery {
                handleCallbackQuery(bot).invoke(this)
            }
        }
    }

    try {
        bot.startPolling()
    } finally {
        DatabaseHelper.close()
    }
}
