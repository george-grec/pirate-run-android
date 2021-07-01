package com.example.piraterun

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.IOException

class AnimationsRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                changeAnimationStatus(enable = false)
                try {
                    base.evaluate()
                } finally {
                    changeAnimationStatus(enable = true)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun changeAnimationStatus(enable:Boolean = true) {
        var e = 1
        if (!enable) e = 0
        with(UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())){
            executeShellCommand(" $TRANSITION_ANIMATION_SCALE $e")
            executeShellCommand("$WINDOW_ANIMATION_SCALE $e")
            executeShellCommand("$ANIMATOR_DURATION $e")
        }
    }

    companion object {
        private const val TRANSITION_ANIMATION_SCALE = "settings put global transition_animation_scale"
        private const val WINDOW_ANIMATION_SCALE = "settings put global window_animation_sc"
        private const val ANIMATOR_DURATION = "settings put global animator_duration_scale"
    }
}