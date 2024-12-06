package com.innoveworkshop.gametest

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
    protected var upButton: Button? = null
    protected var leftButton: Button? = null
    protected var rightButton: Button? = null

    protected var game: Game? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameSurface = findViewById<View>(R.id.gameSurface) as GameSurface
        game = Game()
        gameSurface!!.setRootGameObject(game)

        setupControls()
    }


    private fun setupControls() {
        upButton = findViewById<View>(R.id.up_button) as Button
        upButton!!.setOnClickListener { game!!.spawnNewFallingItem() }

        leftButton = findViewById<View>(R.id.left_button) as Button
        leftButton!!.setOnClickListener { game!!.circle!!.position.x -= 50f }

        rightButton = findViewById<View>(R.id.right_button) as Button
        rightButton!!.setOnClickListener { game!!.circle!!.position.x += 50f }
    }

    inner class Game : GameObject() {
        private var surface: GameSurface? = null
        var circle: Circle? = null
        private val fallingItems = mutableListOf<DroppingRectangle>()
        private val walkingHumans = mutableListOf<Humans>()

        private var elapsedTime = 0f // Tracks elapsed time for spawning humans
        private val spawnInterval = 0.4f // Spawn humans every 1 second

        override fun onStart(surface: GameSurface?) {
            super.onStart(surface)
            this.surface = surface

            circle = Circle(
                (surface!!.width / 2).toFloat(), (surface.height / 7).toFloat(),
                100f,
                Color.RED
            )
            surface.addGameObject(circle!!)

//            surface.addGameObject(
//                Rectangle(
//                    Vector((surface.width / 2).toFloat(), (surface.height / 3).toFloat()),
//                    200f, 100f, Color.GREEN
//                )
//            )

//            surface.addGameObject(
//                DroppingRectangle(
//                    Vector((surface.width / 3).toFloat(), (surface.height / 3).toFloat()),
//                    100f, 100f, 10f, Color.rgb(128, 14, 80)
//                )
//            )
        }
        fun spawnNewFallingItem() {
            if (circle == null || surface == null) return

            // Create a new falling rectangle at the circle's position
            val item = DroppingRectangle(
                Vector(circle!!.position.x, circle!!.position.y), // Use the existing circle instance
                70f,
                30f,
                2f,
                Color.rgb(128, 14, 80)
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
                // Adjust 80f to match human width
                Vector(spawnX, (screenHeight - 30f)), // Use the existing circle instance
                25f,
                70f,
                2f,
                Color.rgb(128, 14, 80)
            )

            walkingHumans.add(item)
            surface!!.addGameObject(item) // Ensure surface is non-null
        }
        override fun onFixedUpdate() {
            super.onFixedUpdate()

            val brickiterator = fallingItems.iterator()
            while (brickiterator.hasNext()) {
                val item = brickiterator.next()

                // Let DroppingRectangle handle its own physics
                item.onFixedUpdate()

                // Check for ground or boundary collision
                if (item.isOutOfBounds(surface!!.height.toFloat())) {
                    item.destroy()
                    brickiterator.remove()
                }
            }

            elapsedTime += 0.016f // Assuming a 60 FPS update rate
            if (elapsedTime >= spawnInterval) {
                spawnNewHuman()
                elapsedTime = 0f
            }

            val humanIterator = walkingHumans.iterator()
            while (humanIterator.hasNext()) {
                val human = humanIterator.next()

                // Let Humans handle their own physics or movement
                human.onFixedUpdate()

                // Add logic to handle human removal if needed (e.g., out of bounds)
                if (human.isOutOfBounds(surface!!.height.toFloat())) {
                    human.destroy()
                    humanIterator.remove()
                }
            }
        }
    }
}
