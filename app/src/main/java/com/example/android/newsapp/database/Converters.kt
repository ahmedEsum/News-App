package com.example.android.newsapp.database

import androidx.room.TypeConverter
import com.example.android.newsapp.pojo.Source

class Converters  {
    @TypeConverter
    fun fromSource (source: Source):String{
        return source.name
    }

    @TypeConverter
    fun toSource (name :String) :Source{
        return Source(name,name)
    }
}