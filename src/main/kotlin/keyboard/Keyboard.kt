package org.example.keyboard

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton

object Keyboard {

    val MOCK = "Пока еще не сделал, держу как макет"

    // Мероприятия
    val buttonEventsList = InlineKeyboardButton.CallbackData("Список мероприятий", "eventsList")
    val buttonEventsCreate = InlineKeyboardButton.CallbackData("Создать новое мероприятие", "eventCreate")

    val buttonSecretSanta = InlineKeyboardButton.CallbackData("Тайный Санта", "secretSanta")
    val buttonWishList = InlineKeyboardButton.CallbackData("Желания", "wishList")

    // Общая кнопка возврата в главное меню
    val buttonMain = InlineKeyboardButton.CallbackData("Главное меню", "keyboardMain")

    // Главное меню
    val keyboardMain = InlineKeyboardMarkup.create(
        listOf(
            listOf(buttonEventsCreate),
            listOf(buttonEventsList),
            listOf(buttonSecretSanta),
            listOf(buttonWishList),
        )
    )

    // Клавиатура с кнопкой "Главное меню"
    val keyboardBack = createSingleButtonKeyboard(buttonMain)

    // Создание клавиатуры с одной кнопкой
    fun createSingleButtonKeyboard(button: InlineKeyboardButton): InlineKeyboardMarkup {
        return InlineKeyboardMarkup.create(listOf(listOf(button)))
    }
}
