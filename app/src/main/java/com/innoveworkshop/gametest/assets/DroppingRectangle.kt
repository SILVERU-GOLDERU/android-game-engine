package com.innoveworkshop.gametest.assets

import com.innoveworkshop.gametest.MainActivity
import com.innoveworkshop.gametest.engine.Rectangle
import com.innoveworkshop.gametest.engine.Vector

class DroppingRectangle(
    position: Vector?,
    width: Float,
    height: Float,
    val mass: Float,
    color: Int,
    private val mainActivity: MainActivity
) : Rectangle(position, width, height, color) {
    private val gravity = 9.8f
    private var acceleration = Vector(0f, 0f)
    private val worldScale = 50f


    fun collidesWith(human: Humans): Boolean {
        return position.x < human.position.x + human.width &&
                position.x + width > human.position.x &&
                position.y < human.position.y + human.height &&
                position.y + height > human.position.y
    }

    fun collidesWith(homeless: Homeless): Boolean {
        return position.x < homeless.position.x + homeless.width &&
                position.x + width > homeless.position.x &&
                position.y < homeless.position.y + homeless.height &&
                position.y + height > homeless.position.y
    }

    fun onCollision(human: Humans) {
        println("DroppingRectangle collided with Human")
        this.destroy()
    }

    fun onCollision(homeless: Homeless) {
        println("DroppingRectangle collided with Homeless")
        this.destroy()
    }

    override fun onFixedUpdate() {
        if (mainActivity.isPaused) return
        super.onFixedUpdate()

        if (!isFloored) {
            val gravityForce = gravity * mass // F = g * m
            acceleration.y += gravityForce / mass // a = F/m
            position.y += acceleration.y / worldScale
        }
    }
    fun isOutOfBounds(surfaceHeight: Float): Boolean {
        return position.y > surfaceHeight - height
    }
}
