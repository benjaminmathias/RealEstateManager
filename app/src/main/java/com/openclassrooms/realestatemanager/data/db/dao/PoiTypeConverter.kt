package com.openclassrooms.realestatemanager.data.db.dao

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.openclassrooms.realestatemanager.data.db.QueryCollection

class PoiTypeConverter {

    // This class is used to convert our ArrayList of String containing nearbyPOI
    private inline fun <reified T> Gson.fromJson(json: String) =
        fromJson<T>(json, object : TypeToken<T>() {}.type)!!

    @TypeConverter
    fun fromStringArrayList(value: ArrayList<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringArrayList(value: String): ArrayList<String> {
        return try {
            Gson().fromJson<ArrayList<String>>(value)
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    @TypeConverter
    fun fromCollection(collection: QueryCollection<String>): String {
        return fromStringArrayList(collection.arrayList)
    }
}
