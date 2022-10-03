package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setContentView(R.layout.activity_main)

        val flashcardQuestion = findViewById<TextView>(R.id.flashcard_question)
        val flashcardAnswer = findViewById<TextView>(R.id.flashcard_answer)
        val addButton = findViewById<ImageView>(R.id.add_question_button)
        val cancelButton = findViewById<ImageView>(R.id.cancel_button)

        flashcardQuestion.setOnClickListener {
            flashcardAnswer.visibility = View.VISIBLE
            flashcardQuestion.visibility = View.INVISIBLE

            Snackbar.make(flashcardQuestion, "Question button was clicked",
                Snackbar.LENGTH_SHORT).show()

            Log.i("Isaac", "Question button was clicked")
        }

        flashcardAnswer.setOnClickListener {
            flashcardAnswer.visibility = View.INVISIBLE
            flashcardQuestion.visibility = View.VISIBLE
        }

        addButton.setOnClickListener {
            val intent = Intent(this, AddCardActivity::class.java)
            startActivity(intent)
        }

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->

            val data: Intent? = result.data

            if (data != null) { // Check that we have data returned
                val questionString = data.getStringExtra("QUESTION_KEY") // 'name' needs to match the key we used when we put the string in the Intent
                val answerString = data.getStringExtra("ANSWER_KEY") // 'name' needs to match the key we used when we put the string in the Intent

                flashcardQuestion.text = questionString
                flashcardAnswer.text = answerString

                Log.i("Isaac: MainActivity", "question: $questionString")
                Log.i("Isaac: MainActivity", "answer: $answerString")
            } else {
                Log.i("Isaac: MainActivity", "Returned null data from AddCardActivity")
            }
        }

        addButton.setOnClickListener {
            val intent = Intent(this, AddCardActivity::class.java)
            resultLauncher.launch(intent)
        }
    }
}

