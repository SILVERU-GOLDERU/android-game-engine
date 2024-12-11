package com.innoveworkshop.gametest.engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.innoveworkshop.gametest.MainActivity
import com.innoveworkshop.gametest.assets.Stopwatch
import com.innoveworkshop.gametest.assets.DroppingRectangle
import com.innoveworkshop.gametest.assets.Humans
import com.innoveworkshop.gametest.assets.Homeless
import java.util.Timer
import java.util.TimerTask

class GameSurface @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr) {
    private val holder: SurfaceHolder
    private var timer: Timer? = null
    private var root: GameObject? = null

    // Create the GameObject list.
    private val gameObjects = ArrayList<GameObject>()

    private val stopwatch = Stopwatch()
    private val paint = Paint().apply {
        textSize = 50f
        isAntiAlias = true
        color = Color.WHITE
    }
    private var mainActivity: MainActivity? = null

    private var destroyedHumansCount = 0
    private var destroyedHomelessCount = 0

    fun initializeWithMainActivity(activity: MainActivity) {
        this.mainActivity = activity
    }

    init {
        // Ensure we are on top of everything.
        setZOrderOnTop(true)

        // Set up the SurfaceHolder event handler.
        holder = getHolder()
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                // Ensure we get the onDraw events.
                setWillNotDraw(false)

                // Start up the root object.
                root!!.onStart(this@GameSurface)


                if (mainActivity?.isPaused == false) {
                    stopwatch.start()
                }

                // Set up the fixed update timer.
                timer = Timer()
                timer!!.scheduleAtFixedRate(FixedUpdateTimer(), 0, (1000 / 30).toLong())
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                stopwatch.pause()
                timer?.cancel()
                timer = null
            }
        })
    }

    fun setRootGameObject(root: GameObject?) {
        this.root = root
    }

    fun addGameObject(gameObject: GameObject) {
        gameObjects.add(gameObject)
        gameObject.onStart(this)
    }

    fun removeGameObject(gameObject: GameObject): Boolean {
        return gameObjects.remove(gameObject)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Color.parseColor("#8bac0f"))

        root!!.onDraw(canvas)
        for (gameObject in gameObjects) {
            gameObject.onDraw(canvas)
        }

        // Draw stopwatch time
        val timeText = stopwatch.getFormattedTime()
        val textWidth = paint.measureText(timeText) // Measure the width of the text
        val x = (width - textWidth) / 4f
        val y = height / 15f
        canvas.drawText(timeText, x, y, paint)

        val humandestroyedText = "Humans Bricked: $destroyedHumansCount"
        val humandestroyedTextWidth = paint.measureText(humandestroyedText)
        canvas.drawText(humandestroyedText, width - humandestroyedTextWidth - 20f, height / 15f, paint)

        val homelessdestroyedText = "Homeless Bricked: $destroyedHomelessCount"
        val homelessdestroyedTextWidth = paint.measureText(homelessdestroyedText)
        canvas.drawText(homelessdestroyedText, width - homelessdestroyedTextWidth - 20f, height / 10f, paint)
    }

    fun incrementDestroyedHumans() {
        destroyedHumansCount++
    }
    fun incrementDestroyedHomeless() {
        destroyedHomelessCount++
    }

    fun startStopwatch() {
        stopwatch.start()
    }

    fun pauseStopwatch() {
        stopwatch.pause()
    }

    fun resetStopwatch() {
        stopwatch.reset()
    }


    internal inner class FixedUpdateTimer : TimerTask() {
        override fun run() {
            for (gameObject in gameObjects) {
                gameObject.onFixedUpdate()
            }
            checkCollisions()  ///


            root!!.onFixedUpdate()
            invalidate()
        }
    }


    private fun checkCollisions() {
        val droppingRectangles = gameObjects.filterIsInstance<DroppingRectangle>()
        val humans = gameObjects.filterIsInstance<Humans>()

        for (rectangle in droppingRectangles) {
            for (human in humans) {
                if (rectangle.collidesWith(human)) {
                    rectangle.onCollision(human)
                    human.onCollision(rectangle)

                    incrementDestroyedHumans()
                }
            }
        }

        val homeless = gameObjects.filterIsInstance<Homeless>()

        for (rectangle in droppingRectangles) {
            for (homeless in homeless) {
                if (rectangle.collidesWith(homeless)) {
                    rectangle.onCollision(homeless)
                    homeless.onCollision(rectangle)

                    incrementDestroyedHomeless()
                }
            }
        }
    }
}
