package com.example.quickread.db

import com.example.quickread.models.Source
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class ConvertersTest {

    private lateinit var converters: Converters

    @Before
    fun setUp() {
        converters = Converters()
    }

    @Test
    fun `fromSource serializes Source to JSON string`() {
        val source = Source(id = "bbc", name = "BBC News")
        val json = converters.fromSource(source)

        assertNotNull(json)
        assert(json.contains("bbc"))
        assert(json.contains("BBC News"))
    }

    @Test
    fun `toSource deserializes JSON string to Source`() {
        val json = """{"id":"cnn","name":"CNN"}"""
        val source = converters.toSource(json)

        assertEquals("cnn", source.id)
        assertEquals("CNN", source.name)
    }

    @Test
    fun `roundtrip Source serialization preserves data`() {
        val original = Source(id = "reuters", name = "Reuters")
        val json = converters.fromSource(original)
        val restored = converters.toSource(json)

        assertEquals(original, restored)
    }

    @Test
    fun `fromSource handles null input`() {
        val json = converters.fromSource(null)

        // Gson serializes null as the string "null"
        assertNotNull(json)
    }
}
