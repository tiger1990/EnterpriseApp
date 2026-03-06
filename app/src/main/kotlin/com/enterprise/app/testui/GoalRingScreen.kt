package com.enterprise.app.testui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random

// --- Branding & Theme ---
object TravelGoalsColors {
    val trackColor = Color(0xFF004D40)
    val glowColor = Color(0xFF26A69A).copy(alpha = 0.20f)
    val progressGradient =
        listOf(Color(0xFFFF7043), Color(0xFFFFA726), Color(0xFF26A69A), Color(0xFFFF7043))

    val MonkOrange = Color(0xFFE58A2E)
    val Sunset = Color(0xFFF2A65A)

    val tipCardBackground = MonkOrange.copy(alpha = 0.28f)

    val tipTitleColor = Color(0xFFEAF1FF)
    val tipBodyColor = Color(0xFFEAF1FF)
    val backgroundStart = Color(0xFF0B1220)
    val backgroundMid1 = Color(0xFF1B2B4D)
    val backgroundMid2 = Color(0xFF2A1B4D)
    val backgroundMid3 = Color(0xFF1C3B5A)
    val backgroundMid4 = Color(0xFF0B3D2E)
    val backgroundEnd = Color(0xFF070B12)
    val surfaceColor = Color(0xFF0D1424).copy(alpha = 0.85f)
    val textPrimary = Color(0xFFEAF1FF)
    val textSecondary = Color(0xFFB9C6E6)
    val textLabel = Color(0xFF9FB1D9)
    val textValue = Color(0xFFCFE0FF)
    val linearTrack = Color(0xFF22314F)
    val linearProgress = Color(0xFF6CE7FF)
    val metaChipBackground = Color(0xFF101B33)
}

// --- Main Screen ---
@Composable
fun GoalRingScreen(
    modifier: Modifier = Modifier,
    goalAmount: Long = 10_000_000L,
    stepAmount: Long = 100_000L
) {
    // 1. Animation state (Lambda-read only in draw phase)
    val bgShift = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        bgShift.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(7000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    var amount by remember { mutableLongStateOf(goalAmount / 4) }
    val clampedAmount = amount.coerceIn(0L, goalAmount)
    val rawProgress = (clampedAmount.toDouble() / goalAmount.toDouble()).toFloat().coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = 0.85f),
        label = "ring_progress"
    )

    val reachedGoal = rawProgress >= 1f
    var confettiKey by remember { mutableIntStateOf(0) }
    LaunchedEffect(reachedGoal) { if (reachedGoal) confettiKey++ }

    val formattedAmount = remember(clampedAmount) { formatIdr(clampedAmount) }
    val formattedGoal = remember(goalAmount) { formatIdr(goalAmount) }
    val tips = remember {
        listOf(
            Tip("Find Travel Buddies", "Connect with travelers heading to the same destination"),
            Tip("Local Monk Hosts", "Stay with verified locals sharing hidden gems"),
            Tip("Group Adventures", "Join community-planned treks and explorations"),
            Tip("Share Your Journey", "Post travel stories to inspire the monk tribe")
        )
    }

    Surface(modifier = modifier.fillMaxSize(), color = TravelGoalsColors.backgroundEnd) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind { // OPTIMIZATION: Animation read happens here, not in composition
                    val t = bgShift.value
                    drawRect(
                        Brush.linearGradient(
                            colors = listOf(
                                TravelGoalsColors.backgroundStart,
                                lerp(
                                    TravelGoalsColors.backgroundMid1,
                                    TravelGoalsColors.backgroundMid2,
                                    t
                                ),
                                lerp(
                                    TravelGoalsColors.backgroundMid3,
                                    TravelGoalsColors.backgroundMid4,
                                    1f - t
                                ),
                                TravelGoalsColors.backgroundEnd
                            ),
                            start = Offset.Zero,
                            end = Offset(size.width * (0.3f + t), size.height * (0.7f - t))
                        )
                    )
                }
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                HeaderBlock("Travel Goal", "Drag the handle. High performance logic.")

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = TravelGoalsColors.surfaceColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AmountRow("Saved", formattedAmount, "Goal", formattedGoal)

                        GoalRing(
                            progress = animatedProgress,
                            onProgressChange = { newProgress ->
                                val snapped = snapToStep(
                                    (newProgress * goalAmount).toLong(),
                                    stepAmount
                                ).coerceIn(0L, goalAmount)
                                if (amount != snapped) amount = snapped
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                        )

                        LinearMeta(rawProgress, clampedAmount, goalAmount)
                    }
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(tips, key = { it.title }) { TipCard(it) }
                }

                Spacer(Modifier.weight(1f))
                FooterCTA(
                    onRandomize = {
                        val new = (goalAmount * Random.nextDouble(0.05, 1.0)).toLong()
                        amount = snapToStep(new, stepAmount).coerceIn(0L, goalAmount)
                    },
                    onReset = { amount = 0L }
                )
            }
            ConfettiOverlay(confettiKey, reachedGoal, Modifier.fillMaxSize())
        }
    }
}

