package com.example.todolist

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.content.Intent
import android.provider.BaseColumns
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_scrolling.*

class ScrollingActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        toolbar.title = "To Do's"

        val plusButton : FloatingActionButton = findViewById(R.id.fab)
        plusButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) :Unit {
                val intent = Intent(this@ScrollingActivity, NewToDoActivity::class.java )
                startActivity(intent)
            }
        })

        viewAdapter = ToDoAdapter(loadData(), this)
        //viewAdapter = ToDoAdapter(emptyArray(), this)
        viewManager = LinearLayoutManager(this)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply{
            setHasFixedSize(true)
            layoutManager = this@ScrollingActivity.viewManager
            adapter = this@ScrollingActivity.viewAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        when (item.itemId) {
            R.id.action_settings -> {}
            R.id.action_new_to_do -> {
                val intent = Intent(this@ScrollingActivity, NewToDoActivity::class.java )
                startActivity(intent)
            }
            R.id.action_clear_all -> {
                /*
                val dbHelper = ToDoContract.ToDoDBHelper(this@ScrollingActivity)
                val db = dbHelper.writableDatabase
                var cursor = db.rawQuery("DROP TABLE IF EXISTS ${ToDoContract.ToDoEntry.TABLE_NAME}", null)
                while(cursor.moveToNext()) {}
                cursor.close()
                cursor = db.rawQuery( "DROP TABLE IF EXISTS ${ToDoContract.MeetingEntry.TABLE_NAME}", null)
                while(cursor.moveToNext()){}
                cursor.close()
                Toast.makeText(this, "Dropping All Tables", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@ScrollingActivity, ScrollingActivity::class.java)
                startActivity(intent)
                */
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun loadData() : Array<ToDoItem>
    {
        val dbHelper = ToDoContract.ToDoDBHelper(this@ScrollingActivity)
        val db = dbHelper.readableDatabase

        val countCursor = db.rawQuery("SELECT ${BaseColumns._ID} FROM ${ToDoContract.ToDoEntry.TABLE_NAME}", null)
        val toDoItems: Array<ToDoItem>
        if(countCursor.count > 0) {
            toDoItems = Array<ToDoItem>(countCursor.count) { i -> ToDoItem(0, "", "", 0) }
            countCursor.close()

            val projection = arrayOf(BaseColumns._ID, ToDoContract.ToDoEntry.COLUMN_NAME_NAME,
                ToDoContract.ToDoEntry.COLUMN_NAME_PRIORITY, ToDoContract.ToDoEntry.COLUMN_NAME_TYPE)

            val selection = "*"
            val selectionArgs = arrayOf("")
            val sortOrd = "${ToDoContract.ToDoEntry.COLUMN_NAME_PRIORITY} DESC"

            val cursor = db.query(ToDoContract.ToDoEntry.TABLE_NAME, projection, null, null, null, null, sortOrd)

            var i = 0
            while(cursor.moveToNext())
            {
                val tempID = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))
                val tempName = cursor.getString(cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_NAME_NAME))
                val tempType = cursor.getString(cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_NAME_TYPE))
                val tempPrior = cursor.getInt(cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_NAME_PRIORITY))
                val tempItem : ToDoItem = ToDoItem(tempID, tempName, tempType, tempPrior)
                toDoItems[i] = tempItem
                i++
            }
            cursor.close()
        }
        else
        {
            toDoItems = emptyArray()
            countCursor.close()
        }

        return toDoItems
    }
}
