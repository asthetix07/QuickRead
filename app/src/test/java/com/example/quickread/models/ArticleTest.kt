package com.example.quickread.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ArticleTest {

    @Test
    fun `article default values are applied correctly`() {
        val article = Article(
            author = "Author",
            content = "Content",
            description = "Description",
            publishedAt = "2026-04-11",
            source = Source("1", "Source"),
            title = "Title",
            url = "https://example.com",
            urlToImage = "https://example.com/img.jpg"
        )

        assertNull(article.id)
        assertEquals("Author", article.author)
        assertEquals("Title", article.title)
        assertEquals("https://example.com", article.url)
    }

    @Test
    fun `article handles null fields gracefully`() {
        val article = Article(
            author = null,
            content = null,
            description = null,
            publishedAt = null,
            source = null,
            title = null,
            url = null,
            urlToImage = null
        )

        assertNull(article.author)
        assertNull(article.title)
        assertNull(article.url)
        assertNull(article.urlToImage)
        assertNull(article.source)
    }

    @Test
    fun `article equality is based on all fields`() {
        val a = Article(
            author = "A", content = "C", description = "D",
            publishedAt = "2026-01-01", source = Source("1", "S"),
            title = "T", url = "https://x.com", urlToImage = null
        )
        val b = a.copy()

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `savedAt timestamp is auto-populated`() {
        val before = System.currentTimeMillis()
        val article = Article(
            author = null, content = null, description = null,
            publishedAt = null, source = null, title = null,
            url = null, urlToImage = null
        )
        val after = System.currentTimeMillis()

        assert(article.savedAt in before..after)
    }
}

class SourceTest {

    @Test
    fun `source data class holds id and name`() {
        val source = Source(id = "abc", name = "ABC News")

        assertEquals("abc", source.id)
        assertEquals("ABC News", source.name)
    }

    @Test
    fun `source equality works correctly`() {
        val s1 = Source("1", "CNN")
        val s2 = Source("1", "CNN")

        assertEquals(s1, s2)
        assertEquals(s1.hashCode(), s2.hashCode())
    }
}

class NewsResponseTest {

    @Test
    fun `newsResponse holds articles and metadata`() {
        val articles = listOf(
            Article(
                author = "A", content = "C", description = "D",
                publishedAt = "2026-01-01", source = Source("1", "S"),
                title = "T", url = "https://x.com", urlToImage = null
            )
        )
        val response = NewsResponse(articles = articles, status = "ok", totalResults = 1)

        assertEquals("ok", response.status)
        assertEquals(1, response.totalResults)
        assertEquals(1, response.articles.size)
        assertEquals("T", response.articles[0].title)
    }

    @Test
    fun `newsResponse with empty articles`() {
        val response = NewsResponse(articles = emptyList(), status = "ok", totalResults = 0)

        assertEquals(0, response.articles.size)
        assertEquals(0, response.totalResults)
    }
}
