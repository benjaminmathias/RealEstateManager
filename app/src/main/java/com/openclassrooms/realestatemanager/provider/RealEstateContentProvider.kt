package com.openclassrooms.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.openclassrooms.realestatemanager.data.db.RealEstateDatabase
import dagger.hilt.android.EntryPointAccessors

class RealEstateContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.openclassrooms.realestatemanager.provider"
        const val TABLE_NAME: String = "realEstate"
        val URI_ITEM: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }

    private lateinit var db : RealEstateDatabase

    override fun onCreate(): Boolean {

        context?.applicationContext?.let {
            val entryPoint = ProviderEntryPoint::class.java
            val hiltEntryPoint = EntryPointAccessors.fromApplication(it, entryPoint)
            db = hiltEntryPoint.getDatabase()
        }

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        if (context != null) {
            val cursor = db.realEstateDao().getAllWithCursor()
            cursor.setNotificationUri(context!!.contentResolver, uri)
            return cursor
        }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String {
        return "vnd.android.cursor.realestate/$AUTHORITY.$TABLE_NAME"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri =
        throw UnsupportedOperationException()

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int =
        throw UnsupportedOperationException()

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = throw UnsupportedOperationException()
}