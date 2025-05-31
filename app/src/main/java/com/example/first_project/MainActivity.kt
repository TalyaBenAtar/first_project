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
import com.google.android.material.textview.MaterialTextView
import com.example.first_project.utilities.SignalManager
import com.example.first_project.utilities.SingleSoundPlayer
import com.example.first_project.utilities.TiltDetector
import com.example.first_project.interfaces.TiltCallback
import com.example.first_project.model.Score
import com.example.first_project.model.ScoreDataManager
import java.time.LocalDate
import android.location.Location
import android.location.LocationManager
import android.location.LocationListener
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity(), TiltCallback {

    private lateinit var main_LBL_score: MaterialTextView
    private lateinit var kotlinCoroutinesActivity: KotlinCoroutinesActivity
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var main_IMG_obstacles: Array<ImageView>
    private lateinit var main_IMG_cars: Array<ImageView>
    private lateinit var main_BTN_Left: ImageButton
    private lateinit var main_BTN_Right: ImageButton
    private var useSensorMode: Boolean = false
    private var moveObsticales: Boolean = false
    private lateinit var tiltDetector: TiltDetector

    private var score: Int = 0
    private var drivingSpeed= 1000L
    private var carPosition = 2 // Start in the middle (lane 2)
    private var wrongAnswers: Int = 0
    private var lifeCount: Int = 3
    private var playerLat: Double = 0.0
    private var playerLon: Double = 0.0
    private val LOCATION_PERMISSION_REQUEST = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SignalManager.init(this)
        ScoreDataManager.init(applicationContext)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
        } else {
            getLocation()
        }
        // Get sensor mode flag from intent
        useSensorMode = intent.getBooleanExtra("USE_SENSOR_MODE", false)

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

        kotlinCoroutinesActivity = KotlinCoroutinesActivity(this, main_IMG_obstacles)
        KotlinCoroutinesActivity.setDrivingSpeed(drivingSpeed)
        kotlinCoroutinesActivity.startTimer()
    }

    private val isGameOver: Boolean
        get() = wrongAnswers == lifeCount

    private fun initViews() {
        main_LBL_score.text = score.toString()
        main_BTN_Left.setOnClickListener { moveCarLeft() }
        main_BTN_Right.setOnClickListener { moveCarRight() }

        tiltDetector = TiltDetector(
            context = this,
            tiltCallback = this
        )

        if (useSensorMode) {
            // Hide buttons or disable them
            main_BTN_Left.visibility = View.INVISIBLE
            main_BTN_Right.visibility = View.INVISIBLE
        } else {
            // Show buttons
            main_BTN_Left.visibility = View.VISIBLE
            main_BTN_Right.visibility = View.VISIBLE
        }

        refreshUI()
    }

    override fun tiltLeft() {
        if (useSensorMode) {
            moveCarLeft()
        }
    }

    override fun tiltRight() {
        if (useSensorMode) {
        moveCarRight()
            }
    }

    override fun tiltBack() {
        drivingSpeed=1000L
        KotlinCoroutinesActivity.setDrivingSpeed(drivingSpeed)
        SignalManager.getInstance().toast("slower")
    }

    override fun tiltFront() {
        drivingSpeed=500L
        KotlinCoroutinesActivity.setDrivingSpeed(drivingSpeed)
        SignalManager.getInstance().toast("faster")
    }


    private fun moveCarLeft() {
        if (carPosition > 0 && moveObsticales) {
            main_IMG_cars[carPosition].visibility = View.INVISIBLE
            carPosition--
            main_IMG_cars[carPosition].visibility = View.VISIBLE
        }
    }

    private fun moveCarRight() {
        if (carPosition < main_IMG_cars.size - 1 && moveObsticales) {
            main_IMG_cars[carPosition].visibility = View.INVISIBLE
            carPosition++
            main_IMG_cars[carPosition].visibility = View.VISIBLE
        }
    }


    private fun refreshUI() {
        //Lost:
        if (isGameOver) {

            if (playerLat == 0.0 && playerLon == 0.0) {
                Log.w("MainActivity", "Using default location due to missing GPS")
                playerLat = 32.0853  // Default latitude (e.g. Tel Aviv)
                playerLon = 34.7818  // Default longitude
            }

            ScoreDataManager.addScore(
                Score.Builder()
                    .score(score)
                    .releaseDate(LocalDate.now())
                    .lat(playerLat)
                    .lon(playerLon)
                    .build()
            )
//            Log.d("SCORE", "Score saved: $score") //for debugging

            kotlinCoroutinesActivity.stopTimer()
            Log.d("Game Status", "Game Over! "+ score)
            changeActivity("She's dead!😭\n Game Over ", score)

        }else { // Ongoing game:
            if (wrongAnswers != 0){
                main_IMG_hearts[main_IMG_hearts.size - wrongAnswers]
                    .visibility = View.INVISIBLE
            }
            moveChildren()
        }
    }

    fun isVisible(index: Int): Boolean {
        if (index < 0 || index >= main_IMG_obstacles.size) {
            return false
        }
        return main_IMG_obstacles[index].isVisible
    }

    fun checkAnswer() {
        if (moveObsticales) {
            var ssp = SingleSoundPlayer(this)
            val obstacleIndex =
                (Constants.GameLogic.NUM_ROWS - 1) * Constants.GameLogic.LANE_NUMBER + carPosition
            if (obstacleIndex in main_IMG_obstacles.indices && isVisible(obstacleIndex) && main_IMG_obstacles[obstacleIndex].tag == "child") {
                if (wrongAnswers != main_IMG_hearts.size)
                    wrongAnswers++

                SignalManager.getInstance().vibrate()
                ssp.playSound(R.raw.car_crash_sound)
                SignalManager.getInstance().toast("She's dead!🥳")

            } else {
                if (obstacleIndex in main_IMG_obstacles.indices && isVisible(obstacleIndex) && main_IMG_obstacles[obstacleIndex].tag == "coin") {
                    score++
                    main_LBL_score.text = score.toString()
                    SignalManager.getInstance().vibrate()
                    SignalManager.getInstance().toast("SOOO RICHHH!!!🥳")
                    ssp.playSound(R.raw.coin_receiver)
                }
            }
            refreshUI()
        }
    }


    private fun moveChildren() {
        if (moveObsticales) {
            // Create a copy of which children are currently visible
            val moved = BooleanArray(main_IMG_obstacles.size) { false }

            for (row in Constants.GameLogic.NUM_ROWS - 1 downTo 0) { // Start from bottom row
                for (lane in 0 until Constants.GameLogic.LANE_NUMBER) {
                    val index = row * Constants.GameLogic.LANE_NUMBER + lane

                    if (index >= main_IMG_obstacles.size) continue

                    if (isVisible(index) && !moved[index]) {
                        // Hide current child
                        main_IMG_obstacles[index].visibility = View.INVISIBLE
                        moved[index] = true

                        val nextRowIndex = index + Constants.GameLogic.LANE_NUMBER
                        if (nextRowIndex < main_IMG_obstacles.size) {
                            // Show child below in same lane
                            setObstacleImageTag(index, nextRowIndex)
                            main_IMG_obstacles[nextRowIndex].visibility = View.VISIBLE
                            moved[nextRowIndex] = true
                        }
                    }
                }
            }
        }
    }

    private fun setObstacleImageTag(pastIndex: Int, newIndex: Int){
        if (main_IMG_obstacles[pastIndex].tag=="child"){
            main_IMG_obstacles[newIndex].setImageResource(R.drawable.child_target)
        } else {
            main_IMG_obstacles[newIndex].setImageResource(R.drawable.coin)
            main_IMG_obstacles[newIndex].scaleType = ImageView.ScaleType.FIT_CENTER
        }
        main_IMG_obstacles[newIndex].setTag(main_IMG_obstacles[pastIndex].tag)
    }


    private fun changeActivity(message: String, score: Int) {
        val intent = Intent(this, GameMenuActivity::class.java)
        var bundle = Bundle()
        bundle.putString(Constants.BundleKeys.MESSAGE_KEY, message)
        bundle.putInt(Constants.BundleKeys.SCORE_KEY, score)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()

    }

    private fun getLocation() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        try {
            locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        playerLat = location.latitude
                        playerLon = location.longitude
                    }
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                },
                null
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        } else {
            Log.w("MainActivity", "Location permission denied")
        }
    }


    private fun findViews() {
        main_LBL_score = findViewById(R.id.main_LBL_score)

        main_BTN_Left = findViewById(R.id.main_BTN_LEFT)
        main_BTN_Right = findViewById(R.id.main_BTN_Right)
        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )
        main_IMG_obstacles = arrayOf(
            findViewById(R.id.main_IMG_obstacle1),
            findViewById(R.id.main_IMG_obstacle2),
            findViewById(R.id.main_IMG_obstacle3),
            findViewById(R.id.main_IMG_obstacle4),
            findViewById(R.id.main_IMG_obstacle5),
            findViewById(R.id.main_IMG_obstacle6),
            findViewById(R.id.main_IMG_obstacle7),
            findViewById(R.id.main_IMG_obstacle8),
            findViewById(R.id.main_IMG_obstacle9),
            findViewById(R.id.main_IMG_obstacle10),
            findViewById(R.id.main_IMG_obstacle11),
            findViewById(R.id.main_IMG_obstacle12),
            findViewById(R.id.main_IMG_obstacle13),
            findViewById(R.id.main_IMG_obstacle14),
            findViewById(R.id.main_IMG_obstacle15),
            findViewById(R.id.main_IMG_obstacle16),
            findViewById(R.id.main_IMG_obstacle17),
            findViewById(R.id.main_IMG_obstacle18),
            findViewById(R.id.main_IMG_obstacle19),
            findViewById(R.id.main_IMG_obstacle20),
            findViewById(R.id.main_IMG_obstacle21),
            findViewById(R.id.main_IMG_obstacle22),
            findViewById(R.id.main_IMG_obstacle23),
            findViewById(R.id.main_IMG_obstacle24),
            findViewById(R.id.main_IMG_obstacle25),
            findViewById(R.id.main_IMG_obstacle26),
            findViewById(R.id.main_IMG_obstacle27),
            findViewById(R.id.main_IMG_obstacle28),
            findViewById(R.id.main_IMG_obstacle29),
            findViewById(R.id.main_IMG_obstacle30)
        )
        main_IMG_cars = arrayOf(
            findViewById(R.id.main_IMG_car1),
            findViewById(R.id.main_IMG_car2),
            findViewById(R.id.main_IMG_car3),
            findViewById(R.id.main_IMG_car4),
            findViewById(R.id.main_IMG_car5)
        )

        resetGame()
    }

    private fun resetGame(){
        KotlinCoroutinesActivity.setDrivingSpeed(drivingSpeed)
        // Make all children invisible
        for (obstacle  in main_IMG_obstacles) {
            obstacle .visibility = View.INVISIBLE
        }

        // Show only the middle car
        main_IMG_cars[0].visibility = View.INVISIBLE
        main_IMG_cars[1].visibility = View.INVISIBLE
        main_IMG_cars[carPosition].visibility = View.VISIBLE
        main_IMG_cars[3].visibility = View.INVISIBLE
        main_IMG_cars[4].visibility = View.INVISIBLE

    }

    override fun onResume() {
        super.onResume()
        tiltDetector.start()
        kotlinCoroutinesActivity.startTimer()
        moveObsticales=true
    }

    override fun onPause() {
        super.onPause()
        tiltDetector.stop()
        kotlinCoroutinesActivity.stopTimer()
        SignalManager.getInstance()
        moveObsticales=false
        finish()
    }
}

