package com.moonlight.myapplication.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moonlight.moonlight.Moonlight
import com.moonlight.moonlight.MoonlightStep
import com.moonlight.moonlight.moonlightTarget
import com.moonlight.moonlight.rememberMoonlightState

@Composable
fun MoonlightShowcase() {
    val moonlightState = rememberMoonlightState(initialDecorated = false)

    val steps = listOf(
        MoonlightStep(
            id = "title",
            title = "Welcome to Moonlight",
            text = "This library helps you create beautiful walkthroughs for your Jetpack Compose apps."
        ),
        MoonlightStep(
            id = "box",
            title = "Highlight Any Component",
            text = "You can highlight Boxes, Rows, Columns, or any custom component easily."
        ),
        MoonlightStep(
            id = "start_button",
            title = "User Interaction",
            text = "Guide users to actionable buttons to drive engagement."
        )
    )

    Moonlight(
        state = moonlightState,
        steps = steps,
        absorbClicks = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Moonlight Library Showcase",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.moonlightTarget(moonlightState, "title")
            )

            Spacer(modifier = Modifier.height(64.dp))

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium)
                    .moonlightTarget(moonlightState, "box"),
                contentAlignment = Alignment.Center
            ) {
                Text("Feature Area", color = MaterialTheme.colorScheme.onPrimaryContainer)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { moonlightState.isVisible = true },
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .moonlightTarget(moonlightState, "start_button")
            ) {
                Text("Start Tutorial again")
            }
        }
    }
}
