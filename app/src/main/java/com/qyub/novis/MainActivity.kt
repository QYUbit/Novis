package com.qyub.novis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Immersive Fullscreen Mode aktivieren
        setupImmersiveMode()

        setContent {
            PatternLockGameTheme {
                PatternLockGameScreen()
            }
        }
    }

    private fun setupImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())

            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupImmersiveMode()
        }
    }
}

@Composable
fun PatternLockGameTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF2196F3),
            secondary = Color(0xFF03DAC5),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E)
        ),
        content = content
    )
}

data class PatternPoint(val row: Int, val col: Int, val id: Int)

enum class GameState {
    WAITING,
    SHOWING_PATTERN,
    INPUT_MODE,
    CORRECT,
    WRONG,
    GAME_OVER
}

@Composable
fun PatternLockGameScreen() {
    var gameState by remember { mutableStateOf(GameState.WAITING) }
    var targetPattern by remember { mutableStateOf<List<PatternPoint>>(emptyList()) }
    var currentPattern by remember { mutableStateOf<List<PatternPoint>>(emptyList()) }
    var timeLeft by remember { mutableFloatStateOf(10f) }
    var score by remember { mutableIntStateOf(0) }
    var level by remember { mutableIntStateOf(1) }
    var showingPatternTime by remember { mutableFloatStateOf(0f) }

    // Pattern grid (3x3)
    val patternGrid = remember {
        (0..2).flatMap { row ->
            (0..2).map { col ->
                PatternPoint(row, col, row * 3 + col)
            }
        }
    }

    // Generate random pattern
    fun generateRandomPattern(): List<PatternPoint> {
        val minLength = minOf(3, 2 + level / 3)
        val maxLength = minOf(6, 3 + level / 2)
        val patternLength = Random.nextInt(minLength, maxLength + 1)
        return patternGrid.shuffled().take(patternLength)
    }

    // Start new round
    fun startNewRound() {
        targetPattern = generateRandomPattern()
        currentPattern = emptyList()
        gameState = GameState.SHOWING_PATTERN
        showingPatternTime = 2f + level * 0.5f
        timeLeft = maxOf(5f, 15f - level * 0.5f)
    }

    // Check if patterns match
    fun checkPattern(): Boolean {
        return targetPattern.map { it.id } == currentPattern.map { it.id }
    }

    // Game timer effect
    LaunchedEffect(gameState, timeLeft, showingPatternTime) {
        when (gameState) {
            GameState.SHOWING_PATTERN -> {
                while (showingPatternTime > 0 && gameState == GameState.SHOWING_PATTERN) {
                    delay(100)
                    showingPatternTime -= 0.1f
                }
                if (gameState == GameState.SHOWING_PATTERN) {
                    gameState = GameState.INPUT_MODE
                }
            }
            GameState.INPUT_MODE -> {
                while (timeLeft > 0 && gameState == GameState.INPUT_MODE) {
                    delay(100)
                    timeLeft -= 0.1f
                }
                if (gameState == GameState.INPUT_MODE) {
                    gameState = GameState.GAME_OVER
                }
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pattern Lock Challenge",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("Level: $level", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text("Score: $score", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    if (gameState == GameState.INPUT_MODE) {
                        Text("Zeit: ${timeLeft.toInt()}s", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status message
        when (gameState) {
            GameState.WAITING -> {
                Text(
                    "Bereit für Level $level?",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
            GameState.SHOWING_PATTERN -> {
                Text(
                    "Merke dir das Muster! ${showingPatternTime.toInt() + 1}s",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(16.dp)
                )
            }
            GameState.INPUT_MODE -> {
                Text(
                    "Zeichne das Muster nach!",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
            }
            GameState.CORRECT -> {
                Text(
                    "Richtig! +${level * 10} Punkte",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
            GameState.WRONG -> {
                Text(
                    "Falsch! Das war das richtige Muster:",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFF44336),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
            GameState.GAME_OVER -> {
                Text(
                    "Zeit abgelaufen!",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFF44336),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pattern display
        PatternView(
            modifier = Modifier.size(300.dp),
            patternGrid = patternGrid,
            targetPattern = if (gameState == GameState.SHOWING_PATTERN || gameState == GameState.WRONG) targetPattern else emptyList(),
            currentPattern = if (gameState == GameState.INPUT_MODE) currentPattern else emptyList(),
            isInputEnabled = gameState == GameState.INPUT_MODE,
            onPatternChange = { newPattern ->
                if (gameState == GameState.INPUT_MODE) {
                    currentPattern = newPattern
                    if (newPattern.size >= targetPattern.size) {
                        // Warte einen Moment bevor die Überprüfung erfolgt
                        kotlinx.coroutines.GlobalScope.launch {
                            delay(300)
                            if (checkPattern()) {
                                score += level * 10
                                level++
                                gameState = GameState.CORRECT
                            } else {
                                gameState = GameState.WRONG
                            }
                        }
                    }
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Control buttons
        when (gameState) {
            GameState.WAITING -> {
                Button(
                    onClick = { startNewRound() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Start Level $level", fontSize = 18.sp)
                }
            }
            GameState.CORRECT -> {
                LaunchedEffect(Unit) {
                    delay(1500)
                    gameState = GameState.WAITING
                }
            }
            GameState.WRONG, GameState.GAME_OVER -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { startNewRound() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Nochmal versuchen", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            score = 0
                            level = 1
                            gameState = GameState.WAITING
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Neues Spiel", fontSize = 18.sp)
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun PatternView(
    modifier: Modifier = Modifier,
    patternGrid: List<PatternPoint>,
    targetPattern: List<PatternPoint>,
    currentPattern: List<PatternPoint>,
    isInputEnabled: Boolean,
    onPatternChange: (List<PatternPoint>) -> Unit
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }



    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(isInputEnabled) {
                if (isInputEnabled) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            dragPosition = offset

                            // Check if starting on a point
                            val cellSize = size.width / 3f
                            val row = (offset.y / cellSize).toInt()
                            val col = (offset.x / cellSize).toInt()

                            if (row in 0..2 && col in 0..2) {
                                val point = patternGrid.find { it.row == row && it.col == col }
                                point?.let {
                                    if (!currentPattern.contains(it)) {
                                        onPatternChange(currentPattern + it)
                                    }
                                }
                            }
                        },
                        onDragEnd = {
                            isDragging = false
                        },
                        onDrag = { change, _ ->
                            dragPosition += change.positionChange()

                            // Check if dragging over a new point
                            val cellSize = size.width / 3f
                            val currentPos = dragPosition
                            val row = (currentPos.y / cellSize).toInt()
                            val col = (currentPos.x / cellSize).toInt()

                            if (row in 0..2 && col in 0..2) {
                                val point = patternGrid.find { it.row == row && it.col == col }
                                point?.let {
                                    if (!currentPattern.contains(it)) {
                                        onPatternChange(currentPattern + it)
                                    }
                                }
                            }
                        }
                    )
                }
            }
    ) {
        val cellSize = size.width / 3f
        val pointRadius = cellSize * 0.15f
        val lineWidth = 8.dp.toPx()

        // Draw connections for target pattern
        if (targetPattern.isNotEmpty()) {
            for (i in 0 until targetPattern.size - 1) {
                val start = getPointPosition(targetPattern[i], cellSize)
                val end = getPointPosition(targetPattern[i + 1], cellSize)

                drawLine(
                    color = Color(0xFF2196F3),
                    start = start,
                    end = end,
                    strokeWidth = lineWidth,
                    cap = StrokeCap.Round
                )
            }
        }

        // Draw connections for current pattern
        if (currentPattern.isNotEmpty()) {
            for (i in 0 until currentPattern.size - 1) {
                val start = getPointPosition(currentPattern[i], cellSize)
                val end = getPointPosition(currentPattern[i + 1], cellSize)

                drawLine(
                    color = Color(0xFF03DAC5),
                    start = start,
                    end = end,
                    strokeWidth = lineWidth,
                    cap = StrokeCap.Round
                )
            }
        }

        // Draw grid points
        patternGrid.forEach { point ->
            val position = getPointPosition(point, cellSize)
            val isInTarget = targetPattern.contains(point)
            val isInCurrent = currentPattern.contains(point)

            val color = when {
                isInTarget && isInCurrent -> Color(0xFF4CAF50)
                isInTarget -> Color(0xFF2196F3)
                isInCurrent -> Color(0xFF03DAC5)
                else -> Color(0xFF666666)
            }

            val radius = if (isInTarget || isInCurrent) pointRadius * 1.2f else pointRadius

            drawCircle(
                color = color,
                radius = radius,
                center = position
            )
        }
    }
}

private fun getPointPosition(point: PatternPoint, cellSize: Float): Offset {
    return Offset(
        x = point.col * cellSize + cellSize / 2f,
        y = point.row * cellSize + cellSize / 2f
    )
}