package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.viewmodel.GamingViewModel
import kotlinx.coroutines.launch

// Color tokens for Premium Cyber Dark thematic vibe (styled after Immersive UI theme)
val DeepDarkBlack = Color(0xFF0A0A0C)
val CharcoalGray = Color(0xFF121217)
val SurfaceCardBg = Color(0xFF16161B)
val CyberGold = Color(0xFFF59E0B)
val CyberCyan = Color(0xFF06B6D4)
val NeonRed = Color(0xFFEF4444)
val EmeraldGreen = Color(0xFF10B981)
val DarkBorderColor = Color(0xFF202026)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: GamingViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val currentUserId by viewModel.currentUserId.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val inAppNotification by viewModel.inAppNotification.collectAsStateWithLifecycle()
    
    val playerActiveTab by viewModel.playerActiveTab.collectAsStateWithLifecycle()
    val adminActiveTab by viewModel.adminActiveTab.collectAsStateWithLifecycle()
    
    // View roles switcher visibility (for extreme trial/demo accessibility!)
    var showRoleSwitcher by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_root_surface"),
        color = DeepDarkBlack
    ) {
        if (currentUser == null) {
            LoginScreen(viewModel = viewModel)
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
            
            // In-App Real-time Announcement / PUSH Alert banner representation
            AnimatedVisibility(
                visible = inAppNotification != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                inAppNotification?.let { notif ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .border(1.dp, CyberGold.copy(0.7f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCardBg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = "Notification",
                                tint = CyberGold,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = notif.title,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = notif.message,
                                    color = Color.LightGray,
                                    fontSize = 13.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = { viewModel.clearInAppNotification() }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Top Header Dashboard & Interactive Switcher (Lets users see both admin tabs & player tabs instantly!)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalGray),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DarkBorderColor)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "EXCLUSIVE MEMBER",
                                color = CyberGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "WILLIAM DANIEL ",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-0.5).sp
                                )
                                Text(
                                    text = "G",
                                    color = CyberGold,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-0.5).sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Active: ${currentUser?.displayName} (${currentUser?.role})",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (showRoleSwitcher) "Hide Switch" else "Quick Role Switch",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                modifier = Modifier
                                    .clickable { showRoleSwitcher = !showRoleSwitcher }
                                    .border(1.dp, CyberCyan.copy(0.4f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Expandable interactive controller to switch roles on-the-fly dynamically
                    AnimatedVisibility(visible = showRoleSwitcher) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = DarkBorderColor, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "TEST CONSOLE (Change role to explore features instantly):",
                                color = Color.Gray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.handleUserLogin("player") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentUserId == "player") CyberGold else SurfaceCardBg
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        "Player Role", 
                                        color = if (currentUserId == "player") DeepDarkBlack else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Button(
                                    onClick = { viewModel.handleUserLogin("admin") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentUserId == "admin") CyberGold else SurfaceCardBg
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        "Admin Role", 
                                        color = if (currentUserId == "admin") DeepDarkBlack else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Button(
                                    onClick = { viewModel.handleUserLogin("support") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentUserId == "support") CyberGold else SurfaceCardBg
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        "Support Role", 
                                        color = if (currentUserId == "support") DeepDarkBlack else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Main Body Content
            Box(modifier = Modifier.weight(1f)) {
                if (currentUser?.role == "ADMIN" || currentUser?.role == "SUPPORT") {
                    AdminDashboardContent(
                        viewModel = viewModel,
                        activeTab = adminActiveTab
                    )
                } else {
                    PlayerPanelContent(
                        viewModel = viewModel,
                        activeTab = playerActiveTab
                    )
                }
            }

            // Bottom Navigation Drawer (Dynamic depending on permissions)
            if (currentUser?.role == "ADMIN" || currentUser?.role == "SUPPORT") {
                AdminBottomNavigation(
                    activeTab = adminActiveTab,
                    onTabSelected = { viewModel.setAdminActiveTab(it) }
                )
            } else {
                PlayerBottomNavigation(
                    activeTab = playerActiveTab,
                    onTabSelected = { viewModel.setPlayerActiveTab(it) }
                )
            }
          }
        }
    }
}

// ------------------------------------------------------------------------
//                          PLAYER SIDE SCREENS
// ------------------------------------------------------------------------

@Composable
fun PlayerPanelContent(viewModel: GamingViewModel, activeTab: String) {
    when (activeTab) {
        "Home" -> PlayerHomeScreen(viewModel)
        "Games" -> PlayerGamesScreen(viewModel)
        "Events" -> PlayerEventsScreen(viewModel)
        "Payments" -> PlayerPaymentsScreen(viewModel)
        "Chat" -> PlayerSupportChatScreen(viewModel)
        "Profile" -> PlayerProfileScreen(viewModel)
    }
}

@Composable
fun PlayerHomeScreen(viewModel: GamingViewModel) {
    val activeGames by viewModel.activeGames.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .testTag("player_home_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Promotional Carousel representation
        item {
            PromoHeroBanner()
        }

        // Live stats scrolling widget simulation
        item {
            LiveClubMetricsWidget()
        }

        // Section Title: Game Center
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔥 HOT SYSTEMS (${activeGames.size})",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Tap Games to search",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }

        // 20+ Game Cards Grid
        // To build a scrollable layout with nested grid, we list the items in standard chunks
        val chunkedGames = activeGames.chunked(2)
        items(chunkedGames) { gameRow ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                gameRow.forEach { game ->
                    GamePlayCard(
                        game = game,
                        onPlayClick = {
                            uriHandler.openUri(game.gameUrl)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill space if the row has only 1 game
                if (gameRow.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PlayerGamesScreen(viewModel: GamingViewModel) {
    val activeGames by viewModel.activeGames.collectAsStateWithLifecycle()
    val searchQuery by viewModel.gameSearchQuery.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.gameSearchQuery.value = it },
            placeholder = { Text("Search 20+ sweeping platforms...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, "Search", tint = CyberGold) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.gameSearchQuery.value = "" }) {
                        Icon(Icons.Default.Clear, "Clear", tint = Color.Gray)
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CharcoalGray,
                unfocusedContainerColor = CharcoalGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedIndicatorColor = CyberGold,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(activeGames) { game ->
                GamePlayCard(
                    game = game,
                    onPlayClick = {
                        uriHandler.openUri(game.gameUrl)
                    }
                )
            }
        }
    }
}

@Composable
fun PlayerEventsScreen(viewModel: GamingViewModel) {
    val events by viewModel.allEvents.collectAsStateWithLifecycle()
    val filterType by viewModel.eventCategoryFilter.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        
        // Horizontal filters
        val filters = listOf("ALL", "PROMO", "WINNING", "CASHOUT")
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { f ->
                val selected = filterType == f
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) CyberGold else CharcoalGray)
                        .border(1.dp, if (selected) CyberGold else DarkBorderColor, RoundedCornerShape(20.dp))
                        .clickable { viewModel.eventCategoryFilter.value = f }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = f,
                        color = if (selected) DeepDarkBlack else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Feed list
        if (events.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No matching events. Stay tuned!", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(events) { post ->
                    EventFeedCard(post)
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun PlayerPaymentsScreen(viewModel: GamingViewModel) {
    val context = LocalContext.current
    val payments by viewModel.allPayments.collectAsStateWithLifecycle()
    var selectedMethodId by remember { mutableStateOf("cashapp") }
    
    val currentMethod = payments.find { it.id == selectedMethodId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        item {
            Text(
                text = "💳 PAYMENT SECURITY CENTER",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        // Methods switcher pills
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                payments.forEach { method ->
                    val selected = method.id == selectedMethodId
                    val activeColor = if (method.isActive) CyberGold else Color.Gray.copy(0.4f)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) activeColor else CharcoalGray)
                            .border(1.dp, if (selected) activeColor else DarkBorderColor, RoundedCornerShape(12.dp))
                            .clickable { selectedMethodId = method.id }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = method.displayName,
                            color = if (selected) DeepDarkBlack else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        currentMethod?.let { method ->
            if (!method.isActive) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCardBg.copy(0.3f)),
                        border = BorderStroke(1.dp, NeonRed.copy(0.3f))
                    ) {
                        Box(modifier = Modifier.padding(16.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("⚠️ This payment channel is currently undergoing automated maintenance. Please select another channel.", color = NeonRed, fontSize = 13.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            } else {
                item {
                    // QR Animation & ID plate card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCardBg),
                        border = BorderStroke(1.dp, CyberGold.copy(0.3f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Fancy Cyber QR code simulator
                            Card(
                                modifier = Modifier
                                    .size(160.dp)
                                    .border(2.dp, CyberGold, RoundedCornerShape(12.dp))
                                    .padding(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        // Draw dynamic cyber QR grid simulation lines
                                        val rows = 8
                                        val cols = 8
                                        val cellW = size.width / cols
                                        val cellH = size.height / rows
                                        for (r in 0 until rows) {
                                            for (c in 0 until cols) {
                                                if ((r + c) % 2 == 0 || (r == 0 && c == 0) || (r >= rows - 2 && c <= 1)) {
                                                    drawRect(
                                                        color = Color.Black,
                                                        topLeft = Offset(c * cellW, r * cellH),
                                                        size = androidx.compose.ui.geometry.Size(cellW, cellH)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Official Group Account Name:",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CharcoalGray)
                                    .border(1.dp, DarkBorderColor, RoundedCornerShape(8.dp))
                                    .clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Payment Tag", method.tag)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Username Copied: ${method.tag}", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = method.tag,
                                    color = CyberGold,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "🚀 ACTION INSTRUCTIONS",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CharcoalGray),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DarkBorderColor)
                    ) {
                        Text(
                            text = method.instructions,
                            color = Color.LightGray,
                            lineHeight = 18.sp,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Post Load button direct link simulation
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.setPlayerActiveTab("Chat")
                            viewModel.sendMessage("player", "I just sent a deposit load request via ${method.displayName}. Account username tag: ${method.tag}. Here is my confirmations.")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.Filled.CheckCircle, "Paid", tint = DeepDarkBlack)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Copied! Launch Support Chat to load now",
                            color = DeepDarkBlack,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PlayerSupportChatScreen(viewModel: GamingViewModel) {
    val messages by viewModel.currentChatMessages.collectAsStateWithLifecycle()
    val isAdminOnline by viewModel.isAdminOnline.collectAsStateWithLifecycle()
    val takeoverStates by viewModel.threadTakeoverStates.collectAsStateWithLifecycle()
    var inputMessage by remember { mutableStateOf("") }
    
    val contextUserId = "player"
    val isChatTakenOver = takeoverStates[contextUserId] ?: false

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Chat Header Status
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CharcoalGray),
            border = BorderStroke(1.dp, DarkBorderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isAdminOnline) EmeraldGreen else Color.DarkGray)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Admin Support Operator",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isAdminOnline) "Online & Instant Responses" else "Administrators are currently away",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                }

                // Human takeover status indicator
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isChatTakenOver) CyberGold.copy(0.15f) else CyberCyan.copy(0.15f)
                    ),
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = if (isChatTakenOver) CyberGold else CyberCyan,
                        shape = RoundedCornerShape(8.dp)
                    )
                ) {
                    Text(
                        text = if (isChatTakenOver) "👤 ADMIN ACTIVE" else "🤖 AI CHAT ACTIVE",
                        color = if (isChatTakenOver) CyberGold else CyberCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Messages List View
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val fromSelf = msg.senderId == "player"
                val alignment = if (fromSelf) Alignment.End else Alignment.Start
                val bubbleColor = if (fromSelf) CyberGold else SurfaceCardBg
                val textColor = if (fromSelf) DeepDarkBlack else Color.White
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = alignment
                ) {
                    Text(
                        text = "${msg.senderName} (${msg.senderRole})",
                        color = Color.Gray,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp, 
                            topEnd = 16.dp, 
                            bottomStart = if (fromSelf) 16.dp else 2.dp, 
                            bottomEnd = if (fromSelf) 2.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(containerColor = bubbleColor),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.text,
                            color = textColor,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        // Message input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                placeholder = { Text("Type query here... E.g. 'load Cash App' or 'Admin'", color = Color.Gray, fontSize = 13.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CharcoalGray,
                    unfocusedContainerColor = CharcoalGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = CyberGold,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (inputMessage.isNotBlank()) {
                        viewModel.sendMessage("player", inputMessage, "PLAYER")
                        inputMessage = ""
                    }
                }),
                modifier = Modifier.weight(1f)
            )

            FloatingActionButton(
                onClick = {
                    if (inputMessage.isNotBlank()) {
                        viewModel.sendMessage("player", inputMessage, "PLAYER")
                        inputMessage = ""
                    }
                },
                containerColor = CyberGold,
                contentColor = DeepDarkBlack,
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, "Send", modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun PlayerProfileScreen(viewModel: GamingViewModel) {
    val notifications by viewModel.allNotifications.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CharcoalGray),
                border = BorderStroke(1.dp, DeepDarkBlack)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(CyberGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser?.displayName?.take(2)?.uppercase() ?: "GP"),
                            color = DeepDarkBlack,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(currentUser?.displayName ?: "GamerPro 777", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(currentUser?.email ?: "player@gmail.com", color = Color.Gray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Membership Tier: VIP Level 3", color = CyberCyan, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.userSignOut() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                        modifier = Modifier.fillMaxWidth(0.8f).height(38.dp).testTag("profile_logout_btn"),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("SIGN OUT / LOGOUT", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Text("🔕 RECENT NOTIFICATIONS FEED", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (notifications.isEmpty()) {
            item {
                Text("No recent alerts in your feed.", color = Color.Gray, fontSize = 12.sp)
            }
        } else {
            items(notifications) { notif ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCardBg),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when(notif.type) {
                                "EVENT" -> Icons.Default.Event
                                "PROMOTION" -> Icons.Default.LocalOffer
                                else -> Icons.Default.Campaign
                            },
                            contentDescription = "Alert",
                            tint = CyberGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(notif.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(notif.message, color = Color.LightGray, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------------
//                          ADMIN SIDE SCREENS & DASHBOARDS
// ------------------------------------------------------------------------

@Composable
fun AdminDashboardContent(viewModel: GamingViewModel, activeTab: String) {
    when (activeTab) {
        "Dashboard" -> AdminMainSummaryScreen(viewModel)
        "Users" -> AdminPlayerManagementScreen(viewModel)
        "Games" -> AdminGameManagementScreen(viewModel)
        "Events" -> AdminEventPostingScreen(viewModel)
        "Payments" -> AdminPaymentControlScreen(viewModel)
        "Chat Center" -> AdminChatCenterScreen(viewModel)
        "Notifications" -> AdminBroadcastNotificationHub(viewModel)
        "Settings" -> AdminSettingsScreen(viewModel)
    }
}

@Composable
fun AdminMainSummaryScreen(viewModel: GamingViewModel) {
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val games by viewModel.allGames.collectAsStateWithLifecycle()
    val events by viewModel.allEvents.collectAsStateWithLifecycle()
    val messages by viewModel.allMessagesList.collectAsStateWithLifecycle()
    val isAdminOnline by viewModel.isAdminOnline.collectAsStateWithLifecycle()

    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        item {
            Text(
                "SYSTEM MASTER DASHBOARD",
                color = CyberGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        // Live Administrative online status toggle
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalGray),
                border = BorderStroke(1.dp, CyberGold.copy(0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Administrative Support Level", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (isAdminOnline) "ONLINE: AI Bot delegates active human requests" else "OFFLINE: AI Chat handles backup",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = isAdminOnline,
                        onCheckedChange = { viewModel.toggleAdminOnlineStatus() },
                        colors = SwitchDefaults.colors(checkedThumbColor = EmeraldGreen)
                    )
                }
            }
        }

        // High Density Admin Stats Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(title = "Registered Users", value = "${users.size}", icon = Icons.Default.People, color = CyberCyan, modifier = Modifier.weight(1f))
                StatCard(title = "Active Games", value = "${games.filter { it.isEnabled }.size}", icon = Icons.Default.VideogameAsset, color = CyberGold, modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(title = "Daily Events Live", value = "${events.size}", icon = Icons.Default.EventAvailable, color = NeonRed, modifier = Modifier.weight(1f))
                StatCard(title = "Chats Exchanged", value = "${messages.size}", icon = Icons.Default.Forum, color = EmeraldGreen, modifier = Modifier.weight(1f))
            }
        }

        // Fast broadcast notification manager
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardBg),
                border = BorderStroke(1.dp, DarkBorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "⚡ RAPID ANNOUNCEMENT BROADCAST",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = broadcastTitle,
                        onValueChange = { broadcastTitle = it },
                        label = { Text("Broadcast Header / Title") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberGold,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = CyberGold,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = broadcastText,
                        onValueChange = { broadcastText = it },
                        label = { Text("Message Body Text") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberGold,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = CyberGold,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (broadcastTitle.isNotBlank() && broadcastText.isNotBlank()) {
                                viewModel.broadcastMessage(broadcastTitle, broadcastText)
                                broadcastTitle = ""
                                broadcastText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Broadcast Live Announcement to Users", color = DeepDarkBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AdminPlayerManagementScreen(viewModel: GamingViewModel) {
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val searchQuery by viewModel.userSearchQuery.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text("PLAYER BASE CONTROL CENTER", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.userSearchQuery.value = it },
            placeholder = { Text("Search users by display name or ID...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, "Search", tint = CyberCyan) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CharcoalGray,
                unfocusedContainerColor = CharcoalGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedIndicatorColor = CyberCyan,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCardBg),
                    border = BorderStroke(1.dp, if (user.status == "SUSPENDED") NeonRed.copy(0.4f) else DarkBorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(user.displayName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (user.status == "ACTIVE") EmeraldGreen.copy(0.15f) else NeonRed.copy(0.15f)
                                    )
                                ) {
                                    Text(
                                        user.status, 
                                        color = if (user.status == "ACTIVE") EmeraldGreen else NeonRed, 
                                        fontSize = 9.sp, 
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text("ID: ${user.id}  •  Role: ${user.role}", color = Color.Gray, fontSize = 11.sp)
                            Text("Last Pulse: ${System.currentTimeMillis() - user.lastActive} ms ago", color = Color.Gray, fontSize = 9.sp)
                        }

                        // Suspended administrative controls
                        if (user.id != "admin") {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (user.status == "ACTIVE") {
                                    Button(
                                        onClick = { viewModel.suspendUser(user.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonRed.copy(0.8f)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Suspend", fontSize = 11.sp, color = Color.White)
                                    }
                                } else {
                                    Button(
                                        onClick = { viewModel.activateUser(user.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen.copy(0.8f)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Restore", fontSize = 11.sp, color = DeepDarkBlack)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminGameManagementScreen(viewModel: GamingViewModel) {
    val games by viewModel.allGames.collectAsStateWithLifecycle()
    
    var showAddForm by remember { mutableStateOf(false) }
    var gameTitle by remember { mutableStateOf("") }
    var gameUrl by remember { mutableStateOf("") }
    var gameCategory by remember { mutableStateOf("Slots") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SYSTEM GAME INVENTORY", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = { showAddForm = !showAddForm },
                colors = ButtonDefaults.buttonColors(containerColor = if (showAddForm) NeonRed else CyberGold)
            ) {
                Text(if (showAddForm) "Close" else "Add Game", color = DeepDarkBlack, fontWeight = FontWeight.Bold)
            }
        }

        AnimatedVisibility(visible = showAddForm) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardBg),
                border = BorderStroke(1.dp, CyberGold.copy(0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("ADD BRAND NEW GAMING PORTAL", color = CyberGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = gameTitle,
                        onValueChange = { gameTitle = it },
                        label = { Text("Game Display Title") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGold, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = gameUrl,
                        onValueChange = { gameUrl = it },
                        label = { Text("Game Destination URL Link") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGold, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Category Selection Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val categories = listOf("Slots", "Fish", "Cards")
                        categories.forEach { cat ->
                            val selected = gameCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) CyberGold else CharcoalGray)
                                    .border(1.dp, if (selected) CyberGold else DarkBorderColor, RoundedCornerShape(8.dp))
                                    .clickable { gameCategory = cat }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(cat, color = if (selected) DeepDarkBlack else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            if (gameTitle.isNotBlank() && gameUrl.isNotBlank()) {
                                viewModel.addGame(
                                    title = gameTitle,
                                    imageUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=400",
                                    gameUrl = gameUrl,
                                    category = gameCategory
                                )
                                gameTitle = ""
                                gameUrl = ""
                                showAddForm = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Verify & Save Portal to App Screen", color = DeepDarkBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(games) { game ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCardBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(game.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(game.gameUrl, color = CyberCyan, fontSize = 11.sp)
                            Text("Category: ${game.category}", color = Color.Gray, fontSize = 11.sp)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(if (game.isEnabled) "ACTIVE" else "DISABLED", color = if (game.isEnabled) EmeraldGreen else Color.Gray, fontSize = 10.sp)
                            Switch(
                                checked = game.isEnabled,
                                onCheckedChange = { viewModel.toggleGameActive(game.id, it) }
                            )
                            IconButton(onClick = { viewModel.deleteGame(game) }) {
                                Icon(Icons.Default.Delete, "Delete", tint = NeonRed)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminEventPostingScreen(viewModel: GamingViewModel) {
    val events by viewModel.allEvents.collectAsStateWithLifecycle()
    
    var postTitle by remember { mutableStateOf("") }
    var postDesc by remember { mutableStateOf("") }
    var posterType by remember { mutableStateOf("PROMO") }
    var gameReference by remember { mutableStateOf("") }
    var rewardsText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("DAILY CLUB EVENTS FEED CREATOR", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardBg),
                border = BorderStroke(1.dp, DarkBorderColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("WRITE NEW DAILY EVENT OR BIG JACKPOT PIN", color = CyberGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = postTitle,
                        onValueChange = { postTitle = it },
                        label = { Text("Event Header / Subject Name") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGold, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = postDesc,
                        onValueChange = { postDesc = it },
                        label = { Text("Details & Description Instructions") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGold, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val types = listOf("PROMO", "WINNING", "CASHOUT", "ANNOUNCEMENT")
                        types.forEach { type ->
                            val selected = posterType == type
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) CyberGold else CharcoalGray)
                                    .clickable { posterType = type }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(type, color = if (selected) DeepDarkBlack else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = gameReference,
                        onValueChange = { gameReference = it },
                        label = { Text("Associated Game Platform (Optional)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGold, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = rewardsText,
                        onValueChange = { rewardsText = it },
                        label = { Text("Winning / Cashout Label (Optional, eg. Won $500)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGold, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (postTitle.isNotBlank() && postDesc.isNotBlank()) {
                                viewModel.addEventPost(
                                    title = postTitle,
                                    description = postDesc,
                                    imageUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=600",
                                    postType = posterType,
                                    gameName = gameReference,
                                    rewardAmount = rewardsText
                                )
                                postTitle = ""
                                postDesc = ""
                                gameReference = ""
                                rewardsText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Post Live to Player Feed", color = DeepDarkBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("PUBLISHED ARCHIVE FEED MANAGEMENT", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        items(events) { post ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(post.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Badge: ${post.postType}  •  Pinned: ${post.isPinned}", color = Color.Gray, fontSize = 11.sp)
                    }
                    Row {
                        IconButton(onClick = { viewModel.togglePinEvent(post.id) }) {
                            Icon(Icons.Default.PushPin, "Pin", tint = if (post.isPinned) CyberGold else Color.Gray)
                        }
                        IconButton(onClick = { viewModel.deleteEvent(post.id) }) {
                            Icon(Icons.Default.Delete, "Delete", tint = NeonRed)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPaymentControlScreen(viewModel: GamingViewModel) {
    val payments by viewModel.allPayments.collectAsStateWithLifecycle()
    var selectedId by remember { mutableStateOf("cashapp") }
    
    val selectedMethod = payments.find { it.id == selectedId }
    var textTag by remember { mutableStateOf("") }
    var textInst by remember { mutableStateOf("") }
    var activeState by remember { mutableStateOf(true) }

    // Synchronize form settings when channel selection changes
    LaunchedEffect(selectedMethod) {
        selectedMethod?.let {
            textTag = it.tag
            textInst = it.instructions
            activeState = it.isActive
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("PAYMENT GATEWAY MANAGER", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        // Horizontal picker selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                payments.forEach { method ->
                    val selected = method.id == selectedId
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) CyberGold else CharcoalGray)
                            .clickable { selectedId = method.id }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(method.displayName, color = if (selected) DeepDarkBlack else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        selectedMethod?.let { method ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCardBg),
                    border = BorderStroke(1.dp, DarkBorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("CONFIGURE: ${method.displayName.uppercase()}", color = CyberGold, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = textTag,
                            onValueChange = { textTag = it },
                            label = { Text("Group Account tag/username") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGold, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = textInst,
                            onValueChange = { textInst = it },
                            label = { Text("Transfer instructions display") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGold, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Channel Status (Active/Locked)", color = Color.White, fontSize = 12.sp)
                            Switch(
                                checked = activeState,
                                onCheckedChange = { activeState = it }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.updatePaymentMethod(
                                    id = method.id,
                                    tag = textTag,
                                    instructions = textInst,
                                    isActive = activeState
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Propagate Changes Instantly", color = DeepDarkBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminChatCenterScreen(viewModel: GamingViewModel) {
    val selectedThreadId by viewModel.selectedChatThreadId.collectAsStateWithLifecycle()
    val messages by viewModel.adminSelectedChatMessages.collectAsStateWithLifecycle()
    val takeoverStates by viewModel.threadTakeoverStates.collectAsStateWithLifecycle()
    
    val threadList = listOf("player", "clover", "highroller") // Pre-populated active player threads for easy admin testing
    var adminReplyText by remember { mutableStateOf("") }
    val threadTakeoverActive = takeoverStates[selectedThreadId] ?: false

    Row(modifier = Modifier.fillMaxSize()) {
        // Master Thread List (Left)
        Column(
            modifier = Modifier
                .width(130.dp)
                .fillMaxHeight()
                .background(CharcoalGray)
                .border(2.dp, DarkBorderColor)
                .padding(4.dp)
        ) {
            Text("Active Chats", color = CyberGold, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(8.dp))
            Divider(color = DarkBorderColor)
            Spacer(modifier = Modifier.height(6.dp))
            threadList.forEach { id ->
                val selected = selectedThreadId == id
                val takingOver = takeoverStates[id] ?: false
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.setSelectedChatThread(id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) SurfaceCardBg else Color.Transparent
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(id.replaceFirstChar { it.uppercase() }, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (takingOver) CyberGold.copy(0.12f) else CyberCyan.copy(0.12f)
                            )
                        ) {
                            Text(
                                text = if (takingOver) "HUMAN" else "AI BOT",
                                color = if (takingOver) CyberGold else CyberCyan,
                                fontSize = 8.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Detail Message History and direct response engine (Right)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            // Takeover dynamic control banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardBg)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Thread Takeover", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(if (threadTakeoverActive) "Human Active" else "AI Bot Active", color = Color.Gray, fontSize = 9.sp)
                    }
                    Switch(
                        checked = threadTakeoverActive,
                        onCheckedChange = { viewModel.setTakeoverState(selectedThreadId, it) }
                    )
                }
            }

            // Message scrolling view
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(messages) { msg ->
                    val fromSelf = msg.senderId == "admin" || msg.senderId == "support"
                    val isSystem = msg.senderId == "system"
                    
                    val alignment = if (fromSelf) Alignment.End else Alignment.Start
                    val bubbleColor = if (isSystem) CharcoalGray else if (fromSelf) CyberGold else SurfaceCardBg
                    val textCol = if (isSystem) CyberCyan else if (fromSelf) DeepDarkBlack else Color.White

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = alignment
                    ) {
                        Text("${msg.senderName} (${msg.senderRole})", color = Color.Gray, fontSize = 8.sp)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = bubbleColor),
                            modifier = Modifier.widthIn(max = 200.dp)
                        ) {
                            Text(msg.text, color = textCol, fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }

            // Direct keyboard reply input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TextField(
                    value = adminReplyText,
                    onValueChange = { adminReplyText = it },
                    placeholder = { Text("Reply as William...", color = Color.Gray, fontSize = 11.sp) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CharcoalGray,
                        unfocusedContainerColor = CharcoalGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = CyberGold,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        if (adminReplyText.isNotBlank()) {
                            viewModel.sendAdminReply(selectedThreadId, adminReplyText)
                            adminReplyText = ""
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(CyberGold)
                        .size(36.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = DeepDarkBlack, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun AdminBroadcastNotificationHub(viewModel: GamingViewModel) {
    val notifications by viewModel.allNotifications.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text("AUDIT NOTIFICATION ARCHIVE", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        
        if (notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No broadcasts logged.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(notifications) { notif ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCardBg)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(notif.title, color = CyberGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(notif.message, color = Color.White, fontSize = 12.sp)
                                Text("Channel Logged: ${notif.type}", color = Color.Gray, fontSize = 10.sp)
                            }
                            IconButton(onClick = { viewModel.removeNotification(notif.id) }) {
                                Icon(Icons.Default.Delete, "Delete", tint = NeonRed)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSettingsScreen(viewModel: GamingViewModel) {
    var adminNotes by remember { mutableStateOf("Keep high hitting events pinned during weekends! Max loads are limited to $5,000 via Cash App per transaction.") }
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CharcoalGray),
                border = BorderStroke(1.dp, CyberGold.copy(0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(currentUser?.displayName ?: "Administrator", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(currentUser?.email ?: "admin@williamdaniel.com", color = Color.Gray, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { viewModel.userSignOut() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp).testTag("admin_logout_btn"),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Text("SIGN OUT", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("SYSTEM SECURITY SETTINGS", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardBg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Administrative Scratch Notes", color = CyberGold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = adminNotes,
                        onValueChange = { adminNotes = it },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberGold, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardBg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Database Integrity Logs", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Schema: Version 1 (Seeded Room DB)\nLocal Synchronization Status: Live\nAPI Client Status: Connected with automatic fallbacks", color = Color.Gray, fontSize = 11.sp)
                }
            }
        }
    }
}

// ------------------------------------------------------------------------
//                       SHARED REUSABLE COMPOSABLES
// ------------------------------------------------------------------------

@Composable
fun PromoHeroBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0x19FFFFFF))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    // Linear gradient from amber-600 (0xFFD97706) to orange-800 (0xFF9A3412)
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFD97706), Color(0xFF9A3412)),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, size.height)
                        )
                    )
                }
                .padding(16.dp)
        ) {
            // Absolute top-right corner element: "0.25 BTC"
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x66000000))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "0.25 BTC",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberGold
                )
            }

            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x33FFFFFF))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "LIVE NOW", 
                            fontSize = 8.sp, 
                            fontWeight = FontWeight.Black, 
                            color = Color.White
                        )
                    }
                    Text(
                        text = "Ends in 04:22:15",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "DAILY JACKPOT",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "$25,000.00",
                    color = CyberGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
fun LiveClubMetricsWidget() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CharcoalGray),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Club Status: ACTIVE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Text("⚡ Jackpot Redemptions: $14,930 Today", color = CyberGold, fontSize = 10.sp, fontWeight = FontWeight.Black)
            Text("Loads: 100% OK", color = CyberCyan, fontSize = 10.sp)
        }
    }
}

@Composable
fun GamePlayCard(
    game: Game,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DarkBorderColor, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardBg)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .drawBehind {
                        // Custom premium look gradient for games representation
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(SurfaceCardBg, CharcoalGray.copy(0.7f))
                            )
                        )
                        // Decorative circuit line graphic
                        drawLine(
                            color = CyberGold.copy(0.3f),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, size.height),
                            strokeWidth = 2f
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // Online images fallback representation with clean center icon
                AsyncImage(
                    model = game.imageUrl,
                    contentDescription = game.title,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Small thematic platform category tag overlay
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepDarkBlack.copy(0.8f))
                ) {
                    Text(
                        text = game.category,
                        color = CyberGold,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = game.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onPlayClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = DeepDarkBlack,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PLAY NOW", color = DeepDarkBlack, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun EventFeedCard(post: EventPost) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (post.isPinned) 1.5.dp else 1.dp,
                color = if (post.isPinned) CyberGold else DarkBorderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardBg)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when(post.postType) {
                            "WINNING" -> Icons.Default.EmojiEvents
                            "CASHOUT" -> Icons.Default.AttachMoney
                            else -> Icons.Default.Campaign
                        },
                        contentDescription = null,
                        tint = CyberGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = post.postType,
                        color = CyberGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                if (post.isPinned) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PushPin, "Pinned", tint = CyberCyan, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PINNED", color = CyberCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Body content area
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                Text(
                    text = post.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = post.description,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            // Bottom associated tag information
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (post.gameName.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CharcoalGray)
                    ) {
                        Text(
                            text = "🎮 ${post.gameName}",
                            color = Color.White,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (post.rewardAmount.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EmeraldGreen.copy(0.12f))
                    ) {
                        Text(
                            text = post.rewardAmount,
                            color = EmeraldGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceCardBg),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = Color.Gray, fontSize = 11.sp)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
        }
    }
}

// Bottom tab navigators

@Composable
fun PlayerBottomNavigation(
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = CharcoalGray,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets.navigationBars,
        modifier = Modifier
            .height(72.dp)
            .drawBehind {
                // border top
                drawLine(
                    color = Color(0x19FFFFFF),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) {
        val tabs = listOf("Home", "Games", "Events", "Payments", "Chat", "Profile")
        tabs.forEach { tab ->
            val selected = activeTab == tab
            val icon = when (tab) {
                "Home" -> Icons.Default.Home
                "Games" -> Icons.Default.Gamepad
                "Events" -> Icons.Default.EventNote
                "Payments" -> Icons.Default.Wallet
                "Chat" -> Icons.AutoMirrored.Filled.Chat
                else -> Icons.Default.Person
            }
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = { 
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selected) CyberGold.copy(0.15f) else Color.Transparent)
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = tab, modifier = Modifier.size(22.dp))
                    }
                },
                label = { Text(tab, fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CyberGold,
                    unselectedIconColor = Color.Gray.copy(0.7f),
                    selectedTextColor = CyberGold,
                    unselectedTextColor = Color.Gray.copy(0.7f),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun AdminBottomNavigation(
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = CharcoalGray,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets.navigationBars,
        modifier = Modifier
            .height(72.dp)
            .drawBehind {
                // border top
                drawLine(
                    color = Color(0x19FFFFFF),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) {
        val tabs = listOf("Dashboard", "Users", "Games", "Events", "Payments", "Chat Center")
        tabs.forEach { tab ->
            val selected = activeTab == tab
            val icon = when (tab) {
                "Dashboard" -> Icons.Default.Dashboard
                "Users" -> Icons.Default.Group
                "Games" -> Icons.Default.Dns
                "Events" -> Icons.Default.EditCalendar
                "Payments" -> Icons.Default.Payments
                else -> Icons.Default.MarkUnreadChatAlt
            }
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = { 
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selected) CyberGold.copy(0.15f) else Color.Transparent)
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = tab, modifier = Modifier.size(20.dp))
                    }
                },
                label = { Text(tab, fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CyberGold,
                    unselectedIconColor = Color.Gray.copy(0.7f),
                    selectedTextColor = CyberGold,
                    unselectedTextColor = Color.Gray.copy(0.7f),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: GamingViewModel) {
    val context = LocalContext.current
    var isRegisterMode by remember { mutableStateOf(false) }
    var isAdminMode by remember { mutableStateOf(false) }

    // Form states
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var displayNameInput by remember { mutableStateOf("") }
    var registerEmailInput by remember { mutableStateOf("") }
    var registerPasswordInput by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepDarkBlack)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Gaming Logo Icon
            Icon(
                imageVector = Icons.Default.Casino,
                contentDescription = "Casino Icon Logo",
                tint = CyberGold,
                modifier = Modifier
                    .size(72.dp)
                    .border(2.dp, CyberGold.copy(0.4f), RoundedCornerShape(20.dp))
                    .padding(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "WILLIAM DANIEL ",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = "G",
                    color = CyberGold,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )
            }
            Text(
                text = "EXCLUSIVE GAMING CLUB",
                color = CyberGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Action Feedback Banners
            errorMessage?.let { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = NeonRed.copy(0.12f)),
                    border = BorderStroke(1.dp, NeonRed.copy(0.35f))
                ) {
                    Text(
                        text = msg,
                        color = NeonRed,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            successMessage?.let { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldGreen.copy(0.12f)),
                    border = BorderStroke(1.dp, EmeraldGreen.copy(0.35f))
                ) {
                    Text(
                        text = msg,
                        color = EmeraldGreen,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Mode Selector Tab Rows (Only show if not in registration screen)
            if (!isRegisterMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CharcoalGray)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = { 
                            isAdminMode = false 
                            errorMessage = null
                            successMessage = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isAdminMode) CyberGold else Color.Transparent
                        ),
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "PLAYER PORTAL",
                            color = if (!isAdminMode) DeepDarkBlack else Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = { 
                            isAdminMode = true 
                            errorMessage = null
                            successMessage = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAdminMode) CyberGold else Color.Transparent
                        ),
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "ADMIN PORTAL",
                            color = if (isAdminMode) DeepDarkBlack else Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Input Form Switcher
            if (isRegisterMode) {
                // REGISTER NEW USER
                Text(
                    text = "CREATE NEW PLAYER ACCOUNT",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = displayNameInput,
                    onValueChange = { displayNameInput = it },
                    label = { Text("Display / Player Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberGold,
                        unfocusedBorderColor = DarkBorderColor,
                        focusedLabelColor = CyberGold,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CharcoalGray,
                        unfocusedContainerColor = CharcoalGray
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("reg_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = registerEmailInput,
                    onValueChange = { registerEmailInput = it },
                    label = { Text("Gmail Address") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberGold,
                        unfocusedBorderColor = DarkBorderColor,
                        focusedLabelColor = CyberGold,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CharcoalGray,
                        unfocusedContainerColor = CharcoalGray
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("reg_email_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = registerPasswordInput,
                    onValueChange = { registerPasswordInput = it },
                    label = { Text("Create Platform Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberGold,
                        unfocusedBorderColor = DarkBorderColor,
                        focusedLabelColor = CyberGold,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CharcoalGray,
                        unfocusedContainerColor = CharcoalGray
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("reg_pass_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        errorMessage = null
                        successMessage = null
                        viewModel.signUpUser(
                            email = registerEmailInput,
                            name = displayNameInput,
                            passwordText = registerPasswordInput
                        ) { success, msg ->
                            if (success) {
                                successMessage = msg
                                isRegisterMode = false
                            } else {
                                errorMessage = msg
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("sign_up_submit_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("REGISTER NOW", color = DeepDarkBlack, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Already have an account? Sign In",
                    color = CyberCyan,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clickable { isRegisterMode = false }
                        .padding(8.dp)
                )

            } else {
                // SIGN IN PORTAL
                Text(
                    text = if (isAdminMode) "ADMINISTRATIVE LOGIN" else "PLAYER LOGIN",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text(if (isAdminMode) "Admin Username" else "Gmail Address or Username") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberGold,
                        unfocusedBorderColor = DarkBorderColor,
                        focusedLabelColor = CyberGold,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CharcoalGray,
                        unfocusedContainerColor = CharcoalGray
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("login_email_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Application Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberGold,
                        unfocusedBorderColor = DarkBorderColor,
                        focusedLabelColor = CyberGold,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CharcoalGray,
                        unfocusedContainerColor = CharcoalGray
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("login_pass_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        errorMessage = null
                        successMessage = null
                        viewModel.loginWithCredentials(
                            emailOrUserId = emailInput,
                            passwordText = passwordInput,
                            asAdmin = isAdminMode
                        ) { success, msg ->
                            if (success) {
                                successMessage = msg
                            } else {
                                errorMessage = msg
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("sign_in_submit_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("SECURE SIGN IN", color = DeepDarkBlack, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }

                if (!isAdminMode) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Google sign-in simulation button
                    Button(
                        onClick = {
                            errorMessage = null
                            viewModel.loginWithGoogle { success, msg ->
                                if (success) {
                                    successMessage = msg
                                } else {
                                    errorMessage = msg
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CharcoalGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .border(1.dp, Color.Gray.copy(0.3f), RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("SIGN IN WITH GOOGLE GMAIL", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Don't have an account? Sign Up Now",
                        color = CyberCyan,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .clickable { isRegisterMode = true }
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = DarkBorderColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Demo Accounts Help Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalGray.copy(0.5f)),
                border = BorderStroke(1.dp, CyberGold.copy(0.15f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Demo / Tester Accounts (Instant Clicks):",
                        color = CyberGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                emailInput = "luckygamer@gmail.com"
                                passwordInput = "player123"
                                isAdminMode = false
                                errorMessage = null
                                successMessage = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardBg),
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Fast Player", color = Color.White, fontSize = 10.sp)
                        }
                        Button(
                            onClick = {
                                emailInput = "admin@williamdaniel.com"
                                passwordInput = "admin123"
                                isAdminMode = true
                                errorMessage = null
                                successMessage = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardBg),
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Fast Admin", color = Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
