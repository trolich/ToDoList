package com.example.todolist

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object ToDoContract
{
    object ToDoEntry : BaseColumns{
        const val TABLE_NAME = "ToDos"
        const val COLUMN_NAME_NAME = "Name"
        const val COLUMN_NAME_PRIORITY = "Priority"
        const val COLUMN_NAME_TYPE = "Type"
    }

    object MeetingEntry
    {
        const val TABLE_NAME = "MeetingDetails"
        const val COLUMN_NAME_ID = "Id" //Foreign key of ToDos table
        const val COLUMN_NAME_NAME = "WhoToMeet"
        const val COLUMN_NAME_ADDRESS = "Address"
        const val COLUMN_NAME_TIME = "Time"
        const val COLUMN_NAME_DATE = "Date"
        const val COLUMN_NAME_PHONE = "PhoneNumber"
        const val COLUMN_NAME_EMAIL = "EmailAddress"
        const val COLUMN_NAME_NOTES = "Notes"
    }

    private const val  SQL_CREATE_MAIN = "CREATE TABLE IF NOT EXISTS ${ToDoEntry.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, ${ToDoEntry.COLUMN_NAME_NAME} TEXT," +
            "${ToDoEntry.COLUMN_NAME_PRIORITY} INTEGER, ${ToDoEntry.COLUMN_NAME_TYPE} INTEGER)"

    private const val  SQL_CREATE_MEETING = "CREATE TABLE IF NOT EXISTS ${MeetingEntry.TABLE_NAME} (" +
            "${MeetingEntry.COLUMN_NAME_ID} INTEGER PRIMARY KEY REFERENCES ${ToDoEntry.TABLE_NAME}(${BaseColumns._ID}) ON DELETE CASCADE" +
            ", ${MeetingEntry.COLUMN_NAME_NAME} TEXT, " +
            "${MeetingEntry.COLUMN_NAME_ADDRESS} TEXT, ${MeetingEntry.COLUMN_NAME_TIME} TEXT," +
            "${MeetingEntry.COLUMN_NAME_DATE} TEXT, ${MeetingEntry.COLUMN_NAME_PHONE} TEXT," +
            "${MeetingEntry.COLUMN_NAME_EMAIL} TEXT, ${MeetingEntry.COLUMN_NAME_NOTES} TEXT)"

    private const val SQL_DROP_MAIN = "DROP TABLE IF EXISTS ${ToDoEntry.TABLE_NAME}"
    private const val SQL_DROP_MEETING = "DROP TABLE IF EXISTS ${MeetingEntry.TABLE_NAME}"

    class ToDoDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
    {
        override fun onCreate(db: SQLiteDatabase?) {

            db!!.execSQL(SQL_CREATE_MAIN)
            db.execSQL(SQL_CREATE_MEETING)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            // TODO find something to do with this
            db?.execSQL(SQL_DROP_MEETING)
            db?.execSQL(SQL_DROP_MAIN)

            db!!.execSQL(SQL_CREATE_MAIN)
            db.execSQL(SQL_CREATE_MEETING)
        }

        companion object
        {
            val DATABASE_NAME = "ToDoMain.db"
            val DATABASE_VERSION = 4
        }
    }
}