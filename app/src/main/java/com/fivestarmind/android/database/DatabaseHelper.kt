package com.fivestarmind.android.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.fivestarmind.android.mvp.model.response.VideoDurationModel

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        /**
         * Database information
         */
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "WinningWaysVideos"

        /**
         * Table information.
         */
        private const val TABLE_VIDEOS = "table_videos"

        /**
         * Keys name used in tables.
         */
        private const val KEY_USER_ID = "userId"
        private const val KEY_VIDEO_ID = "videoId"
        private const val KEY_VIDEO_DURATION = "videoDuration"
        private const val KEY_VIDEO_SYNC_STATUS = "videoSyncStatus"

        /**
         * Create table queries.
         */
        private const val CREATE_TABLE_VIDEOS =
            "CREATE TABLE $TABLE_VIDEOS ( $KEY_USER_ID INTEGER, $KEY_VIDEO_ID INTEGER PRIMARY KEY, $KEY_VIDEO_DURATION TEXT, $KEY_VIDEO_SYNC_STATUS TEXT)"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("DatabaseHelper", "onCreate - Table created successfully")
        db!!.execSQL(CREATE_TABLE_VIDEOS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_VIDEOS")
        onCreate(db)
    }

    /**
     * This method will accept videos duration model and save into the database.
     */
    fun addVideos(model: VideoDurationModel) {
        val db: SQLiteDatabase = this.writableDatabase

        val values = ContentValues()
        values.put(KEY_USER_ID, model.userId)
        values.put(KEY_VIDEO_ID, model.videoId)
        values.put(KEY_VIDEO_DURATION, model.videoDuration)
        values.put(KEY_VIDEO_SYNC_STATUS, model.videoSyncStatus)

        db.insert(TABLE_VIDEOS, null, values)
    }

    /**
     * This method will provide videos
     */
    fun getVideos(): ArrayList<VideoDurationModel> {
        val arrayList: ArrayList<VideoDurationModel> = arrayListOf()
        val selectQuery = "SELECT * FROM $TABLE_VIDEOS"
        val db = this.writableDatabase

        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val videoDurationModel = VideoDurationModel(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3)
                )
                arrayList.add(videoDurationModel)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return arrayList
    }

    /**
     * This method will remove all data from videos table.
     */
    fun removeAllData() {
        val dbase = this.writableDatabase
        dbase.execSQL("delete from $TABLE_VIDEOS")
    }

    /**
     * This will remove particular item from database matching its key.
     */
    fun removeParticularItemFromDatabase(id: Int) {
        val dbase = this.writableDatabase

        val deleteQuery1 = ("DELETE FROM " + TABLE_VIDEOS
                + " WHERE " + KEY_VIDEO_ID + " = '" + id + "'")

        dbase.execSQL(deleteQuery1)
    }
}