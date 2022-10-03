package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView

class AddCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        val questionEditText = findViewById<EditText>(R.id.new_question)
        val answerEditText = findViewById<EditText>(R.id.new_answer)

        val cancelButton = findViewById<ImageView>(R.id.cancel_button)
        val saveButton = findViewById<ImageView>(R.id.save_button)

        saveButton.setOnClickListener {
            val questionString = questionEditText.text.toString()
            val answerString = answerEditText.text.toString()

            val data = Intent() // create a new Intent, this is where we will put our data
            data.putExtra("QUESTION_KEY", questionString) // puts one string into the Intent, with the key as 'QUESTION_KEY'
            data.putExtra("ANSWER_KEY", answerString) // puts another string into the Intent, with the key as 'ANSWER_KEY'

            setResult(RESULT_OK, data) // set result code and bundle data for response
            finish() // closes this activity and pass data to the original activity that launched this activity
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }
}
