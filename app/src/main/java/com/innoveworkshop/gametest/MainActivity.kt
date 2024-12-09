package com.innoveworkshop.gametest

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.innoveworkshop.gametest.assets.DroppingRectangle
import com.innoveworkshop.gametest.assets.Humans
import com.innoveworkshop.gametest.engine.Circle
import com.innoveworkshop.gametest.engine.GameObject
import com.innoveworkshop.gametest.engine.GameSurface
import com.innoveworkshop.gametest.engine.Rectangle
import com.innoveworkshop.gametest.engine.Vector

class MainActivity : AppCompatActivity() {
    protected var gameSurface: GameSurface? = null
    protected var dropButton: Button? = null
    protected var leftButton: Button? = null
    protected var rightButton: Button? = null
    protected var pauseButton: Button? = null

    var isPaused = true
    private var isGameStarted = false

    protected var game: Game? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameSurface = findViewById<View>(R.id.gameSurface) as GameSurface
        gameSurface?.initializeWithMainActivity(this)
        game = Game()
        gameSurface!!.setRootGameObject(game)

        setupControls()

        gameSurface?.viewTreeObserver?.addOnWindowFocusChangeListener { hasFocus ->
            if (!hasFocus) {
                // App has lost focus (e.g., multitasking mode)
                if (!isPaused) { // Only pause if not already paused
                    isPaused = true
                    pauseButton?.text = if (isGameStarted){
                        "Resume"
                    } else{
                        "Start"
                    }
                    gameSurface?.pauseStopwatch()
                }
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setupControls() {

        pauseButton = findViewById<View>(R.id.pause_button) as Button
        pauseButton!!.text = "Start"
        pauseButton!!.setOnClickListener {
            if (!isGameStarted) {
                // First click starts the game
                isGameStarted = true
                isPaused = false
                pauseButton!!.text = "Pause"
                game!!.startGame()
                gameSurface?.startStopwatch()
            } else {
                // Subsequent clicks toggle pause/resume
                isPaused = !isPaused
                val buttonText = if (isPaused) "Resume" else "Pause"
                pauseButton!!.text = buttonText
                if (isPaused) {
                    gameSurface?.pauseStopwatch()
                } else {
                    gameSurface?.startStopwatch()
                }
            }



//            game?.let {
//                isPaused = !isPaused // Toggle pause state
//                val buttonText = if (isPaused) "Resume" else "Pause"
//                pauseButton!!.text = buttonText
//            }
//
//            if (isPaused) {
//                gameSurface?.pauseStopwatch() // Pause the stopwatch
//            } else {
//                gameSurface?.startStopwatch() // Resume the stopwatch
//            }
        }

        dropButton = findViewById<View>(R.id.drop_button) as Button
        dropButton!!.setOnClickListener {
            if (isPaused == false){
                game!!.spawnNewFallingItem()
            }
        }

        leftButton = findViewById<View>(R.id.left_button) as Button
        rightButton = findViewById<View>(R.id.right_button) as Button

        leftButton!!.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    game!!.moveLeft = true
                    println("Move Left: true")
                }
                android.view.MotionEvent.ACTION_UP -> {
                    game!!.moveLeft = false
                    println("Move Left: false")
                }
            }
            true
        }

