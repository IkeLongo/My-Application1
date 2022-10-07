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

    lateinit var flashcardDatabase: FlashcardDatabase

    var allFlashcards = mutableListOf<Flashcard>()

    var currCardDisplayedIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flashcardDatabase = FlashcardDatabase(this)
        allFlashcards = flashcardDatabase.getAllCards().toMutableList()

        val flashcardQuestion = findViewById<TextView>(R.id.flashcard_question)
        val flashcardAnswer = findViewById<TextView>(R.id.flashcard_answer)
        val addButton = findViewById<ImageView>(R.id.add_question_button)
        val cancelButton = findViewById<ImageView>(R.id.cancel_button)

        if (allFlashcards.size > 0) {
            flashcardQuestion.text = allFlashcards[0].question
            flashcardAnswer.text = allFlashcards[0].answer
        }

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

                Log.i("Isaac: MainActivity", "question: $questionString")
                Log.i("Isaac: MainActivity", "answer: $answerString")

                flashcardQuestion.text = questionString
                flashcardAnswer.text = answerString

                // Save newly created flashcard to database
                if (!questionString.isNullOrEmpty() && !answerString.isNullOrEmpty()) {

                    flashcardDatabase.insertCard(Flashcard(questionString, answerString))
                    // Update set of flashcards to include new card
                    allFlashcards = flashcardDatabase.getAllCards().toMutableList()
                }
              } else {
                    Log.i("MainActivity", "Returned null data from AddCardActivity")
              }
            }

        addButton.setOnClickListener {
            val intent = Intent(this, AddCardActivity::class.java)
            resultLauncher.launch(intent)
        }
        val nextButton = findViewById<ImageView>(R.id.next_button)

        nextButton.setOnClickListener {
            // don't try to go to next card if you have no cards to begin with
            if (allFlashcards.size == 0) {
                // return here, so that the rest of the code in this onClickListener doesn't execute
                return@setOnClickListener
            }

            // advance our pointer index so we can show the next card
            currCardDisplayedIndex++

            // make sure we don't get an IndexOutOfBoundsError if we are viewing the last indexed card in our list
            if (currCardDisplayedIndex >= allFlashcards.size) {
                // Go back to the beginning
                currCardDisplayedIndex = 0
            }

            // set the question and answer TextViews with data from the database
            allFlashcards = flashcardDatabase.getAllCards().toMutableList()

            val question = allFlashcards[currCardDisplayedIndex].question
            val answer = allFlashcards[currCardDisplayedIndex].answer

            flashcardAnswer.text = answer
            flashcardQuestion.text = question
        }
    }
}