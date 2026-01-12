package com.moonlight.moonlight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.toSize

class MoonlightState(initialDecorated: Boolean = false) {
    var isVisible by mutableStateOf(initialDecorated)
    var currentStepIndex by mutableIntStateOf(0)

    // Stores the bounds of registered targets relative to the root
    internal val targetBounds = mutableStateMapOf<String, Rect>()

    fun nextStep(maxSteps: Int) {
        if (currentStepIndex < maxSteps - 1) {
            currentStepIndex++
        } else {
            dismiss()
        }
    }

    fun previousStep() {
        if (currentStepIndex > 0) {
            currentStepIndex--
        }
    }

    fun dismiss() {
        isVisible = false
        currentStepIndex = 0
    }

    fun skip() {
        dismiss()
    }
}

@Composable
fun rememberMoonlightState(initialDecorated: Boolean = false): MoonlightState {
    return remember { MoonlightState(initialDecorated) }
}

fun Modifier.moonlightTarget(
    state: MoonlightState,
    id: String
): Modifier = this.onGloballyPositioned { coordinates ->
    if (state.isVisible) {
        val position = coordinates.positionInRoot()
        val size = coordinates.size.toSize()
        state.targetBounds[id] = Rect(position, size)
    }
}

