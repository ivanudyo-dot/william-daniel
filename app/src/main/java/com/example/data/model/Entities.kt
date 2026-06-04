package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String, // username / user-id
    val displayName: String,
    val role: String, // "PLAYER", "ADMIN", "SUPPORT"
    val status: String, // "ACTIVE", "SUSPENDED"
    val lastActive: Long = System.currentTimeMillis(),
    val email: String = "",
    val passwordHash: String = ""
)

@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val imageUrl: String,
    val gameUrl: String,
    val isEnabled: Boolean = true,
    val category: String = "Slots" // "Slots", "Cards", "Fish", "Hottest"
)

@Entity(tableName = "daily_events")
data class EventPost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val imageUrl: String, // Banner or screenshot file path/URL
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val postType: String = "EVENT", // "PROMO", "WINNING", "CASHOUT", "ANNOUNCEMENT"
    val gameName: String = "",
    val rewardAmount: String = "" // Optional reward text e.g. "$500 Win"
)

@Entity(tableName = "payment_methods")
data class PaymentMethod(
    @PrimaryKey val id: String, // "chime", "cashapp", "venmo", "paypal"
    val displayName: String,
    val tag: String, // Username / Cashtag / Tag
    val instructions: String,
    val qrImageUrl: String,
    val paymentPhotoUrl: String,
    val isActive: Boolean = true
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val threadId: String, // Maps to userId for player-admin threads (global chat has threadId = "global")
    val senderId: String,
    val senderName: String,
    val senderRole: String, // "PLAYER", "ADMIN", "SUPPORT", "AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isAiResponse: Boolean = false,
    val isHumanTakeover: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val type: String, // "PUSH", "EVENT", "PROMOTION", "CHAT", "ANNOUNCEMENT"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "media_uploads")
data class MediaUpload(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String,
    val localUriOrUrl: String,
    val category: String, // "BANNER", "POSTER", "WINNING", "CUSTOM"
    val timestamp: Long = System.currentTimeMillis()
)
