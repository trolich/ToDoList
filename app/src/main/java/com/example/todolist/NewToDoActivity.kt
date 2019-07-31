package com.example.todolist

import android.content.ContentValues
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

class NewToDoActivity : AppCompatActivity(){

    private var newRowID : Long = 0
    private var name = "Temp"
    private var priority = 0
    private var toDoType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_to_do)

        val spinner : Spinner = findViewById(R.id.itemTypeSpinner)
        ArrayAdapter.createFromResource(this,R.array.item_types,android.R.layout.simple_spinner_item).
            also{adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }

        val priorityGroup : RadioGroup = findViewById(R.id.priorityGroup)
        priorityGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{
            _, checkId -> val radio : RadioButton = findViewById(checkId)
            priority = when(radio.text)
            {
                "Low Priority" -> 1
                "Medium Priority" -> 2
                "High Priority" -> 3
                else -> 0
            }
        })

        val addButton : Button = findViewById(R.id.addButton)
        addButton.setOnClickListener{addToDo()}

        val detailsButton : Button = findViewById(R.id.newToDoAddDetailsButton)
        detailsButton.setOnClickListener {
            val itemText: EditText = findViewById(R.id.itemName)
            val typeSpinner: Spinner = findViewById(R.id.itemTypeSpinner)
            toDoType = typeSpinner.selectedItem.toString()
            name = itemText.text.toString()
            addDetails(name, toDoType) }
    }

    private fun addToDo()
    {
        if(saveData() >= 0)
        {
            val intent = Intent(this@NewToDoActivity, ScrollingActivity::class.java )
            startActivity(intent)
        }
    }

    private fun addDetails(name : String, itemType : String)
    {
        val id = saveData()
        if(id >= 0) {
            when (itemType) {
                "Meeting" -> {
                    val bundle: Bundle = Bundle()
                    bundle.putLong("id", id)
                    bundle.putString("name", name)
                    val intent = Intent(this@NewToDoActivity, MeetingDetailsActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)}
                else -> {
                    val intent = Intent(this@NewToDoActivity, ScrollingActivity::class.java )
                    startActivity(intent)}
            }
        }
    }

    private fun saveData() : Long {
        val itemText: EditText = findViewById(R.id.itemName)
        val typeSpinner: Spinner = findViewById(R.id.itemTypeSpinner)
        toDoType = typeSpinner.selectedItem.toString()
        name = itemText.text.toString()
        if (itemText.text.isEmpty()) {
            Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show()
            return -1
        } else if (priority == 0) {
            Toast.makeText(this, "Please select a priority", Toast.LENGTH_SHORT).show()
            return -1
        } else {
            Toast.makeText(
                this,
                "Adding item: $name with priority $priority and type $toDoType",
                Toast.LENGTH_LONG
            ).show()

            val dbHelper = ToDoContract.ToDoDBHelper(this@NewToDoActivity)
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(ToDoContract.ToDoEntry.COLUMN_NAME_NAME, name)
                put(ToDoContract.ToDoEntry.COLUMN_NAME_PRIORITY, priority)
                put(ToDoContract.ToDoEntry.COLUMN_NAME_TYPE, toDoType)
            }

            newRowID = db.insert(ToDoContract.ToDoEntry.TABLE_NAME, null, values)
            return newRowID
        }
    }
}
