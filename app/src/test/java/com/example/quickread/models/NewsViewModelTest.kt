package com.example.quickread.models

import app.cash.turbine.test
import com.example.quickread.db.ArticleDao
import com.example.quickread.repository.NewsRepository
import com.example.quickread.ui.state.NewsUiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: NewsRepository
    private lateinit var dao: ArticleDao
    private lateinit var viewModel: NewsViewModel

    private val sampleArticles = listOf(
        Article(
            author = "Author A",
            content = "Content A",
            description = "Desc A",
            publishedAt = "2026-04-10",
            source = Source("1", "Source A"),
            title = "Title A",
            url = "https://a.com",
            urlToImage = "https://a.com/img.jpg"
        ),
        Article(
            author = "Author B",
            content = "Content B",
            description = "Desc B",
            publishedAt = "2026-04-11",
            source = Source("2", "Source B"),
            title = "Title B",
            url = "https://b.com",
            urlToImage = null
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        dao = mockk(relaxed = true)
        viewModel = NewsViewModel(repository, dao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── fetchTopHeadlines ────────────────────────────────────────────────

    @Test
    fun `fetchTopHeadlines emits articles on successful response`() = runTest {
        val response = Response.success(
            NewsResponse(articles = sampleArticles, status = "ok", totalResults = 2)
        )
        coEvery { repository.getTopHeadlines(any(), any(), any()) } returns response

        viewModel.topNews.test {
            assertEquals(NewsUiState(), awaitItem()) // initial state

            viewModel.fetchTopHeadlines()
            advanceUntilIdle()

            val loaded = awaitItem() // loading = true
            val result = awaitItem() // articles populated
            assertEquals(2, result.articles.size)
            assertFalse(result.isLoading)
            assertNull(result.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchTopHeadlines sets error on failed response`() = runTest {
        val errorBody = "{}".toResponseBody(null)
        val response = Response.error<NewsResponse>(500, errorBody)
        coEvery { repository.getTopHeadlines(any(), any(), any()) } returns response

        viewModel.topNews.test {
            awaitItem() // initial

            viewModel.fetchTopHeadlines()
            advanceUntilIdle()

            awaitItem() // loading = true
            val result = awaitItem()
            assertEquals("Failed to load news.", result.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchTopHeadlines sets error on exception`() = runTest {
        coEvery { repository.getTopHeadlines(any(), any(), any()) } throws RuntimeException("Network down")

        viewModel.topNews.test {
            awaitItem() // initial

            viewModel.fetchTopHeadlines()
            advanceUntilIdle()

            awaitItem() // loading = true
            val result = awaitItem()
            assertEquals("Network down", result.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchTopHeadlines does not fetch when already loading`() = runTest {
        val response = Response.success(
            NewsResponse(articles = sampleArticles, status = "ok", totalResults = 2)
        )
        coEvery { repository.getTopHeadlines(any(), any(), any()) } returns response

        viewModel.fetchTopHeadlines()
        // Calling again immediately should be no-op (isLoading guard)
        viewModel.fetchTopHeadlines()
        advanceUntilIdle()

        val state = viewModel.topNews.value
        assertEquals(2, state.articles.size)
    }

    // ── selectCategory ───────────────────────────────────────────────────

    @Test
    fun `selectCategory resets state and triggers new fetch`() = runTest {
        val response = Response.success(
            NewsResponse(articles = sampleArticles, status = "ok", totalResults = 2)
        )
        coEvery { repository.getTopHeadlines(any(), any(), any()) } returns response

        viewModel.selectCategory("technology")
        advanceUntilIdle()

        assertEquals("technology", viewModel.selectedCategory.value)
        val state = viewModel.topNews.value
        assertEquals(2, state.articles.size)
    }

    @Test
    fun `selectCategory with same value is a no-op`() = runTest {
        viewModel.selectCategory("health")
        val response = Response.success(
            NewsResponse(articles = sampleArticles, status = "ok", totalResults = 2)
        )
        coEvery { repository.getTopHeadlines(any(), any(), any()) } returns response
        advanceUntilIdle()

        // Selecting same category again should not reset
        val stateBefore = viewModel.topNews.value
        viewModel.selectCategory("health")
        advanceUntilIdle()
        val stateAfter = viewModel.topNews.value

        assertEquals(stateBefore, stateAfter)
    }

    // ── searchNews ───────────────────────────────────────────────────────

    @Test
    fun `searchNews emits articles on success`() = runTest {
        val response = Response.success(
            NewsResponse(articles = sampleArticles, status = "ok", totalResults = 2)
        )
        coEvery { repository.searchNews(any(), any()) } returns response

        viewModel.searchNewsState.test {
            awaitItem() // initial

            viewModel.searchNews("bitcoin")
            advanceUntilIdle()

            val loading = awaitItem()
            assertTrue(loading.isLoading)

            val result = awaitItem()
            assertEquals(2, result.articles.size)
            assertNull(result.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchNews sets error on failure`() = runTest {
        val errorBody = "{}".toResponseBody(null)
        val response = Response.error<NewsResponse>(500, errorBody)
        coEvery { repository.searchNews(any(), any()) } returns response

        viewModel.searchNewsState.test {
            awaitItem() // initial

            viewModel.searchNews("bitcoin")
            advanceUntilIdle()

            awaitItem() // loading
            val result = awaitItem()
            assertEquals("Failed to fetch search results.", result.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchNews sets error on exception`() = runTest {
        coEvery { repository.searchNews(any(), any()) } throws RuntimeException("Timeout")

        viewModel.searchNewsState.test {
            awaitItem()

            viewModel.searchNews("kotlin")
            advanceUntilIdle()

            awaitItem() // loading
            val result = awaitItem()
            assertEquals("Timeout", result.error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