// --- Optimized Components ---

@Composable
private fun GoalRing(
    progress: Float,
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentOnProgressChange by rememberUpdatedState(onProgressChange)
    val ringThickness = 18.dp
    val handleRadius = 10.dp
    var lastProgress by remember { mutableFloatStateOf(progress) }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { lastProgress = progress },
                        onDrag = { change, _ ->
                            change.consume()
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val p = progressFromPosition(center, change.position)
                            val unwrapped = unwrapProgress(p, lastProgress).coerceIn(0f, 1f)
                            lastProgress = unwrapped
                            currentOnProgressChange(unwrapped)
                        }
                    )
                }
                .drawWithCache {
                    // Manual center calculation because 'center' is not in CacheDrawScope
                    val calculatedCenter = Offset(size.width / 2f, size.height / 2f)
                    val radius = min(size.width, size.height) * 0.34f
                    val ringRect = Rect(center = calculatedCenter, radius = radius)

                    val progBrush = Brush.sweepGradient(
                        TravelGoalsColors.progressGradient,
                        center = calculatedCenter
                    )
                    val glowBrush = Brush.radialGradient(
                        colors = listOf(TravelGoalsColors.glowColor, Color.Transparent),
                        center = calculatedCenter,
                        radius = radius * 1.7f
                    )
                    val handleFillBrush = Brush.radialGradient(
                        colors = listOf(
                            TravelGoalsColors.textPrimary,
                            TravelGoalsColors.tipBodyColor
                        ),
                        radius = handleRadius.toPx() * 1.8f
                    )
                    val strokePx = ringThickness.toPx()

                    onDrawWithContent {
                        val stroke = Stroke(width = strokePx, cap = StrokeCap.Round)

                        // 1. Glow
                        drawCircle(glowBrush, radius * 1.2f, calculatedCenter)

                        // 2. Track
                        drawArc(
                            TravelGoalsColors.trackColor,
                            0f,
                            360f,
                            false,
                            ringRect.topLeft,
                            ringRect.size,
                            style = stroke
                        )

                        // 3. Progress
                        val sweep = progress * 360f
                        drawArc(
                            progBrush,
                            -90f,
                            sweep,
                            false,
                            ringRect.topLeft,
                            ringRect.size,
                            style = stroke
                        )

                        // 4. Handle Position via intact math logic
                        val angleRad = Math.toRadians(angleFromProgress(progress).toDouble())
                        val hCenter = Offset(
                            calculatedCenter.x + cos(angleRad).toFloat() * radius,
                            calculatedCenter.y + sin(angleRad).toFloat() * radius
                        )

                        // 5. Handle Visuals
                        drawCircle(
                            Color.Black.copy(0.3f),
                            handleRadius.toPx() * 1.5f,
                            hCenter + Offset(0f, 4f)
                        )
                        drawCircle(handleFillBrush, handleRadius.toPx() * 1.3f, hCenter)
                        drawCircle(
                            TravelGoalsColors.backgroundStart,
                            handleRadius.toPx() * 1.3f,
                            hCenter,
                            style = Stroke(2.dp.toPx())
                        )
                    }
                }
        )
        // Static labels - Small recomposition scope
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.displaySmall,
                color = TravelGoalsColors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Drag to adjust",
                style = MaterialTheme.typography.labelLarge,
                color = TravelGoalsColors.textSecondary
            )
        }
    }
}

// ---------------- Reusable Functions (kept as-is) ----------------
fun angleFromProgress(p: Float): Float = -90f + (p.coerceIn(0f, 1f) * 360f)
fun progressFromPosition(center: Offset, pos: Offset): Float {
    val dx = pos.x - center.x
    val dy = pos.y - center.y
    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    if (angle < 0f) angle += 360f
    return ((angle + 90f) % 360f / 360f).coerceIn(0f, 1f)
}

