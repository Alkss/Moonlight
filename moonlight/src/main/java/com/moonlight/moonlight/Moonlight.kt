package com.moonlight.moonlight

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

data class MoonlightStep(
    val id: String,
    val text: String,
    val title: String = "Walkthrough"
)

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

@Composable
fun Moonlight(
    modifier: Modifier = Modifier,
    state: MoonlightState,
    steps: List<MoonlightStep>,
    overlayColor: Color = Color.Black.copy(alpha = 0.7f),
    skipText: String = "Skip",
    absorbClicks: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Main App Content
        content()

        // Overlay and Walkthrough
        if (state.isVisible && steps.isNotEmpty()) {
            val currentStep = steps.getOrNull(state.currentStepIndex)
            val currentTargetBounds = currentStep?.let { state.targetBounds[it.id] }

            MoonlightOverlay(
                bounds = currentTargetBounds,
                overlayColor = overlayColor,
                absorbInput = absorbClicks,
                onDismiss = { state.dismiss() }
            )

            if (currentStep != null && currentTargetBounds != null) {
                // Determine if we should show the card at the top or bottom
                // If the target is in the bottom half of the screen, show card at top.
                // Otherwise show at bottom.
                val screenHeight =
                    androidx.compose.ui.platform.LocalConfiguration.current.screenHeightDp.dp
                val screenHeightPx =
                    with(androidx.compose.ui.platform.LocalDensity.current) { screenHeight.toPx() }

                val isTargetBottom = currentTargetBounds.center.y > screenHeightPx / 2

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MoonlightCard(
                        modifier = Modifier
                            .align(if (isTargetBottom) Alignment.TopCenter else Alignment.BottomCenter)
                            .then(if (isTargetBottom) Modifier.statusBarsPadding() else Modifier.navigationBarsPadding()),
                        step = currentStep,
                        stepIndex = state.currentStepIndex,
                        totalSteps = steps.size,
                        onNext = { state.nextStep(steps.size) },
                        onPrev = { state.previousStep() },
                        onSkip = { state.skip() },
                        skipText = skipText
                    )
                }
            }
        }
    }
}

@Composable
private fun MoonlightOverlay(
    bounds: Rect?,
    overlayColor: Color,
    absorbInput: Boolean,
    onDismiss: () -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!absorbInput) {
                    onDismiss()
                }
            }
    ) {
        val path = Path().apply {
            fillType = PathFillType.EvenOdd
            addRect(Rect(Offset.Zero, size))

            if (bounds != null) {
                val padding = 8.dp.toPx()
                val cornerRadius = 12.dp.toPx()

                val inflatedBounds = Rect(
                    left = bounds.left - padding,
                    top = bounds.top - padding,
                    right = bounds.right + padding,
                    bottom = bounds.bottom + padding
                )

                addRoundRect(RoundRect(inflatedBounds, CornerRadius(cornerRadius)))
            }
        }

        drawPath(path, overlayColor)
    }
}

@Composable
private fun MoonlightCard(
    modifier: Modifier = Modifier,
    step: MoonlightStep,
    stepIndex: Int,
    totalSteps: Int,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onSkip: () -> Unit,
    skipText: String,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with title and Skip button (top right)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = step.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onSkip) {
                    Text(skipText)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = step.text,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Footer with arrows
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (stepIndex > 0) {
                    Button(onClick = onPrev) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                } else {
                    Spacer(modifier = Modifier.width(ButtonDefaults.MinWidth)) // Placeholder to keep spacing
                }

                Spacer(modifier = Modifier.weight(1f))

                // Indicators 1/3
                Text(
                    text = "${stepIndex + 1} / $totalSteps",
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(onClick = onNext) {
                    if (stepIndex == totalSteps - 1) {
                        Text("Finish")
                    } else {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                    }
                }
            }
        }
    }
}
