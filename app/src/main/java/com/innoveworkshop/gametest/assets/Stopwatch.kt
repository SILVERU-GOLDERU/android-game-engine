package com.innoveworkshop.gametest.assets

class Stopwatch {
    private var startTime: Long = 0L // Tracks when the stopwatch started
    private var elapsedTime: Long = 0L // Tracks total elapsed time
    var isRunning: Boolean = false // Tracks whether the stopwatch is running

    fun start() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime
            isRunning = true
        }
    }

    fun pause() {
        if (isRunning) {
            elapsedTime = System.currentTimeMillis() - startTime
            isRunning = false
        }
    }

    fun reset() {
        startTime = 0L
        elapsedTime = 0L
        isRunning = false
    }

    fun getFormattedTime(): String {
        val totalSeconds = (if (isRunning) System.currentTimeMillis() - startTime else elapsedTime) / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}