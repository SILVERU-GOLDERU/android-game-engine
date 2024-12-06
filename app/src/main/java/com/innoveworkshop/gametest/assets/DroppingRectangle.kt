package com.innoveworkshop.gametest.assets

import com.innoveworkshop.gametest.engine.Rectangle
import com.innoveworkshop.gametest.engine.Vector

class DroppingRectangle(
    position: Vector?,
    width: Float,
    height: Float,
    val mass: Float,
    color: Int
) : Rectangle(position, width, height, color) {
    private val gravity = 9.8f
    private var velocity = Vector(0f, 0f)
    private val worldScale = 10f


    override fun onFixedUpdate() {
        super.onFixedUpdate()

        if (!isFloored) {
            val gravityForce = gravity * mass // F = g * m
            velocity.y += gravityForce / mass // Acceleration: a = F / m = g
            position.y += velocity.y / worldScale  // Update position
        }
    }
    fun isOutOfBounds(surfaceHeight: Float): Boolean {
        return position.y > surfaceHeight - height
    }
}
