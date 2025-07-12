package com.qyub.novis.components.games

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun Counter() {
    var count by remember { mutableIntStateOf(0) }

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {}
            .clickable(
                onClick = { count++ },
                //interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(count.toString(), style = MaterialTheme.typography.displayLarge, color = Color.White)
        }
    }
}