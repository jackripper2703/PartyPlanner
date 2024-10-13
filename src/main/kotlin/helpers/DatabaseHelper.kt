package org.example.helpers

import org.example.models.Event
import org.example.models.User
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException

object DatabaseHelper {

    private const val DB_URL = "jdbc:sqlite:src/main/data/my_database.db"

    private fun connect(): Connection? {
        return try {
            DriverManager.getConnection(DB_URL).also {
                println("SQLite TRUE")
            }
        } catch (e: SQLException) {
            println("SQLite FALSE: ${e.message}")
            null
        }
    }

    fun close() {
        connect()?.close()
    }

    fun createTables() {
        val sqlCreateEventTable = """
            CREATE TABLE IF NOT EXISTS events (
                id INTEGER PRIMARY KEY,
                date TEXT NOT NULL,
                name TEXT NOT NULL,
                description TEXT NOT NULL
            );
        """

        val sqlCreateUserTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY,
                userId INTEGER NOT NULL,
                username TEXT NOT NULL
            );
        """

        val sqlCreateWishlistsTable = """
            CREATE TABLE IF NOT EXISTS wish_lists (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                description TEXT NOT NULL
            );
        """

        val sqlCreateEventParticipantsTable = """
            CREATE TABLE IF NOT EXISTS event_participants (
                eventId TEXT NOT NULL,
                userId INTEGER NOT NULL,
                FOREIGN KEY (eventId) REFERENCES events(id),
                FOREIGN KEY (userId) REFERENCES users(id),
                PRIMARY KEY (eventId, userId)
            );
        """

        val sqlCreateUserWishlistsTable = """
            CREATE TABLE IF NOT EXISTS user_wish_lists (
                wishListId TEXT NOT NULL,
                userId INTEGER NOT NULL,
                FOREIGN KEY (wishListId) REFERENCES wish_lists(id),
                FOREIGN KEY (userId) REFERENCES users(id),
                PRIMARY KEY (wishListId, userId)
            );
        """

        connect()?.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute(sqlCreateUserTable)
                println("Таблица User создана или уже существует.")
                stmt.execute(sqlCreateEventTable)
                println("Таблица Event создана или уже существует.")
                stmt.execute(sqlCreateWishlistsTable)
                println("Таблица WishlLists создана или уже существует.")
                stmt.execute(sqlCreateEventParticipantsTable)
                println("Таблица EventParticipants создана или уже существует.")
                stmt.execute(sqlCreateUserWishlistsTable)
                println("Таблица UserWishLists создана или уже существует.")
            }
        }
    }

    fun insertUser(userId: Int, username: String) {
        val sql = "INSERT INTO users (userId, username) VALUES (?, ?)"
        connect()?.prepareStatement(sql)?.use { statement ->
            statement.setInt(1, userId)
            statement.setString(2, username)
            statement.executeUpdate()
        }
    }

    fun getUser(username: String): User? {
        val sql = "SELECT * FROM users WHERE username = ?"

        connect()?.prepareStatement(sql)?.use { statement ->
            statement.setString(1, username)
            val resultSet: ResultSet = statement.executeQuery()
            return if (resultSet.next()) {
                User(
                    userId = resultSet.getInt("userId").toLong(), username = resultSet.getString("username")
                )
            } else {
                null
            }
        } ?: return null
    }



    fun insertEvent(date: String, descriptionFull: String): Int? {
        val name = descriptionFull.split("\n").firstOrNull() ?: ""
        val description = descriptionFull.substringAfter("\n", "").trim()
        val sql = "INSERT INTO events (date, name, description) VALUES (?, ?, ?)"
        return connect()?.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)?.use { statement ->
            statement.setString(1, date)
            statement.setString(2, name)
            statement.setString(3, description)
            statement.executeUpdate()

            // Получаем сгенерированный ключ
            val generatedKeys = statement.generatedKeys
            if (generatedKeys.next()) {
                generatedKeys.getInt(1) // Возвращаем ID
            } else {
                null // Если не удалось получить ключ
            }
        }
    }

    fun getEvents(): List<Event> {
        val sql = "SELECT * FROM events"
        val events = mutableListOf<Event>()

        connect()?.prepareStatement(sql)?.use { statement ->
            val resultSet: ResultSet = statement.executeQuery()
            if (!resultSet.next()) {
                println("Таблица events пуста.")
                return emptyList()
            }
            do {
                events.add(
                    Event(
                        id = resultSet.getInt("id"),
                        date = resultSet.getString("date"),
                        name = resultSet.getString("name"),
                        description = resultSet.getString("description")
                    )
                )
            }while (resultSet.next())
        }
        return events
    }

    fun getEvent(id: Int): Event? {
        val sql = "SELECT * FROM events WHERE id = ?"

        connect()?.prepareStatement(sql)?.use { statement ->
            statement.setInt(1, id)
            val resultSet: ResultSet = statement.executeQuery()
            return if (resultSet.next()) {
                Event(
                    id = resultSet.getInt("id"),
                    date = resultSet.getString("date"),
                    name = resultSet.getString("name"),
                    description = resultSet.getString("description")
                )
            } else {
                null // Возвращаем null, если событие не найдено
            }
        } ?: return null // Возвращаем null, если соединение не установлено
    }


    fun getParticipantsByEvent(eventId: String): List<User> {
        val sql = """
        SELECT users.* 
        FROM users 
        JOIN event_participants 
        ON users.id = event_participants.userId 
        WHERE event_participants.eventId = ?
    """

        val participants = mutableListOf<User>()

        connect()?.prepareStatement(sql)?.use { statement ->
            statement.setString(1, eventId)
            val resultSet: ResultSet = statement.executeQuery()
            while (resultSet.next()) {
                participants.add(
                    User(
                        userId = resultSet.getInt("userId").toLong(),
                        username = resultSet.getString("username")
                    )
                )
            }
        }

        return participants
    }

    fun addUserToEvent(id: String, userId: Int) {
        val sql = "INSERT INTO event_participants (id, userId) VALUES (?, ?)"
        connect()?.prepareStatement(sql)?.use { statement ->
            statement.setString(1, id)
            statement.setInt(2, userId)
            statement.executeUpdate()
            println("Пользователь добавлен в мероприятие.")
        }
    }

    fun deleteEvent(id: Int): Boolean {
        val sql = "DELETE FROM events WHERE id = ?"

        connect()?.prepareStatement(sql)?.use { statement ->
            statement.setInt(1, id)
            val affectedRows = statement.executeUpdate()
            return affectedRows > 0
        } ?: return false
    }

}
