package org.example.helpers

import io.github.cdimascio.dotenv.Dotenv

object EnvVariables {

    private fun loadEnvVariables(): Dotenv {
        return Dotenv.load()
    }

    val dotenv = loadEnvVariables()

    val botToken = dotenv["TOKEN"] ?: error("Bot token not found in .env")
    val chatId = dotenv["CHAT_ID"].toLong()

}