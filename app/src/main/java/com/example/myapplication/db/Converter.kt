package com.example.myapplication.db

import androidx.room.TypeConverter
import com.example.myapplication.models.Source
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun fromSource(source: Source?): String {
        return Gson().toJson(source)
    }

    @TypeConverter
    fun toSource(json: String): Source {
        return Gson().fromJson(json, Source::class.java)
    }
}
