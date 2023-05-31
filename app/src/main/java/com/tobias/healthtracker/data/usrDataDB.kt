package com.tobias.healthtracker.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDataDBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /*
    1. Zeile = Name
    2. Zeile = Prio no 1

    3. Zeile = Wasser goal
    4. Zeile = Wasser usr

    5. Zeile = Kalorien goal
    6. Zeile = Kalorien usr

    7. Zeile = Workouts goal
    8. Zeile = Workouts user

    9. Zeile = Tag
     */


    companion object {
        private const val DATABASE_NAME = "user_data.db"
        private const val DATABASE_VERSION = 1

        // Define table and column names
        private const val TABLE_NAME = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DATA = "data"

        private const val TABLE_KALORIEN_NAME = "kalorien_this_day"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_KALORIEN = "kalorien"

        private const val TABLE_WATER_NAME = "water_this_day"
        private const val COLUMN_WATER = "water"

        private const val TABLE_WORKOUT_NAME = "workout_this_month"
        private const val COLUMN_WORKOUT = "workout"
        private const val COLUMN_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_DATA TEXT)"
        db.execSQL(createTableQuery)

        //Kalorien Table
        val createTableKalorienQuery = "CREATE TABLE $TABLE_KALORIEN_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TIME TEXT," +
                "$COLUMN_DATA TEXT," +
                "$COLUMN_KALORIEN TEXT)"
        db.execSQL(createTableKalorienQuery)

        val createTableWaterQuery = "CREATE TABLE $TABLE_WATER_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TIME TEXT," +
                "$COLUMN_DATA TEXT," +
                "$COLUMN_WATER TEXT)"
        db.execSQL(createTableWaterQuery)

        val createTableWorkoutQuery = "CREATE TABLE $TABLE_WORKOUT_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_DATE TEXT)"
        db.execSQL(createTableWorkoutQuery)


        for (i in 1..9) {
            val values = ContentValues().apply {
                putNull(COLUMN_DATA)
            }
            db.insert(TABLE_NAME, null, values)
        }

        /*for (i in 1..8) {
            val values = ContentValues().apply {
                put(COLUMN_DATA, "15")
                put(COLUMN_TIME, "15:30")
                put(COLUMN_KALORIEN, "1500")

            }
            db.insert(TABLE_KALORIEN_NAME, null, values)
        }*/
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }


    fun insertData(data: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATA, data)
        }
        val insertedId = db.insert(TABLE_NAME, null, values)
        db.close()
        return insertedId
    }

    fun insertFoodData(name: String, time: String, kalorien: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATA, name)
            put(COLUMN_TIME, time)
            put(COLUMN_KALORIEN, kalorien)
        }
        val insertedId = db.insert(TABLE_KALORIEN_NAME, null, values)
        db.close()
        return insertedId
    }


    @SuppressLint("Range")
    fun getAllData(): List<String> {
        val AllData = mutableListOf<String>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
            val data = cursor.getString(cursor.getColumnIndex(COLUMN_DATA))
        }
        cursor.close()
        db.close()
        return AllData
    }

    @SuppressLint("Range")
    fun getAllFoodData(): List<UserKalorienEaten> {
        val allData = mutableListOf<UserKalorienEaten>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_KALORIEN_NAME"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val data = cursor.getString(cursor.getColumnIndex(COLUMN_DATA)) as String
            val time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME)) as String
            val kalorien = cursor.getDouble(cursor.getColumnIndex(COLUMN_KALORIEN))
            val userKalorienEaten = UserKalorienEaten(data, time, kalorien)
            allData.add(userKalorienEaten)
        }
        cursor.close()
        db.close()
        return allData
    }

    fun insertWaterData(name: String, time: String, liters: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATA, name)
            put(COLUMN_TIME, time)
            put(COLUMN_WATER, liters)
        }
        val insertedId = db.insert(TABLE_WATER_NAME, null, values)
        db.close()
        return insertedId
    }

    @SuppressLint("Range")
    fun getAllWaterData(): List<UserWaterDrunken> {
        val allData = mutableListOf<UserWaterDrunken>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_WATER_NAME"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val data = cursor.getString(cursor.getColumnIndex(COLUMN_DATA)) as String
            val time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME)) as String
            val water = cursor.getDouble(cursor.getColumnIndex(COLUMN_WATER))
            val userWaterDrunken = UserWaterDrunken(data, time, water)
            allData.add(userWaterDrunken)
        }
        cursor.close()
        db.close()
        return allData
    }


    fun insertWorkoutData(time: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATE, time)
        }
        val insertedId = db.insert(TABLE_WORKOUT_NAME, null, values)
        db.close()
        return insertedId
    }


    @SuppressLint("Range")
    fun getAllWorkoutData(): List<UserWorkouts> {
        val allData = mutableListOf<UserWorkouts>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_WORKOUT_NAME"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID)) as Int
            val time = cursor.getString(cursor.getColumnIndex(COLUMN_DATE)) as String
            val userWorkouts = UserWorkouts(id,time)
            allData.add(userWorkouts)
        }
        cursor.close()
        db.close()
        return allData
    }


    @SuppressLint("Range")
    fun getDataById(userId: Int): String? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $userId"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            //val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
            val data = cursor.getString(cursor.getColumnIndex(COLUMN_DATA))

            cursor.close()
            db.close()
            return data
        }
        cursor.close()
        db.close()
        return null
    }


    fun updateData(id: Int, data: String): Int {
        val db = writableDatabase

        // Check if the row exists in the table
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $id"
        val cursor = db.rawQuery(query, null)

        if (cursor.count == 0) {
            // Row doesn't exist, so create it with the default value of 0
            val values = ContentValues().apply {
                put(COLUMN_ID, id)
                put(COLUMN_DATA, "0")
                // Add more column-value pairs as needed
            }
            db.insert(TABLE_NAME, null, values)
        }

        cursor.close()

        // Update the data of the row
        val values = ContentValues().apply {
            put(COLUMN_DATA, data)
            // Add more column-value pairs as needed
        }
        val updatedRows = db.update(
            TABLE_NAME,
            values,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
        db.close()
        return updatedRows
    }


    fun deleteData(userId: Long): Int {
        val db = writableDatabase
        val deletedRows = db.delete(
            TABLE_NAME,
            "$COLUMN_ID = ?",
            arrayOf(userId.toString())
        )
        db.close()
        return deletedRows
    }

    fun clearTable(tableName: String) {
        val db = writableDatabase
        db.execSQL("DELETE FROM $tableName")
        db.close()
    }

    fun debugTable() {
        val db = writableDatabase
        for (i in 1..8) {
            val values = ContentValues().apply {
                put(COLUMN_DATA, "15")
                put(COLUMN_TIME, "15:30")
                put(COLUMN_KALORIEN, "1500")
                // Add more column-value pairs as needed
            }
            db.insert(TABLE_KALORIEN_NAME, null, values)
        }
    }
}