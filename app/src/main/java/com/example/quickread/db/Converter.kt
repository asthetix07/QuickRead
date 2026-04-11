package com.example.quickread.db

import androidx.room.TypeConverter
import com.example.quickread.models.Source
import com.google.gson.Gson

/**
 * Room type converters for complex types.
 * Gson instance is cached to avoid repeated allocations during DB operations.
 */
class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromSource(source: Source?): String {
        return gson.toJson(source)
    }

    @TypeConverter
    fun toSource(json: String): Source {
        return gson.fromJson(json, Source::class.java)
    }
}
