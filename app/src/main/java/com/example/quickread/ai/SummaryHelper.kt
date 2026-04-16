package com.example.quickread.ai

import com.example.quickread.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton helper for AI-powered news summarization using Gemini.
 *
 * The [GenerativeModel] is created once and reused across all summary
 * requests, avoiding repeated initialization overhead.
 */
@Singleton
class SummaryHelper @Inject constructor() {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-3-flash-preview",
        apiKey = BuildConfig.GEMINI_API_KEY,
        systemInstruction = content {
            text("You are a news summarizer. Summarize the given news title into a short summary of maximum 25-30 words.")
        }
    )

    /**
     * Generates a concise summary (25–30 words) for the given news [title].
     *
     * @param title The article headline to summarize.
     * @return The AI-generated summary text, or a fallback message.
     */
    suspend fun generateSummary(title: String): String =
        withContext(Dispatchers.IO) {
            val response = generativeModel.generateContent("Title: $title")
            response.text ?: "No summary generated."
        }
}
