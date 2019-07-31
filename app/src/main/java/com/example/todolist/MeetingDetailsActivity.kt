package com.example.todolist

import android.annotation.TargetApi
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.BaseColumns
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.format.DateFormat
import android.widget.*

import kotlinx.android.synthetic.main.activity_meeting_details.*
import kotlinx.android.synthetic.main.content_meeting_details.*
import org.w3c.dom.Text

class MeetingDetailsActivity : AppCompatActivity() {

    var newItem = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_details)
        setSupportActionBar(toolbar)

        val nameText : EditText = findViewById(R.id.meetingWhoEditText)
        val addressText : EditText = findViewById(R.id.meetingAddressEditText)
        val timeText : EditText = findViewById(R.id.meetingTimeEditText)
        val dateText : EditText = findViewById(R.id.meetingDateEditText)
        val phoneText : EditText = findViewById(R.id.meetingPhoneEditText)
        val emailText : EditText = findViewById(R.id.meetingEmailEditText)
        val notesText : EditText = findViewById(R.id.meetingNotesEditText)


        //if entry already exists, load the current information in the edit text views
        val b : Bundle? = intent.extras
        var id : Long = -1
        val meetingName : String
        if(b != null)
        {
            id = b["id"] as Long
            meetingName = b["name"] as String
            load(id, meetingName, nameText, addressText, timeText, dateText, phoneText, emailText, notesText)
        }

        val addMeetingDetailsButton : Button = findViewById(R.id.addMeetingDetailsButton)
        addMeetingDetailsButton.setOnClickListener {
            // Grab all the current values
            val name: String = nameText.text.toString()
            val address: String = addressText.text.toString()
            val time: String = timeText.text.toString()
            val date: String = dateText.text.toString()
            val phone: String = phoneText.text.toString()
            val email: String = emailText.text.toString()
            val notes: String = notesText.text.toString()

            addDetails(id, name, address, time, date, phone, email, notes)
        }

        meetingTimeEditText.setOnClickListener{
            TimePickerFragment().show(supportFragmentManager, "Meeting Time")
        }
    }

    private fun load(id : Long, name : String, nameText : EditText, addressText : EditText, timeText : EditText, dateText : EditText,
                     phoneText : EditText, emailText : EditText, notesText : EditText) {
        toolbar.title = name
        val entry = ToDoContract.MeetingEntry
        //Load all info from meeting database, set edit texts accordingly
        val dbHelper = ToDoContract.ToDoDBHelper(this@MeetingDetailsActivity)
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            entry.COLUMN_NAME_NAME,
            entry.COLUMN_NAME_ADDRESS,
            entry.COLUMN_NAME_TIME,
            entry.COLUMN_NAME_DATE,
            entry.COLUMN_NAME_EMAIL,
            entry.COLUMN_NAME_PHONE,
            entry.COLUMN_NAME_NOTES
        )

        val selection = "${entry.COLUMN_NAME_ID} = ?"
        val selectionArgs: Array<String> = arrayOf("$id")
        val sortOrd = ""

        val cursor = db.query(entry.TABLE_NAME, projection, selection, selectionArgs, null, null, null)

        var i = 0
        if (cursor != null) {
            while (cursor.moveToNext()) {
                nameText.setText(cursor.getString(cursor.getColumnIndex(entry.COLUMN_NAME_NAME)))
                addressText.setText(cursor.getString(cursor.getColumnIndex(entry.COLUMN_NAME_ADDRESS)))
                timeText.setText(cursor.getString(cursor.getColumnIndex(entry.COLUMN_NAME_TIME)))
                dateText.setText(cursor.getString(cursor.getColumnIndex(entry.COLUMN_NAME_DATE)))
                emailText.setText(cursor.getString(cursor.getColumnIndex(entry.COLUMN_NAME_EMAIL)))
                phoneText.setText(cursor.getString(cursor.getColumnIndex(entry.COLUMN_NAME_PHONE)))
                notesText.setText(cursor.getString(cursor.getColumnIndex(entry.COLUMN_NAME_NOTES)))
                i++
                newItem = false
            }
            cursor.close()
        }
    }

    private fun addDetails(id : Long, name : String, address : String, time : String, date : String, phone : String, email : String, notes : String)
    {
        val entry = ToDoContract.MeetingEntry
        //Check validity of name, address, time, and date
        when{
            name.isEmpty() -> {
                Toast.makeText(this, "Please enter a valid person or place", Toast.LENGTH_LONG).show()
            }
            address.isEmpty() -> {
                Toast.makeText(this, "Please enter a valid address", Toast.LENGTH_SHORT).show()
            }
            time.isEmpty() -> {
                Toast.makeText(this, "Please enter a valid time", Toast.LENGTH_SHORT).show()
            }
            date.isEmpty() -> {
                Toast.makeText(this, "Please enter a valid date", Toast.LENGTH_SHORT).show()
            }
            else -> {
                //Save information to the meeting database
                val dbHelper = ToDoContract.ToDoDBHelper(this@MeetingDetailsActivity)
                val db = dbHelper.writableDatabase
                if(newItem) { // if new entry, insert
                    val values = ContentValues().apply {
                        put(entry.COLUMN_NAME_ID, id)
                        put(entry.COLUMN_NAME_NAME, name)
                        put(entry.COLUMN_NAME_ADDRESS, address)
                        put(entry.COLUMN_NAME_TIME, time)
                        put(entry.COLUMN_NAME_DATE, date)
                        put(entry.COLUMN_NAME_EMAIL, email)
                        put(entry.COLUMN_NAME_PHONE, phone)
                        put(entry.COLUMN_NAME_NOTES, notes)
                    }

                    val result = db.insert(entry.TABLE_NAME, null, values)
                }
                else // if entry exists, update
                {
                    val values = ContentValues().apply {
                        put(entry.COLUMN_NAME_NAME, name)
                        put(entry.COLUMN_NAME_ADDRESS, address)
                        put(entry.COLUMN_NAME_TIME, time)
                        put(entry.COLUMN_NAME_DATE, date)
                        put(entry.COLUMN_NAME_EMAIL, email)
                        put(entry.COLUMN_NAME_PHONE, phone)
                        put(entry.COLUMN_NAME_NOTES, notes)
                    }
                    val result = db.update(entry.TABLE_NAME,values,"${entry.COLUMN_NAME_ID} = ?", Array<String>(1){"$id"})
                }

                val intent : Intent = Intent(this@MeetingDetailsActivity, ScrollingActivity::class.java)
                startActivity(intent)
            }
        }
    }

    class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

        @TargetApi(24)
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val cal = Calendar.getInstance()
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val min = cal.get(Calendar.MINUTE)

            return TimePickerDialog(activity, this, hour, min, DateFormat.is24HourFormat(activity))
        }

        override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
            val text = "$hourOfDay : $minute"
            activity?.meetingTimeEditText?.setText(text)
            //Toast.makeText(activity, "The time set is $hourOfDay : $minute", Toast.LENGTH_SHORT).show()
        }

    }
}
