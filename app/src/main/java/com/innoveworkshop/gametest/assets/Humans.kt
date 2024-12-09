package com.innoveworkshop.gametest.assets

import com.innoveworkshop.gametest.MainActivity
import com.innoveworkshop.gametest.engine.Rectangle
import com.innoveworkshop.gametest.engine.Vector

class Humans(
    position: Vector,
    width: Float,
    height: Float,
    val mass: Float,
    color: Int,
    private val mainActivity: MainActivity
) : Rectangle(position, width, height, color) {

    private var speed = 5f // Default walking speed

    init {
        // Set movement direction based on spawn position
        if (position.x > 0f) {
            speed = -5f // Move left if spawned on the right
        }
    }

    fun onCollision(rectangle: DroppingRectangle) {
        println("Human collided with DroppingRectangle")
        this.destroy() // remove the human
    }


    override fun onFixedUpdate() {
        if (mainActivity.isPaused) return // Skip updates if paused
        super.onFixedUpdate()

        // Move the human
        position.x += speed
    }

    // Check if the human has moved off the screen
    fun isOutOfBounds(surfaceWidth: Float): Boolean {
        return position.x + width < 0 || position.x > surfaceWidth
    }
}
