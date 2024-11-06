package com.acapp1412.scotiabanktakehome

import android.view.View
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/***
 * start fade in animation as shown in demo
 * the target view will first be set with 0 alpha some Y offset, then set back to the original
 * visibility and Y offset with specified duration
 */

fun View.fadeIn(duration: Duration = DefaultFadeInDuration) {
    alpha = 0f
    translationY = 50f
    animate()
        .alpha(1f)
        .translationY(0f)
        .setDuration(duration.inWholeMilliseconds)
        .start()
}

val DefaultFadeInDuration = 0.6.seconds