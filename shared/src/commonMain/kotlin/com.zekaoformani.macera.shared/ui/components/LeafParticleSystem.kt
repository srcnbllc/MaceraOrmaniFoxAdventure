package com.zekaoformani.macera.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    var speed: Float,
    var radius: Float,
    var alpha: Float,
    var angle: Float,
    var swaySpeed: Float
)

@Composable
fun LeafParticleSystem(modifier: Modifier = Modifier) {
    val particles = remember {
        List(20) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = Random.nextFloat() * 2f + 1f,
                radius = Random.nextFloat() * 6f + 3f,
                alpha = Random.nextFloat() * 0.4f + 0.1f,
                angle = Random.nextFloat() * 2 * PI.toFloat(),
                swaySpeed = Random.nextFloat() * 0.05f + 0.01f
            )
        }
    }

    var ticks by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(16) // ~60fps
            ticks++
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        if (ticks > 0) {
            particles.forEach { p ->
                p.y += p.speed
                p.angle += p.swaySpeed
                p.x += sin(p.angle) * 1f

                if (p.y > height + p.radius) {
                    p.y = -p.radius
                    p.x = Random.nextFloat() * width
                }
                
                val isAmber = p.radius > 6f
                val baseColor = if (isAmber) Color(0xFFFFCC80) else Color(0xFFA5D6A7)
                
                drawCircle(
                    color = baseColor.copy(alpha = p.alpha),
                    radius = p.radius,
                    center = Offset(p.x, p.y)
                )
            }
        }
    }
}
