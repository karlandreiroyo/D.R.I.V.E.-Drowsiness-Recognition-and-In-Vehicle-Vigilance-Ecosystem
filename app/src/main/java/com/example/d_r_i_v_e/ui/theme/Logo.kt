package com.example.d_r_i_v_e.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun DriveLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        // Colors from the image
        val darkBlue = Color(0xFF1B365D)
        val teal = Color(0xFF24828D)
        
        // 1. Draw Shield Outline
        val shieldPath = Path().apply {
            moveTo(width * 0.1f, height * 0.15f)
            quadraticTo(width * 0.5f, height * 0.05f, width * 0.9f, height * 0.15f)
            lineTo(width * 0.9f, height * 0.5f)
            quadraticTo(width * 0.9f, height * 0.85f, width * 0.5f, height * 0.95f)
            quadraticTo(width * 0.1f, height * 0.85f, width * 0.1f, height * 0.5f)
            close()
        }
        drawPath(shieldPath, darkBlue, style = Stroke(width * 0.06f, cap = StrokeCap.Round))

        // 2. Draw Road (Teal)
        val roadPath = Path().apply {
            moveTo(width * 0.15f, height * 0.75f)
            quadraticTo(width * 0.4f, height * 0.55f, width * 0.6f, height * 0.85f)
            lineTo(width * 0.45f, height * 0.92f)
            quadraticTo(width * 0.35f, height * 0.7f, width * 0.15f, height * 0.82f)
            close()
        }
        drawPath(roadPath, teal, style = Fill)

        // 3. Draw Car (Dark Blue)
        val carPath = Path().apply {
            moveTo(width * 0.25f, height * 0.45f)
            lineTo(width * 0.35f, height * 0.35f)
            lineTo(width * 0.65f, height * 0.35f)
            lineTo(width * 0.75f, height * 0.45f)
            lineTo(width * 0.78f, height * 0.55f)
            lineTo(width * 0.22f, height * 0.55f)
            close()
        }
        drawPath(carPath, darkBlue, style = Fill)
        
        // Car details (lights)
        drawCircle(Color.White, radius = width * 0.02f, center = Offset(width * 0.3f, height * 0.48f))
        drawCircle(Color.White, radius = width * 0.02f, center = Offset(width * 0.7f, height * 0.48f))

        // 4. Draw EKG / Heartbeat Line (Teal)
        val ekgPath = Path().apply {
            moveTo(width * 0.1f, height * 0.5f)
            lineTo(width * 0.45f, height * 0.5f)
            lineTo(width * 0.48f, height * 0.3f)
            lineTo(width * 0.52f, height * 0.7f)
            lineTo(width * 0.55f, height * 0.5f)
            lineTo(width * 0.75f, height * 0.5f)
        }
        drawPath(ekgPath, teal, style = Stroke(width * 0.03f, cap = StrokeCap.Round))

        // 5. Draw Warning Triangle (Teal)
        val trianglePath = Path().apply {
            moveTo(width * 0.75f, height * 0.65f)
            lineTo(width * 0.95f, height * 0.5f)
            lineTo(width * 0.95f, height * 0.8f)
            close()
        }
        drawPath(trianglePath, teal, style = Fill)
        
        // Exclamation mark
        drawRect(Color.White, topLeft = Offset(width * 0.88f, height * 0.58f), size = Size(width * 0.02f, height * 0.08f))
        drawCircle(Color.White, radius = width * 0.015f, center = Offset(width * 0.89f, height * 0.72f))
    }
}
