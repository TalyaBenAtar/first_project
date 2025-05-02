package com.example.first_project


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.first_project.utilities.Constants

import com.example.first_project.utilities.SignalManager

class MainActivity : AppCompatActivity() {

//    private lateinit var main_LBL_score: MaterialTextView
    private lateinit var kotlinCoroutinesActivity: KotlinCoroutinesActivity

    private lateinit var main_IMG_hearts: Array<AppCompatImageView>

    private lateinit var main_IMG_children: Array<ImageView>

    private lateinit var main_IMG_cars: Array<ImageView>

    private lateinit var main_BTN_Left: ImageButton

    private lateinit var main_BTN_Right: ImageButton



    private var carPosition = 1 // Start in the middle (lane 1)

    private var wrongAnswers: Int = 0

    private var lifeCount: Int = 3


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SignalManager.init(this)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViews()
        lifeCount = main_IMG_hearts.size
        initViews()

        kotlinCoroutinesActivity = KotlinCoroutinesActivity(this, main_IMG_children)
        kotlinCoroutinesActivity.startTimer()

    }

    private val isGameOver: Boolean
        get() = wrongAnswers == lifeCount

    private fun initViews() {
//        main_LBL_score.text = gameManager.score.toString()
        main_BTN_Left.setOnClickListener { moveCarLeft()}
        main_BTN_Right.setOnClickListener { moveCarRight();}

        //checkAnswer()
        refreshUI()
    }

    private fun moveCarLeft() {
        if (carPosition > 0) {
            main_IMG_cars[carPosition].visibility = View.INVISIBLE
            carPosition--
            main_IMG_cars[carPosition].visibility = View.VISIBLE
        }
    }

    private fun moveCarRight() {
        if (carPosition < main_IMG_cars.size - 1) {
            main_IMG_cars[carPosition].visibility = View.INVISIBLE
            carPosition++
            main_IMG_cars[carPosition].visibility = View.VISIBLE
        }
    }




    private fun refreshUI() {
        //Lost:
        if (isGameOver) {
            kotlinCoroutinesActivity.stopTimer()
            Log.d("Game Status", "Game Over! ")
            changeActivity("She's dead!😭\n Game Over ")
//        }else if (gameManager.isGameEnded) {  //Game Ended:
//            Log.d("Game Status", "You Won! " + gameManager.score)
//            changeActivity("🥳You Won! " ,gameManager.score)

        }else { // Ongoing game:

            if (wrongAnswers != 0){
                main_IMG_hearts[main_IMG_hearts.size - wrongAnswers]
                    .visibility = View.INVISIBLE
            }
            moveChildren()
        }


    }

    fun isVisible(index: Int): Boolean {
        if (index < 0 || index >= main_IMG_children.size) {
            return false
        }
        return main_IMG_children[index].isVisible
    }

    fun checkAnswer() {
        val childIndex = (Constants.GameLogic.NUM_ROWS - 1) * Constants.GameLogic.LANE_NUMBER + carPosition
        if (childIndex in main_IMG_children.indices && isVisible(childIndex)) {
            if (wrongAnswers!=main_IMG_hearts.size)
                wrongAnswers++
            SignalManager.getInstance().vibrate()
            SignalManager.getInstance().toast("She's dead!🥳")
        }
        refreshUI()
    }


    private fun moveChildren() {
        // Create a copy of which children are currently visible
        val moved = BooleanArray(main_IMG_children.size) { false }

        for (row in Constants.GameLogic.NUM_ROWS - 1 downTo 0) { // Start from bottom row
            for (lane in 0 until Constants.GameLogic.LANE_NUMBER) {
                val index = row * Constants.GameLogic.LANE_NUMBER + lane

                if (index >= main_IMG_children.size) continue

                if (isVisible(index) && !moved[index]) {
                    // Hide current child
                    main_IMG_children[index].visibility = View.INVISIBLE
                    moved[index] = true

                    val nextRowIndex = index + Constants.GameLogic.LANE_NUMBER
                    if (nextRowIndex < main_IMG_children.size) {
                        // Show child below in same lane
                        main_IMG_children[nextRowIndex].visibility = View.VISIBLE
                        moved[nextRowIndex] = true
                    }
                }
            }
        }
    }





    private fun changeActivity(message: String) {
        val intent = Intent(this, EndGameActivity::class.java)
        var bundle = Bundle()
        bundle.putString(Constants.BundleKeys.MESSAGE_KEY, message)
        //bundle.putInt(Constants.BundleKeys.SCORE_KEY, score)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()

    }

    private fun findViews() {
//        main_LBL_score = findViewById(R.id.main_LBL_score) --> will be needed in the next assignment

        main_BTN_Left = findViewById(R.id.main_BTN_LEFT)
        main_BTN_Right = findViewById(R.id.main_BTN_Right)
        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )
        main_IMG_children = arrayOf(
            findViewById(R.id.main_IMG_child1),
            findViewById(R.id.main_IMG_child2),
            findViewById(R.id.main_IMG_child3),
            findViewById(R.id.main_IMG_child4),
            findViewById(R.id.main_IMG_child5),
            findViewById(R.id.main_IMG_child6),
            findViewById(R.id.main_IMG_child7),
            findViewById(R.id.main_IMG_child8),
            findViewById(R.id.main_IMG_child9),
            findViewById(R.id.main_IMG_child10),
            findViewById(R.id.main_IMG_child11),
            findViewById(R.id.main_IMG_child12)
        )
        main_IMG_cars = arrayOf(
            findViewById(R.id.main_IMG_car1),
            findViewById(R.id.main_IMG_car2),
            findViewById(R.id.main_IMG_car3),
        )

        resetGame()
    }

    private fun resetGame(){
        // Make all children invisible
        for (child in main_IMG_children) {
            child.visibility = View.INVISIBLE
        }

        // Show only the middle car
        main_IMG_cars[0].visibility = View.INVISIBLE
        main_IMG_cars[carPosition].visibility = View.VISIBLE
        main_IMG_cars[2].visibility = View.INVISIBLE

    }


}