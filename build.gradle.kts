plugins {
    kotlin("jvm") version "1.9.23" // Убедитесь, что версия актуальна
    application
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.2.0") // Проверьте последнюю версию
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

}

application {
    mainClass.set("org.example.TgBot") // Замените на ваш основной класс
}
