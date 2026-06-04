package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY lastActive DESC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET status = :status WHERE id = :userId")
    suspend fun updateUserStatus(userId: String, status: String)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)
}

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY id ASC")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE isEnabled = 1 ORDER BY id ASC")
    fun getActiveGames(): Flow<List<Game>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game)

    @Update
    suspend fun updateGame(game: Game)

    @Delete
    suspend fun deleteGame(game: Game)

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGameById(gameId: Int)
}

@Dao
interface EventDao {
    @Query("SELECT * FROM daily_events ORDER BY isPinned DESC, timestamp DESC")
    fun getAllEvents(): Flow<List<EventPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventPost)

    @Update
    suspend fun updateEvent(event: EventPost)

    @Delete
    suspend fun deleteEvent(event: EventPost)

    @Query("DELETE FROM daily_events WHERE id = :eventId")
    suspend fun deleteEventById(eventId: Int)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payment_methods")
    fun getAllPaymentsFlow(): Flow<List<PaymentMethod>>

    @Query("SELECT * FROM payment_methods WHERE id = :methodId LIMIT 1")
    suspend fun getPaymentMethodById(methodId: String): PaymentMethod?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentMethod)

    @Update
    suspend fun updatePayment(payment: PaymentMethod)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE threadId = :threadId ORDER BY timestamp ASC")
    fun getChatMessagesByThread(threadId: String): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllMessagesFlow(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages WHERE threadId = :threadId")
    suspend fun clearThread(threadId: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: Int)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications WHERE id = :notificationId")
    suspend fun deleteNotification(notificationId: Int)
}

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_uploads ORDER BY timestamp DESC")
    fun getAllMedia(): Flow<List<MediaUpload>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: MediaUpload)

    @Query("DELETE FROM media_uploads WHERE id = :mediaId")
    suspend fun deleteMedia(mediaId: Int)
}
