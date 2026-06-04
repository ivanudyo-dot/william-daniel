package com.example.data.repository

import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AppRepository(
    private val userDao: UserDao,
    private val gameDao: GameDao,
    private val eventDao: EventDao,
    private val paymentDao: PaymentDao,
    private val chatDao: ChatDao,
    private val notificationDao: NotificationDao,
    private val mediaDao: MediaDao
) {
    // Flows
    val allUsers: Flow<List<User>> = userDao.getAllUsers()
    val allGames: Flow<List<Game>> = gameDao.getAllGames()
    val activeGames: Flow<List<Game>> = gameDao.getActiveGames()
    val allEvents: Flow<List<EventPost>> = eventDao.getAllEvents()
    val allPaymentsFlow: Flow<List<PaymentMethod>> = paymentDao.getAllPaymentsFlow()
    val allNotifications: Flow<List<NotificationItem>> = notificationDao.getAllNotifications()
    val allMedia: Flow<List<MediaUpload>> = mediaDao.getAllMedia()
    val allMessagesFlow: Flow<List<ChatMessage>> = chatDao.getAllMessagesFlow()

    // Thread messages
    fun getChatMessagesByThread(threadId: String): Flow<List<ChatMessage>> =
        chatDao.getChatMessagesByThread(threadId)

    // User operations
    suspend fun getUserById(userId: String): User? = userDao.getUserById(userId)
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    suspend fun updateUserStatus(userId: String, status: String) = userDao.updateUserStatus(userId, status)
    suspend fun deleteUser(userId: String) = userDao.deleteUser(userId)

    // Game operations
    suspend fun insertGame(game: Game) = gameDao.insertGame(game)
    suspend fun updateGame(game: Game) = gameDao.updateGame(game)
    suspend fun deleteGame(game: Game) = gameDao.deleteGame(game)
    suspend fun deleteGameById(gameId: Int) = gameDao.deleteGameById(gameId)

    // Event operations
    suspend fun insertEvent(event: EventPost) = eventDao.insertEvent(event)
    suspend fun updateEvent(event: EventPost) = eventDao.updateEvent(event)
    suspend fun deleteEvent(event: EventPost) = eventDao.deleteEvent(event)
    suspend fun deleteEventById(eventId: Int) = eventDao.deleteEventById(eventId)

    // Payment operations
    suspend fun insertPayment(payment: PaymentMethod) = paymentDao.insertPayment(payment)
    suspend fun updatePayment(payment: PaymentMethod) = paymentDao.updatePayment(payment)
    suspend fun getPaymentMethodById(id: String): PaymentMethod? = paymentDao.getPaymentMethodById(id)

    // Chat operations
    suspend fun insertMessage(message: ChatMessage) = chatDao.insertMessage(message)
    suspend fun clearThread(threadId: String) = chatDao.clearThread(threadId)

    // Notification operations
    suspend fun insertNotification(notification: NotificationItem) = notificationDao.insertNotification(notification)
    suspend fun markAsRead(notificationId: Int) = notificationDao.markAsRead(notificationId)
    suspend fun markAllAsRead() = notificationDao.markAllAsRead()
    suspend fun deleteNotification(notificationId: Int) = notificationDao.deleteNotification(notificationId)

    // Media operations
    suspend fun insertMedia(media: MediaUpload) = mediaDao.insertMedia(media)
    suspend fun deleteMedia(mediaId: Int) = mediaDao.deleteMedia(mediaId)

    // Seeding database
    suspend fun seedDatabaseIfEmpty() {
        // Users Seed
        val existingUsers = allUsers.firstOrNull() ?: emptyList()
        if (existingUsers.isEmpty()) {
            userDao.insertUser(User("admin", "William Daniel", "ADMIN", "ACTIVE", email = "admin@williamdaniel.com", passwordHash = "admin123"))
            userDao.insertUser(User("support", "Sarah Support", "SUPPORT", "ACTIVE", email = "support@williamdaniel.com", passwordHash = "support123"))
            userDao.insertUser(User("player", "LuckyGamer777", "PLAYER", "ACTIVE", email = "luckygamer@gmail.com", passwordHash = "player123"))
            userDao.insertUser(User("clover", "GoldClover", "PLAYER", "ACTIVE", email = "clover@gmail.com", passwordHash = "clover123"))
            userDao.insertUser(User("highroller", "HighRollerSlots", "PLAYER", "ACTIVE", email = "highroller@gmail.com", passwordHash = "highroller123"))
        }

        // Games Seed (20+ Games)
        val existingGames = allGames.firstOrNull() ?: emptyList()
        if (existingGames.isEmpty()) {
            val gamesList = listOf(
                Game(title = "Fire Kirin Classic", imageUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=400", gameUrl = "https://firekirin.xyz/"),
                Game(title = "Orion Stars VIP", imageUrl = "https://images.unsplash.com/photo-1538481199705-c710c4e965fc?w=400", gameUrl = "https://orionstars.vip:8588/"),
                Game(title = "Game Vault Gold", imageUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=400", gameUrl = "https://gamevault.vip/"),
                Game(title = "Juwa Winner", imageUrl = "https://images.unsplash.com/photo-1612287230202-1bf1d85d1bdf?w=400", gameUrl = "https://juwamember.com/"),
                Game(title = "Milky Way Sweeps", imageUrl = "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=400", gameUrl = "https://milkywayapp.xyz/"),
                Game(title = "Vegas Sweeps Club", imageUrl = "https://images.unsplash.com/photo-1596838132731-3301c3fd4317?w=400", gameUrl = "https://vegassweeps.vip/"),
                Game(title = "River Sweeps VIP", imageUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=400", gameUrl = "https://riversweeps.org/"),
                Game(title = "Ultra Monster Sweepstakes", imageUrl = "https://images.unsplash.com/photo-1552820728-8b83bb6b773f?w=400", gameUrl = "https://ultramonsterapp.com/"),
                Game(title = "Golden Dragon Club", imageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400", gameUrl = "https://goldendragon777.com/"),
                Game(title = "Panda Master Sweeps", imageUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=400", gameUrl = "https://pandamasterapp.com/"),
                Game(title = "V Blink Deluxe", imageUrl = "https://images.unsplash.com/photo-1551103782-8ab07afd45c1?w=400", gameUrl = "https://vblink777.club/"),
                Game(title = "Buffalo Gold Megaways", imageUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=400", gameUrl = "https://firekirin.xyz/", category = "Slots"),
                Game(title = "Moby Dick Fish Hunt", imageUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=400", gameUrl = "https://gamevault.vip/", category = "Fish"),
                Game(title = "Cleopatra Diamonds", imageUrl = "https://images.unsplash.com/photo-1596838132731-3301c3fd4317?w=400", gameUrl = "https://vegassweeps.vip/", category = "Slots"),
                Game(title = "God of Fortune Reels", imageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400", gameUrl = "https://goldendragon777.com/", category = "Slots"),
                Game(title = "Kraken Revenge Fish King", imageUrl = "https://images.unsplash.com/photo-1612287230202-1bf1d85d1bdf?w=400", gameUrl = "https://juwamember.com/", category = "Fish"),
                Game(title = "Wild Joker Cards", imageUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=400", gameUrl = "https://riversweeps.org/", category = "Cards"),
                Game(title = "Super Cherry Classic", imageUrl = "https://images.unsplash.com/photo-1596838132731-3301c3fd4317?w=400", gameUrl = "https://vegassweeps.vip/", category = "Slots"),
                Game(title = "Ocean King 4", imageUrl = "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=400", gameUrl = "https://milkywayapp.xyz/", category = "Fish"),
                Game(title = "Throne of Zeus", imageUrl = "https://images.unsplash.com/photo-1538481199705-c710c4e965fc?w=400", gameUrl = "https://orionstars.vip:8588/", category = "Slots"),
                Game(title = "Golden Shamrock Slot", imageUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=400", gameUrl = "https://gamevault.vip/", category = "Slots"),
                Game(title = "Double Double Bonus Video Poker", imageUrl = "https://images.unsplash.com/photo-1552820728-8b83bb6b773f?w=400", gameUrl = "https://vblink777.club/", category = "Cards")
            )
            for (game in gamesList) {
                gameDao.insertGame(game)
            }
        }

        // Payments Seed
        val existingPayments = allPaymentsFlow.firstOrNull() ?: emptyList()
        if (existingPayments.isEmpty()) {
            paymentDao.insertPayment(
                PaymentMethod(
                    id = "chime",
                    displayName = "Chime",
                    tag = "williamgaming_chime",
                    instructions = "Deposit Instructions:\n1. Open your Chime App.\n2. Search Pay Anyone Username: williamgaming_chime\n3. Note: Enter Player Username inside note section\n4. Type correct amount and send.\n5. Screenshot confirmation, send here in Support Chat for 5-minute load credit!",
                    qrImageUrl = "chime_qr",
                    paymentPhotoUrl = "chime_card",
                    isActive = true
                )
            )
            paymentDao.insertPayment(
                PaymentMethod(
                    id = "cashapp",
                    displayName = "Cash App",
                    tag = "\$WilliamGaming77",
                    instructions = "Deposit Instructions:\n1. Open Cash App.\n2. In Send Field enter: \$WilliamGaming77\n3. Add Note: your gaming app username (e.g., JaneDoe_Juwa)\n4. Submit and take screenshot.\n5. Post receipt screenshot to customer service chat.\nLoads take 1-3 minutes!",
                    qrImageUrl = "cashapp_qr",
                    paymentPhotoUrl = "cashapp_card",
                    isActive = true
                )
            )
            paymentDao.insertPayment(
                PaymentMethod(
                    id = "venmo",
                    displayName = "Venmo",
                    tag = "@WilliamGamingClub",
                    instructions = "Deposit Instructions:\n1. Send Venmo to: @WilliamGamingClub\n2. Do NOT type any gambling-related terms. Use emoji '👍' or 'Dinner'.\n3. Mark payment Private.\n4. Send receipt snapshot to gaming chat.\nAccount load is immediate upon validation.",
                    qrImageUrl = "venmo_qr",
                    paymentPhotoUrl = "venmo_card",
                    isActive = true
                )
            )
            paymentDao.insertPayment(
                PaymentMethod(
                    id = "paypal",
                    displayName = "PayPal",
                    tag = "williamgaming_paypal@gmail.com",
                    instructions = "Deposit Instructions:\n1. Go to PayPal, select Send Money.\n2. Recipient: williamgaming_paypal@gmail.com\n3. CRITICAL: Select 'Sending to Friends & Family' so money isn't put on hold!\n4. Enter player account name in personal message.\n5. Send transaction ID / screenshot in Support Support chat.",
                    qrImageUrl = "paypal_qr",
                    paymentPhotoUrl = "paypal_card",
                    isActive = true
                )
            )
        }

        // Daily Events Seed
        val existingEvents = allEvents.firstOrNull() ?: emptyList()
        if (existingEvents.isEmpty()) {
            eventDao.insertEvent(
                EventPost(
                    title = "🔥 Daily Load Bonus - Get 20% EXTRA on all cash app deposits!",
                    description = "Limited time offer for William Daniel Gaming club members! Deposit \$50 or more with Cash App today and get a massive 20% extra game credits loaded to your favorite platform (Fire Kirin, Orion Stars, Game Vault). Apply in live chat support instantly.",
                    imageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600",
                    isPinned = true,
                    postType = "PROMO",
                    rewardAmount = "20% Load Bonus"
                )
            )
            eventDao.insertEvent(
                EventPost(
                    title = "🏆 Massive Hitting Game: kirin King Fisherman!",
                    description = "Player GamerPro 777 hit the giant dragon on Kirin King Fisherman game in Juwa platform today, taking home a massive \$1,450.00 jackpot cashout! Slots and Fish games are hitting super HOT this evening. Play now to get lucky!",
                    imageUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=600",
                    isPinned = false,
                    postType = "WINNING",
                    gameName = "Juwa Winner",
                    rewardAmount = "Won $1,450.00"
                )
            )
            eventDao.insertEvent(
                EventPost(
                    title = "💰 Quick Cashout: $800 paid to player GoldClover!",
                    description = "Congratulations GoldClover! Redeemed \$800 on Orion Stars tonight and paid instantly via Chime Pay. Clean, safe, and super fast loads/cashouts guaranteed 24/7. Thank you for gaming with William Daniel!",
                    imageUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600",
                    isPinned = false,
                    postType = "CASHOUT",
                    gameName = "Orion Stars VIP",
                    rewardAmount = "Paid Out $800"
                )
            )
        }

        // Welcome notifications & Chat Messages
        val existingMessages = allMessagesFlow.firstOrNull() ?: emptyList()
        if (existingMessages.isEmpty()) {
            chatDao.insertMessage(
                ChatMessage(
                    threadId = "global",
                    senderId = "admin",
                    senderName = "William Daniel",
                    senderRole = "ADMIN",
                    text = "Welcome to William Daniel Gaming platform! Direct chat is active 24/7. Enjoy high-hitting games!",
                    timestamp = System.currentTimeMillis() - 3600000
                )
            )
            chatDao.insertMessage(
                ChatMessage(
                    threadId = "player",
                    senderId = "admin",
                    senderName = "William Daniel",
                    senderRole = "ADMIN",
                    text = "Hello! I am William, your administrator. If you need any deposits, cashouts, or fresh game accounts for Fire Kirin, Orion Stars, Game Vault or Juwa, message me here anytime!",
                    timestamp = System.currentTimeMillis() - 10000
                )
            )
        }

        val existingNotifications = allNotifications.firstOrNull() ?: emptyList()
        if (existingNotifications.isEmpty()) {
            notificationDao.insertNotification(
                NotificationItem(
                    title = "⚡ Welcome to William Daniel Gaming",
                    message = "We are glad to have you aboard. Enjoy 24/7 fast loading, real-time customer support, and instant payouts via Cash App, Chime, Venmo, and PayPal.",
                    type = "ANNOUNCEMENT"
                )
            )
            notificationDao.insertNotification(
                NotificationItem(
                    title = "🔥 20% Cash App Bonus Active",
                    message = "Check out our Daily Events board! Get 20% match on deposits \$50+.",
                    type = "PROMOTION"
                )
            )
        }
    }
}
