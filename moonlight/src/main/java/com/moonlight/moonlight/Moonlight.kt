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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

data class MoonlightStep(
    val ids: List<String>,
    val title: String = "Walkthrough",
    val text: String,
) {
    constructor(id: String, title: String = "Walkthrough", text: String) : this(
        listOf(id),
        title,
        text
    )
}

@Composable
fun Moonlight(
    modifier: Modifier = Modifier,
    state: MoonlightState,
    steps: List<MoonlightStep>,
    colors: MoonlightColors = MoonlightDefaults.colors(),
    typography: MoonlightTypography = MoonlightDefaults.typography(),
    skipText: String = "Skip",
    endText: String = "Finish",
    absorbClicks: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Main App Content
        content()

        // Overlay and Walkthrough
        if (state.isVisible && steps.isNotEmpty()) {
            val currentStep = steps.getOrNull(state.currentStepIndex)
            val currentTargetBounds = currentStep?.let { step ->
                step.ids.mapNotNull { id -> state.targetBounds[id] }
            } ?: emptyList()

            MoonlightOverlay(
                boundsList = currentTargetBounds,
                overlayColor = colors.overlayColor,
                absorbInput = absorbClicks,
                onDismiss = { state.dismiss() }
            )

            if (currentStep != null && currentTargetBounds.isNotEmpty()) {
                // Determine if we should show the card at the top or bottom
                // Calculate the combined bounding box of all targets
                val combinedBounds = Rect(
                    left = currentTargetBounds.minOf { it.left },
                    top = currentTargetBounds.minOf { it.top },
                    right = currentTargetBounds.maxOf { it.right },
                    bottom = currentTargetBounds.maxOf { it.bottom }
                )

                // containerSize.height is already in pixels
                val screenHeightPx = LocalWindowInfo.current.containerSize.height.toFloat()

                // Determine best position for the card based on available space
                // If targets are in the bottom half, show card at top, otherwise show at bottom
                val spaceAbove = combinedBounds.top
                val spaceBelow = screenHeightPx - combinedBounds.bottom
                val isTargetBottom = spaceBelow < spaceAbove

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
                        colors = colors,
                        typography = typography,
                        skipText = skipText,
                        endText = endText
                    )
                }
            }
        }
    }
}

@Composable
private fun MoonlightOverlay(
    boundsList: List<Rect>,
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

            for (bounds in boundsList) {
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
    colors: MoonlightColors,
    typography: MoonlightTypography,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onSkip: () -> Unit,
    skipText: String,
    endText: String,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.containerColor
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
                    style = typography.titleStyle,
                    color = colors.titleContentColor,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onSkip) {
                    Text(skipText, color = colors.actionButtonContainerColor)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = step.text,
                style = typography.textStyle,
                color = colors.textContentColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Footer with arrows
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (stepIndex > 0) {
                    Button(
                        onClick = onPrev,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.actionButtonContainerColor,
                            contentColor = colors.actionButtonContentColor
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                } else {
                    Spacer(modifier = Modifier.width(ButtonDefaults.MinWidth)) // Placeholder to keep spacing
                }

                Spacer(modifier = Modifier.weight(1f))

                // Indicators 1/3
                Text(
                    text = "${stepIndex + 1} / $totalSteps",
                    style = typography.indicatorStyle,
                    color = colors.indicatorColor
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.actionButtonContainerColor,
                        contentColor = colors.actionButtonContentColor
                    )
                ) {
                    if (stepIndex == totalSteps - 1) {
                        Text(endText)
                    } else {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                    }
                }
            }
        }
    }
}
