package com.example.myapplication

import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.os.CountDownTimer
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    lateinit var flashcardDatabase: FlashcardDatabase

    var allFlashcards = mutableListOf<Flashcard>()

    var currCardDisplayedIndex = 0

    var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flashcardDatabase = FlashcardDatabase(this)
        allFlashcards = flashcardDatabase.getAllCards().toMutableList()

        val flashcardQuestion = findViewById<TextView>(R.id.flashcard_question)
        val flashcardAnswer = findViewById<TextView>(R.id.flashcard_answer)
        val addButton = findViewById<ImageView>(R.id.add_question_button)
        val cancelButton = findViewById<ImageView>(R.id.cancel_button)
        val nextButton = findViewById<ImageView>(R.id.next_button)

        // (1) Load animation resource files for next button
        val leftOutAnim = AnimationUtils.loadAnimation(this, R.anim.left_out)
        val rightInAnim = AnimationUtils.loadAnimation(this, R.anim.right_in)

        if (allFlashcards.size > 0) {
            flashcardQuestion.text = allFlashcards[0].question
            flashcardAnswer.text = allFlashcards[0].answer
        }

        flashcardQuestion.setOnClickListener {
            flashcardAnswer.visibility = View.VISIBLE
            flashcardQuestion.visibility = View.INVISIBLE

            val answerSideView = findViewById<View>(R.id.flashcard_answer)
            val questionSideView = findViewById<View>(R.id.flashcard_question)

            // get the center for the clipping circle

            val cx = answerSideView.width / 2
            val cy = answerSideView.height / 2

            // get the final radius for the clipping circle

            val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

            // create the animator for this view (the start radius is zero)

            val anim = ViewAnimationUtils.createCircularReveal(answerSideView, cx, cy, 0f, finalRadius)

            // hide the question and show the answer to prepare for playing the animation!

            questionSideView.visibility = View.INVISIBLE
            answerSideView.visibility = View.VISIBLE

            anim.duration = 2000
            anim.start()

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
                val questionString = data.getStringExtra("QUESTION_KEY")
                // 'name' needs to match the key we used when we put the string in the Intent
                val answerString = data.getStringExtra("ANSWER_KEY")
                // 'name' needs to match the key we used when we put the string in the Intent

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
            val i = Intent(this, AddCardActivity::class.java)
            resultLauncher.launch(i)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }

        nextButton.setOnClickListener {
            // don't try to go to next card if you have no cards to begin with
            if (allFlashcards.size == 0) {
                // return here, so that the rest of the code in this onClickListener doesn't execute
                return@setOnClickListener
            }

            // (2) Play the animations in sequence by setting listeners
            // to know when animation finished
            leftOutAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    // this method is called when the animation first starts

                    flashcardAnswer.visibility = View.INVISIBLE
                    flashcardQuestion.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animation?) {
                    // this method is called when the animation is finished playing
                    flashcardQuestion.startAnimation(rightInAnim)

                    // advance our pointer index so we can show the next card
                    currCardDisplayedIndex++

                    // make sure we don't get an IndexOutOfBoundsError if we are viewing
                    // the last indexed card in our list
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

                    flashcardAnswer.visibility = View.INVISIBLE
                    flashcardQuestion.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(animation: Animation?) {
                    // we don't need to worry about this method
                }
            })
            flashcardQuestion.startAnimation(leftOutAnim)
        }

        countDownTimer = object : CountDownTimer(15000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                findViewById<TextView>(R.id.timer).text = "" + millisUntilFinished / 1000
            }

            override fun onFinish() {}

            private fun startTimer() {
                countDownTimer?.cancel()
                countDownTimer?.start()
            }
        }
    }
}