fun unwrapProgress(current: Float, previous: Float): Float {
    val c0 = current
    val c1 = current + 1f
    val c2 = current - 1f
    fun dist(a: Float, b: Float) = abs(a - b)
    return when {
        dist(c1, previous) < dist(c0, previous) && dist(c1, previous) < dist(c2, previous) -> c1
        dist(c2, previous) < dist(c0, previous) -> c2
        else -> c0
    }
}

@Composable
private fun HeaderBlock(t: String, s: String) = Column {
    Text(
        t,
        style = MaterialTheme.typography.headlineSmall,
        color = TravelGoalsColors.textPrimary,
        fontWeight = FontWeight.Bold
    )
    Text(s, style = MaterialTheme.typography.bodyMedium, color = TravelGoalsColors.textSecondary)
}

@Composable
private fun AmountRow(ll: String, lv: String, rl: String, rv: String) =
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(ll, color = TravelGoalsColors.textLabel); Text(
            lv,
            style = MaterialTheme.typography.titleLarge,
            color = TravelGoalsColors.textPrimary
        )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                rl,
                color = TravelGoalsColors.textLabel
            ); Text(
            rv,
            style = MaterialTheme.typography.titleMedium,
            color = TravelGoalsColors.textValue
        )
        }
    }

@Composable
private fun LinearMeta(p: Float, a: Long, g: Long) =
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LinearProgressIndicator(
            progress = { p },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(TravelGoalsColors.linearTrack, RoundedCornerShape(4.dp)),
            color = TravelGoalsColors.linearProgress
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MetaChip("Remaining", formatIdr(max(0, g - a)))
            MetaChip("Status", if (p >= 1f) "Goal Reached!" else "On Track")
        }
    }

@Composable
private fun MetaChip(l: String, v: String) =
    Surface(shape = RoundedCornerShape(16.dp), color = TravelGoalsColors.metaChipBackground) {
        Row(Modifier.padding(8.dp)) {
            Text(
                l,
                color = TravelGoalsColors.textLabel
            ); Spacer(Modifier.width(8.dp)); Text(v, color = TravelGoalsColors.textPrimary)
        }
    }

@Composable
private fun TipCard(tip: Tip) = Card(
    Modifier.width(200.dp),
    colors = CardDefaults.cardColors(TravelGoalsColors.tipCardBackground),
    shape = RoundedCornerShape(16.dp)
) {
    Column(Modifier.padding(12.dp)) {
        Text(
            tip.title,
            fontWeight = FontWeight.Bold,
            color = TravelGoalsColors.tipTitleColor
        ); Text(tip.body, color = TravelGoalsColors.tipBodyColor, fontSize = 12.sp)
    }
}

@Composable
private fun FooterCTA(onRandomize: () -> Unit, onReset: () -> Unit) =
    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
        Button(onRandomize, Modifier.weight(1f)) { Text("Randomize") }
        OutlinedButton(onReset, Modifier.weight(1f)) { Text("Reset") }
    }

@Composable
private fun ConfettiOverlay(key: Int, enabled: Boolean, modifier: Modifier) {
    if (!enabled || key == 0) return
    val anim = remember(key) { Animatable(0f) }
    val particles = remember(key) {
        List(80) {
            Particle(
                Random.nextFloat(),
                Random.nextFloat() * -0.2f,
                (Random.nextFloat() - 0.5f) * 0.8f,
                Random.nextFloat() * 1.2f,
                TravelGoalsColors.progressGradient.random()
            )
        }
    }
    LaunchedEffect(key) { anim.animateTo(1f, tween(1500, easing = LinearEasing)) }
    Canvas(modifier) {
        particles.forEach { p ->
            val time = anim.value
            val x = (p.sx + p.vx * time) * size.width
            val y = (p.sy + p.vy * time + 0.5f * time * time) * size.height
            drawCircle(p.color.copy(alpha = 1f - time), 8f, Offset(x, y))
        }
    }
}

// --- Utils ---
private data class Tip(val title: String, val body: String)
private data class Particle(
    val sx: Float,
    val sy: Float,
    val vx: Float,
    val vy: Float,
    val color: Color
)

private fun snapToStep(v: Long, s: Long) = if (s <= 0) v else ((v + s / 2) / s) * s
private fun formatIdr(v: Long) = "Rp ${
    if (v >= 1e6) {
        String.format(Locale.US, "%.1fM", v / 1e6)
    } else if (v >= 1e3) {
        String.format(
            Locale.US,
            "%.1fK",
            v / 1e3
        )
    } else v.toString()
}"

@Preview
@Composable
fun PreviewGoal() = MaterialTheme { GoalRingScreen() }
