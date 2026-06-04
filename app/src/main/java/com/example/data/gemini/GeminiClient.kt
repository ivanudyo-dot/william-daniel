package com.example.data.gemini

import android.util.Log
import com.example.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// --- Simple Moshi-compatible local REST types ---
data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

data class GenerateContentResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: Content?
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun getAiResponse(userMessage: String, threadHistory: List<com.example.data.model.ChatMessage>): String {
        // Fallback key injection from BuildConfig which resolves from Secrets/env
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isEmpty() || apiKey.contains("MY_GEMINI_API_KEY")) {
            Log.w(TAG, "Gemini API Key is missing or default. Using local agent responder.")
            return getLocalFallbackResponse(userMessage)
        }

        // Construct conversational turns
        val contents = mutableListOf<Content>()
        
        // Add up to 6 turns of history to fit context window optimally
        threadHistory.takeLast(6).forEach { msg ->
            val roleName = if (msg.senderRole == "PLAYER") "user" else "model"
            contents.add(
                Content(parts = listOf(Part(text = msg.text)))
            )
        }
        
        // Always add current message
        contents.add(Content(parts = listOf(Part(text = userMessage))))

        val systemInstruction = Content(
            parts = listOf(
                Part(
                    text = "You are an instant 24/7 AI Chatbot receptionist for 'William Daniel Gaming' online sweeps club. " +
                           "Keep responses short (1-3 sentences maximum). Be extremely polite and energetic. " +
                           "William Daniel Gaming supports loading and cashout for top systems: Fire Kirin, Orion Stars, Game Vault, Juwa, and Milky Way Sweeps. " +
                           "Deposits are handled securely via Cash App (\$WilliamGaming77), Chime (williamgaming_chime), Venmo (@WilliamGamingClub), and PayPal (williamgaming_paypal@gmail.com). " +
                           "If a player asks to Cash Out or Deposit, explain that they should make the transfer using their preferred app (found in the 'Payments' tab) or type 'Admin' to wait for human takeover. " +
                           "If they ask for accounts, offer to create one immediately. " +
                           "If they request human help, reply: 'I am alerting William/Support right now. Please hold, we will connect you shortly!' and note that human takeover is active."
                )
            )
        )

        val request = GenerateContentRequest(
            contents = contents,
            systemInstruction = systemInstruction
        )

        return try {
            val response = service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Thanks for messaging William Daniel Gaming! An Administrator or Support staff has been notified and will join this chat thread shortly to help you. How else can I assist in the meantime?"
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API Call failed, fallback active", e)
            getLocalFallbackResponse(userMessage)
        }
    }

    private fun getLocalFallbackResponse(message: String): String {
        val lower = message.lowercase()
        return when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") -> {
                "Hello! Welcome to William Daniel Gaming Support. 🎰 I'm your AI Assistant. How can we load your accounts or help you redeem prizes today?"
            }
            lower.contains("deposit") || lower.contains("load") || lower.contains("pay") || lower.contains("cashapp") || lower.contains("send") -> {
                "To load credits instantly, tap the 'Payments' tab to see our direct Cash App, Chime, Venmo, or PayPal details. Send payment with your username in the memo, and post a screenshot here. This alert has notified William!"
            }
            lower.contains("cashout") || lower.contains("redeem") || lower.contains("withdraw") || lower.contains("cash out") -> {
                "We payout instantly 24/7 via Cash App, Chime, Venmo, and PayPal! To redeem winnings, please message William or Support here with your game username, portal name (e.g. Fire Kirin) and cashout amount. A human is joining!"
            }
            lower.contains("game") || lower.contains("play") || lower.contains("link") || lower.contains("portal") -> {
                "We support Fire Kirin, Orion Stars, Game Vault, Juwa, and Milky Way sweeps! Tap the 'Games' tab to view our 20+ active hot systems and launch them instantly."
            }
            lower.contains("admin") || lower.contains("human") || lower.contains("support") || lower.contains("person") || lower.contains("help") -> {
                "🔔 Human Takeover Requested! William and support staff have been notified and are joining your private chat thread now. Please type your query and hold on!"
            }
            else -> {
                "Thanks for gaming with William Daniel Gaming! 🃏 Your message has been pinned. William or support will respond instantly. Type 'admin' if you need an urgent human rep."
            }
        }
    }
}