        rightButton!!.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    game!!.moveRight = true
                    println("Move Right: true")
                }
                android.view.MotionEvent.ACTION_UP -> {
                    game!!.moveRight = false
                    println("Move Right: false")
                }

            }
            true
        }

    }



    inner class Game : GameObject() {
        private var surface: GameSurface? = null
        var circle: Circle? = null
        var rectangle: Rectangle? = null
        private val fallingItems = mutableListOf<DroppingRectangle>()
        private val walkingHumans = mutableListOf<Humans>()

        var moveLeft = false
        var moveRight = false
        private var velocity = 0f // Current velocity
        private val acceleration = 2f // Acceleration
        private val maxSpeed = 15f // Maximum speed
        private val deceleration = 1.5f // Deceleration


        private var elapsedTime = 0f // Tracks elapsed time for spawning humans
        private val spawnInterval = 0.4f // Spawn humans every 1 second

        override fun onStart(surface: GameSurface?) {
            super.onStart(surface)
            this.surface = surface

            if (rectangle == null){
                rectangle = Rectangle(
                    Vector((surface!!.width / 2).toFloat(), (surface.height / 7).toFloat()),
                    25f,70f,
                    Color.BLACK
                )
                surface.addGameObject(rectangle!!)
            }


        }

        fun spawnNewFallingItem() {
            if (rectangle == null || surface == null) return

            // Create a new falling rectangle at the rectangle's position
            val item = DroppingRectangle(
                Vector(rectangle!!.position.x, rectangle!!.position.y), // Use the existing rectangle instance
                70f,
                30f,
                2f,
                Color.BLACK,
                this@MainActivity
            )

            fallingItems.add(item)
            surface!!.addGameObject(item) // Ensure surface is non-null
        }

        fun spawnNewHuman() {

            val screenWidth = surface!!.width
            val screenHeight = surface!!.height
            val spawnX = if (Math.random() < 0.5) 0f else (screenWidth - 70f) //adjust human width fatness

            // Create a new human
            val item = Humans(
                Vector(spawnX, (screenHeight - 25f)), //adjust human height fatness
                25f,
                70f,
                2f,
                Color.BLACK,
                this@MainActivity
            )

            walkingHumans.add(item)
            surface!!.addGameObject(item) // Ensure surface is non-null
        }


        fun startGame() {
            isPaused = false
            elapsedTime = 0f
        }

        override fun onFixedUpdate() {

            if (isPaused) return

            super.onFixedUpdate()

            //println("Velocity: $velocity, rectangle Position X: ${rectangle!!.position.x}")

            if (moveLeft && !moveRight) {

                velocity -= acceleration

                if (velocity < -maxSpeed) {
                    velocity = -maxSpeed
                }

            } else if (moveRight && !moveLeft) {

                velocity += acceleration

                if (velocity > maxSpeed) {

                    velocity = maxSpeed
                }

            } else {
                // Deceleration when moving right
                if (velocity > 0) {

                    velocity -= deceleration

                    if (velocity < 0){

                        velocity = 0f
                    }
                // Deceleration moving left with negative velocity
                } else if (velocity < 0) {

                    velocity += deceleration

                    if (velocity > 0) {

                        velocity = 0f
                    }
                }
            }

            // Update rectangle position
            val newPositionX = (rectangle!!.position.x + velocity).coerceIn(0f, surface!!.width - rectangle!!.width)

            // Check if the rectangle is at a boundary and adjust velocity
            if (newPositionX == 0f && velocity < 0) {
                velocity = 0f // Stop movement to the left
            } else if (newPositionX == surface!!.width - rectangle!!.width && velocity > 0) {
                velocity = 0f // Stop movement to the right
            }

            // Update the rectangle's position
            rectangle!!.position.x = newPositionX

            //brick
            val brickiterator = fallingItems.iterator()
            while (brickiterator.hasNext()) {
                val item = brickiterator.next()

                // Let DroppingRectangle handle its own physics
                item.onFixedUpdate()

                // Check for ground to destroy
                if (item.isOutOfBounds(surface!!.height.toFloat())) {
                    item.destroy()
                    brickiterator.remove()
                }
            }

            elapsedTime += 0.016f // 60 FPS update rate
            if (elapsedTime >= spawnInterval) {
                spawnNewHuman()
                elapsedTime = 0f
            }

            //human spawn
            val humanIterator = walkingHumans.iterator()
            while (humanIterator.hasNext()) {
                val human = humanIterator.next()

                // Humans file handles their own physics
                human.onFixedUpdate()

                // human removal out of bounds
                if (human.isOutOfBounds(surface!!.height.toFloat())) {
                    human.destroy()
                    humanIterator.remove()
                }
            }
        }
    }
}
