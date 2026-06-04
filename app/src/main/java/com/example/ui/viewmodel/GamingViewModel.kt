package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.AppRepository
import com.example.data.gemini.GeminiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GamingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(
            userDao = database.userDao(),
            gameDao = database.gameDao(),
            eventDao = database.eventDao(),
            paymentDao = database.paymentDao(),
            chatDao = database.chatDao(),
            notificationDao = database.notificationDao(),
            mediaDao = database.mediaDao()
        )

        // Seed DB on background thread
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.seedDatabaseIfEmpty()
            } catch (e: Exception) {
                Log.e("GamingViewModel", "Database seeding failed", e)
            }
        }
    }

    // Role, Login & Tab state
    private val _currentUserId = MutableStateFlow("")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Active screen navigation tab
    private val _playerActiveTab = MutableStateFlow("Home")
    val playerActiveTab: StateFlow<String> = _playerActiveTab.asStateFlow()

    private val _adminActiveTab = MutableStateFlow("Dashboard")
    val adminActiveTab: StateFlow<String> = _adminActiveTab.asStateFlow()

    // Global Notification Banner State
    private val _inAppNotification = MutableStateFlow<NotificationItem?>(null)
    val inAppNotification: StateFlow<NotificationItem?> = _inAppNotification.asStateFlow()

    // Search and filters
    val userSearchQuery = MutableStateFlow("")
    val gameSearchQuery = MutableStateFlow("")
    val eventCategoryFilter = MutableStateFlow("ALL")

    // Admin Status
    private val _isAdminOnline = MutableStateFlow(true)
    val isAdminOnline: StateFlow<Boolean> = _isAdminOnline.asStateFlow()

    // Human Takeover state map (threadId -> isHumanTakeover)
    private val _threadTakeoverStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val threadTakeoverStates: StateFlow<Map<String, Boolean>> = _threadTakeoverStates.asStateFlow()

    // Selected Active Chat Thread (for Admin view)
    private val _selectedChatThreadId = MutableStateFlow("player")
    val selectedChatThreadId: StateFlow<String> = _selectedChatThreadId.asStateFlow()

    // Reactive database data flows
    val allUsers: StateFlow<List<User>> = repository.allUsers
        .combine(userSearchQuery) { users, query ->
            if (query.isBlank()) users
            else users.filter { it.displayName.contains(query, ignoreCase = true) || it.id.contains(query, ignoreCase = true) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGames: StateFlow<List<Game>> = repository.allGames
        .combine(gameSearchQuery) { games, query ->
            if (query.isBlank()) games
            else games.filter { it.title.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeGames: StateFlow<List<Game>> = repository.activeGames
        .combine(gameSearchQuery) { games, query ->
            if (query.isBlank()) games
            else games.filter { it.title.contains(query, ignoreCase = true) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEvents: StateFlow<List<EventPost>> = repository.allEvents
        .combine(eventCategoryFilter) { events, filter ->
            if (filter == "ALL") events
            else events.filter { it.postType == filter }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<PaymentMethod>> = repository.allPaymentsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<NotificationItem>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMedia: StateFlow<List<MediaUpload>> = repository.allMedia
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMessagesList: StateFlow<List<ChatMessage>> = repository.allMessagesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently observed messages flow
    private val _currentThreadId = MutableStateFlow("global")
    val currentThreadId: StateFlow<String> = _currentThreadId.asStateFlow()

    val currentChatMessages: StateFlow<List<ChatMessage>> = _currentThreadId
        .flatMapLatest { threadId -> repository.getChatMessagesByThread(threadId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Messages List for specific User Thread
    val adminSelectedChatMessages: StateFlow<List<ChatMessage>> = _selectedChatThreadId
        .flatMapLatest { threadId -> repository.getChatMessagesByThread(threadId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // App starts logged out, prompting the user for custom secure logins
    }

    // Sign out management
    fun userSignOut() {
        _currentUserId.value = ""
        _currentUser.value = null
    }

    // Credential-based login (handles player emails or usernames and administrator accounts)
    fun loginWithCredentials(
        emailOrUserId: String,
        passwordText: String,
        asAdmin: Boolean,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val trimmedInput = emailOrUserId.trim()
            val trimmedPassword = passwordText.trim()
            if (trimmedInput.isEmpty() || trimmedPassword.isEmpty()) {
                onResult(false, "Username/Email and Password cannot be empty.")
                return@launch
            }

            // Fallback: If database is completely empty and no users, seed it now so defaults exist
            repository.seedDatabaseIfEmpty()

            val allUsersList = repository.allUsers.firstOrNull() ?: emptyList()
            val matchedUser = allUsersList.find { 
                (it.id.equals(trimmedInput, ignoreCase = true) || it.email.equals(trimmedInput, ignoreCase = true)) && 
                it.passwordHash == trimmedPassword
            }

            if (matchedUser != null) {
                if (asAdmin && matchedUser.role == "PLAYER") {
                    onResult(false, "This portal requires Administrator credentials.")
                    return@launch
                }
                if (matchedUser.status == "SUSPENDED") {
                    onResult(false, "Your account has been suspended by administration. Support contact required.")
                    return@launch
                }

                _currentUserId.value = matchedUser.id
                _currentUser.value = matchedUser
                repository.updateUser(matchedUser.copy(lastActive = System.currentTimeMillis()))
                
                if (matchedUser.role == "PLAYER") {
                    _currentThreadId.value = matchedUser.id
                } else {
                    _currentThreadId.value = "global"
                }
                onResult(true, "Welcome back, ${matchedUser.displayName}!")
            } else {
                onResult(false, "Invalid username/email or password formulation.")
            }
        }
    }

    // Gmail-address sign up with password
    fun signUpUser(
        email: String,
        name: String,
        passwordText: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val trimmedEmail = email.trim()
            val trimmedName = name.trim()
            val trimmedPassword = passwordText.trim()

            if (trimmedEmail.isEmpty() || trimmedName.isEmpty() || trimmedPassword.isEmpty()) {
                onResult(false, "All registration fields are required.")
                return@launch
            }

            if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
                onResult(false, "Please provide a valid Gmail / Email address.")
                return@launch
            }

            val username = trimmedEmail.substringBefore("@")
            val allUsersList = repository.allUsers.firstOrNull() ?: emptyList()
            val exists = allUsersList.any { 
                it.id.equals(username, ignoreCase = true) || it.email.equals(trimmedEmail, ignoreCase = true) 
            }

            if (exists) {
                onResult(false, "Account with this email already exists. Try signing in!")
            } else {
                val newUser = User(
                    id = username,
                    displayName = trimmedName,
                    role = "PLAYER",
                    status = "ACTIVE",
                    email = trimmedEmail,
                    passwordHash = trimmedPassword
                )
                repository.insertUser(newUser)
                _currentUserId.value = username
                _currentUser.value = newUser
                _currentThreadId.value = username
                onResult(true, "Successfully registered! Welcome, $trimmedName.")
            }
        }
    }

    // Google Secure OAuth Sign In simulation
    fun loginWithGoogle(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val randomNumber = (100..999).random()
            val testGmail = "player$randomNumber@gmail.com"
            val username = "google_player_$randomNumber"
            val displayName = "LuckyPlayer $randomNumber"

            val newUser = User(
                id = username,
                displayName = displayName,
                role = "PLAYER",
                status = "ACTIVE",
                email = testGmail,
                passwordHash = "google_auth_sso_verified"
            )
            repository.insertUser(newUser)
            _currentUserId.value = username
            _currentUser.value = newUser
            _currentThreadId.value = username
            onResult(true, "Successfully authenticated with Google account: $testGmail")
        }
    }

    // Login management
    fun handleUserLogin(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUserById(userId)
            if (user != null) {
                _currentUserId.value = userId
                _currentUser.value = user
                repository.updateUser(user.copy(lastActive = System.currentTimeMillis()))
                // Set default chat thread
                if (user.role == "PLAYER") {
                    _currentThreadId.value = userId
                } else {
                    _currentThreadId.value = "global"
                }
            } else {
                // If user doesn't exist, create player account
                val newPlayer = User(userId, userId.replaceFirstChar { it.uppercase() }, "PLAYER", "ACTIVE", email = "$userId@gmail.com", passwordHash = "${userId}123")
                repository.insertUser(newPlayer)
                _currentUserId.value = userId
                _currentUser.value = newPlayer
                _currentThreadId.value = userId
            }
        }
    }

    fun setPlayerActiveTab(tab: String) {
        _playerActiveTab.value = tab
    }

    fun setAdminActiveTab(tab: String) {
        _adminActiveTab.value = tab
    }

    fun setSelectedChatThread(threadId: String) {
        _selectedChatThreadId.value = threadId
    }

    fun toggleAdminOnlineStatus() {
        _isAdminOnline.value = !_isAdminOnline.value
        val statusText = if (_isAdminOnline.value) "Online" else "Offline"
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertMessage(
                ChatMessage(
                    threadId = "global",
                    senderId = "system",
                    senderName = "System",
                    senderRole = "SUPPORT",
                    text = "Admin William Daniel status is now: $statusText"
                )
            )
        }
    }

    fun clearInAppNotification() {
        _inAppNotification.value = null
    }

    // --- A. PLAYER MANAGEMENT ---
    fun suspendUser(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserStatus(userId, "SUSPENDED")
            triggerSystemNotification("User Suspended", "User account '$userId' has been suspended by administration.")
        }
    }

    fun activateUser(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserStatus(userId, "ACTIVE")
            triggerSystemNotification("User Restored", "User account '$userId' has been fully restored.")
        }
    }

    // --- B. GAME MANAGEMENT ---
    fun addGame(title: String, imageUrl: String, gameUrl: String, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newGame = Game(title = title, imageUrl = imageUrl, gameUrl = gameUrl, category = category)
            repository.insertGame(newGame)
            triggerSystemNotification(
                title = "🎮 New Game Added!",
                message = "Play the newly added '$title' instantly inside the Games tab!",
                type = "EVENT"
            )
        }
    }

    fun updateGame(game: Game) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateGame(game)
            triggerSystemNotification("Game Updated", "${game.title} details updated successfully.")
        }
    }

    fun deleteGame(game: Game) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteGame(game)
            triggerSystemNotification("Game Removed", "${game.title} has been removed.")
        }
    }

    fun toggleGameActive(gameId: Int, isEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val games = repository.allGames.firstOrNull() ?: emptyList()
            val game = games.find { it.id == gameId }
            if (game != null) {
                repository.updateGame(game.copy(isEnabled = isEnabled))
                val status = if (isEnabled) "Enabled" else "Disabled"
                triggerSystemNotification("Game $status", "${game.title} has been $status instantly.")
            }
        }
    }

    // --- C. DAILY EVENTS ---
    fun addEventPost(title: String, description: String, imageUrl: String, postType: String, gameName: String = "", rewardAmount: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val post = EventPost(
                title = title,
                description = description,
                imageUrl = imageUrl,
                postType = postType,
                gameName = gameName,
                rewardAmount = rewardAmount
            )
            repository.insertEvent(post)
            triggerSystemNotification(
                title = "🔥 Daily Event Feed Update!",
                message = title,
                type = "EVENT"
            )
        }
    }

    fun deleteEvent(eventId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEventById(eventId)
        }
    }

    fun togglePinEvent(eventId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val events = repository.allEvents.firstOrNull() ?: emptyList()
            val event = events.find { it.id == eventId }
            if (event != null) {
                repository.updateEvent(event.copy(isPinned = !event.isPinned))
            }
        }
    }

    // --- D. PAYMENT CENTER ---
    fun updatePaymentMethod(id: String, tag: String, instructions: String, isActive: Boolean, qrImageUrl: String = "", paymentPhotoUrl: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = repository.getPaymentMethodById(id)
            if (existing != null) {
                val updated = existing.copy(
                    tag = tag,
                    instructions = instructions,
                    isActive = isActive,
                    qrImageUrl = if (qrImageUrl.isNotEmpty()) qrImageUrl else existing.qrImageUrl,
                    paymentPhotoUrl = if (paymentPhotoUrl.isNotEmpty()) paymentPhotoUrl else existing.paymentPhotoUrl
                )
                repository.updatePayment(updated)
                triggerSystemNotification("Payment Updated", "$id details modified. Refresh Payments tab to view changes instantly.")
            }
        }
    }

    // --- E. REAL-TIME CHAT & AI CHAT BOT ---
    fun sendMessage(threadId: String, text: String, senderRole: String? = null) {
        if (text.isBlank()) return
        
        viewModelScope.launch(Dispatchers.IO) {
            val sId = _currentUserId.value
            val sName = _currentUser.value?.displayName ?: sId
            val sRole = senderRole ?: _currentUser.value?.role ?: "PLAYER"

            val message = ChatMessage(
                threadId = threadId,
                senderId = sId,
                senderName = sName,
                senderRole = sRole,
                text = text,
                isAiResponse = false
            )
            repository.insertMessage(message)

            // Auto-trigger AI Bot Response if thread is not taken over by admin, and sent by a player
            if (sRole == "PLAYER" && threadId != "global") {
                val isTakenOver = _threadTakeoverStates.value[threadId] ?: false
                
                // If user requests a human / admin explicitly
                val lowercaseText = text.lowercase()
                val needsHuman = lowercaseText.contains("admin") || lowercaseText.contains("human") || 
                                 lowercaseText.contains("support") || lowercaseText.contains("staff") || 
                                 lowercaseText.contains("help") || lowercaseText.contains("William")

                if (needsHuman && !isTakenOver) {
                    // Mark thread takeover state
                    setTakeoverState(threadId, true)
                    insertSystemJoinMessage(threadId, "William Daniel (ADMIN) has received an alert and is joining this chat...")
                }

                if (!isTakenOver && !needsHuman) {
                    // Chatbot response simulation using Gemini API
                    val chatHistory = repository.getChatMessagesByThread(threadId).firstOrNull() ?: emptyList()
                    val botReply = GeminiClient.getAiResponse(text, chatHistory)
                    
                    // Insert AI Response message
                    val aiMessage = ChatMessage(
                        threadId = threadId,
                        senderId = "ai_bot",
                        senderName = "William Daniel AI Bot",
                        senderRole = "AI",
                        text = botReply,
                        isAiResponse = true
                    )
                    repository.insertMessage(aiMessage)
                }
            }
        }
    }

    // Admin replies directly in individual Player chat
    fun sendAdminReply(playerThreadId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            val adminUser = _currentUser.value ?: User("admin", "William Daniel", "ADMIN", "ACTIVE")
            
            // Mark takeover true when admin types
            setTakeoverState(playerThreadId, true)

            val reply = ChatMessage(
                threadId = playerThreadId,
                senderId = adminUser.id,
                senderName = adminUser.displayName,
                senderRole = adminUser.role,
                text = text,
                isAiResponse = false,
                isHumanTakeover = true
            )
            repository.insertMessage(reply)
        }
    }

    // Administrative human takeover
    fun setTakeoverState(threadId: String, isTakenOver: Boolean) {
        val currentMap = _threadTakeoverStates.value.toMutableMap()
        currentMap[threadId] = isTakenOver
        _threadTakeoverStates.value = currentMap
    }

    private suspend fun insertSystemJoinMessage(threadId: String, systemText: String) {
        repository.insertMessage(
            ChatMessage(
                threadId = threadId,
                senderId = "system",
                senderName = "System Alert",
                senderRole = "SUPPORT",
                text = systemText,
                isHumanTakeover = true
            )
        )
    }

    // --- F. PLAYER COMMUNICATION CENTER (BROADCASTS) ---
    fun broadcastMessage(title: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            // Write to Global Chat
            repository.insertMessage(
                ChatMessage(
                    threadId = "global",
                    senderId = "admin",
                    senderName = "William Daniel (BROADCAST)",
                    senderRole = "ADMIN",
                    text = text
                )
            )

            // Trigger notification
            val notification = NotificationItem(
                title = title,
                message = text,
                type = "ANNOUNCEMENT"
            )
            repository.insertNotification(notification)
            _inAppNotification.value = notification
        }
    }

    // Helpers to create simulated system notifications
    private suspend fun triggerSystemNotification(title: String, message: String, type: String = "PUSH") {
        val notification = NotificationItem(
            title = title,
            message = message,
            type = type
        )
        repository.insertNotification(notification)
        _inAppNotification.value = notification
    }

    fun removeNotification(notificationId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNotification(notificationId)
        }
    }

    fun dismissInAppNotification() {
        _inAppNotification.value = null
    }
}
