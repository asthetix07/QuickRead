package com.example.quickread.ui.state

import com.example.quickread.models.Article
import com.example.quickread.models.Source
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NewsUiStateTest {

    @Test
    fun `default state has no articles, no loading, no error`() {
        val state = NewsUiState()

        assertFalse(state.isLoading)
        assertTrue(state.articles.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `loading state is represented correctly`() {
        val state = NewsUiState(isLoading = true)

        assertTrue(state.isLoading)
        assertTrue(state.articles.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `error state preserves error message`() {
        val state = NewsUiState(error = "Network error")

        assertFalse(state.isLoading)
        assertEquals("Network error", state.error)
        assertTrue(state.articles.isEmpty())
    }

    @Test
    fun `state with articles has correct count`() {
        val articles = listOf(
            Article(
                author = "A", content = "C", description = "D",
                publishedAt = "2026-01-01", source = Source("1", "S"),
                title = "T", url = "https://x.com", urlToImage = null
            )
        )
        val state = NewsUiState(articles = articles)

        assertEquals(1, state.articles.size)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `copy preserves unmodified fields`() {
        val state = NewsUiState(isLoading = true)
        val updated = state.copy(isLoading = false, error = "Fail")

        assertFalse(updated.isLoading)
        assertEquals("Fail", updated.error)
        assertTrue(updated.articles.isEmpty())
    }
}
