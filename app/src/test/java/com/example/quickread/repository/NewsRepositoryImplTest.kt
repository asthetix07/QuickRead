package com.example.quickread.repository

import com.example.quickread.api.NewsAPI
import com.example.quickread.models.Article
import com.example.quickread.models.NewsResponse
import com.example.quickread.models.Source
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class NewsRepositoryImplTest {

    private lateinit var api: NewsAPI
    private lateinit var repository: NewsRepositoryImpl

    private val sampleArticles = listOf(
        Article(
            author = "Author",
            content = "Content",
            description = "Desc",
            publishedAt = "2026-04-10",
            source = Source("1", "Test Source"),
            title = "Test Title",
            url = "https://test.com",
            urlToImage = "https://test.com/img.jpg"
        )
    )

    @Before
    fun setUp() {
        api = mockk()
        repository = NewsRepositoryImpl(api)
    }

    // ── getTopHeadlines ──────────────────────────────────────────────────

    @Test
    fun `getTopHeadlines delegates to API and returns response`() = runTest {
        val expected = Response.success(
            NewsResponse(articles = sampleArticles, status = "ok", totalResults = 1)
        )
        coEvery { api.getTopHeadlines(any(), any(), any(), any()) } returns expected

        val result = repository.getTopHeadlines("us", 1)

        assertTrue(result.isSuccessful)
        assertEquals(1, result.body()?.articles?.size)
        coVerify(exactly = 1) { api.getTopHeadlines("us", 1, null, any()) }
    }

    @Test
    fun `getTopHeadlines passes category parameter correctly`() = runTest {
        val expected = Response.success(
            NewsResponse(articles = sampleArticles, status = "ok", totalResults = 1)
        )
        coEvery { api.getTopHeadlines(any(), any(), any(), any()) } returns expected

        repository.getTopHeadlines("us", 1, "technology")

        coVerify { api.getTopHeadlines("us", 1, "technology", any()) }
    }

    // ── searchNews ───────────────────────────────────────────────────────

    @Test
    fun `searchNews delegates to API and returns response`() = runTest {
        val expected = Response.success(
            NewsResponse(articles = sampleArticles, status = "ok", totalResults = 1)
        )
        coEvery { api.searchNews(any(), any(), any()) } returns expected

        val result = repository.searchNews("kotlin", 1)

        assertTrue(result.isSuccessful)
        assertEquals(1, result.body()?.articles?.size)
        coVerify(exactly = 1) { api.searchNews("kotlin", 1, any()) }
    }

    @Test
    fun `searchNews propagates API exception`() = runTest {
        coEvery { api.searchNews(any(), any(), any()) } throws RuntimeException("Server error")

        val exception = try {
            repository.searchNews("crash", 1)
            null
        } catch (e: RuntimeException) {
            e
        }

        assertEquals("Server error", exception?.message)
    }
}
