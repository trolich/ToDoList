package com.example.todolist

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.BaseColumns
import android.support.constraint.ConstraintLayout
import android.support.v4.content.res.ResourcesCompat.getColor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class ToDoAdapter(private val dataSet : Array<ToDoItem>, context: Context) : RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>()
{
    val con = context
    class ToDoViewHolder(var item : View) : RecyclerView.ViewHolder(item)
    {
        public val title = item.findViewById<TextView>(R.id.toDoName)
        private val type = item.findViewById<TextView>(R.id.toDoType)
        public val finishButton : Button = item.findViewById(R.id.finishToDoButton)
        val detailsButton : Button = item.findViewById(R.id.details_button)

        public fun setTitle(newTitle : String)
        {
            title.text = newTitle
        }

        public fun setType(newType : String)
        {
            type.text = newType
        }

        public fun setColor(prior : Int)
        {
            when(prior)
            {
                3 -> when(type.text) {
                    "Homework" -> item.setBackgroundColor(item.context.resources.getColor(R.color.homeworkHigh))
                    "Meeting" -> item.setBackgroundColor(item.context.resources.getColor(R.color.meetingHigh))
                    "Shopping" -> item.setBackgroundColor(item.context.resources.getColor(R.color.shoppingHigh))
                    "Workout" -> item.setBackgroundColor(item.context.resources.getColor(R.color.workoutHigh))
                    "Chores" -> item.setBackgroundColor(item.context.resources.getColor(R.color.choresHigh))
                    else -> item.setBackgroundColor(Color.WHITE)
                }
                2 -> when(type.text)
                {
                    "Homework" -> item.setBackgroundColor(item.context.resources.getColor(R.color.homeworkMed))
                    "Meeting" -> item.setBackgroundColor(item.context.resources.getColor(R.color.meetingMed))
                    "Shopping" -> item.setBackgroundColor(item.context.resources.getColor(R.color.shoppingMed))
                    "Workout" -> item.setBackgroundColor(item.context.resources.getColor(R.color.workoutMed))
                    "Chores" -> item.setBackgroundColor(item.context.resources.getColor(R.color.choresMed))
                    else -> item.setBackgroundColor(Color.WHITE)
                }
                1 -> when(type.text)
                {
                    "Homework" -> item.setBackgroundColor(item.context.resources.getColor(R.color.homeworkLow))
                    "Meeting" -> item.setBackgroundColor(item.context.resources.getColor(R.color.meetingLow))
                    "Shopping" -> item.setBackgroundColor(item.context.resources.getColor(R.color.shoppingLow))
                    "Workout" -> item.setBackgroundColor(item.context.resources.getColor(R.color.workoutLow))
                    "Chores" -> item.setBackgroundColor(item.context.resources.getColor(R.color.choresLow))
                    else -> item.setBackgroundColor(Color.WHITE)
                }
                else -> item.setBackgroundColor(Color.WHITE)
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ToDoViewHolder {
        val textView = LayoutInflater.from(p0.context).inflate(R.layout.list_item, p0, false)
        return ToDoViewHolder(textView)
    }

    override fun onBindViewHolder(p0: ToDoViewHolder, p1: Int) {
        p0.setTitle(dataSet[p1].name)
        p0.setType(dataSet[p1].type)
        p0.setColor(dataSet[p1].priority)

        p0.title.setOnClickListener{

        }
        p0.finishButton.setOnClickListener {
            val dbHelper = ToDoContract.ToDoDBHelper(con)
            val db = dbHelper.writableDatabase

            val sql1 = "DELETE FROM ${ToDoContract.MeetingEntry.TABLE_NAME} WHERE ${ToDoContract.MeetingEntry.COLUMN_NAME_ID} = ?"
            val selectionArgs1 = arrayOf(dataSet[p1].ID.toString())

            val cursor1 = db.rawQuery(sql1,selectionArgs1)
            while(cursor1.moveToNext())
            {}
            cursor1.close()

            val sql2 = "DELETE FROM ${ToDoContract.ToDoEntry.TABLE_NAME} WHERE ${BaseColumns._ID} = ?"
            val selectionArgs2 = arrayOf(dataSet[p1].ID.toString())

            val cursor2 = db.rawQuery(sql2,selectionArgs2)
            while(cursor2.moveToNext())
            {}
            cursor2.close()

            val intent = Intent(con, ScrollingActivity::class.java )
            con.startActivity(intent)
        }

        p0.detailsButton.setOnClickListener {
            when(dataSet[p1].type) {
                "Meeting" -> {
                    val bundle = Bundle()
                    bundle.putLong("id", dataSet[p1].ID)
                    bundle.putString("name", dataSet[p1].name)
                    val intent = Intent(con, MeetingDetailsActivity::class.java)
                    intent.putExtras(bundle)
                    con.startActivity(intent)
                }
                else -> Toast.makeText(con, "Not a meeting", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = dataSet.size
}