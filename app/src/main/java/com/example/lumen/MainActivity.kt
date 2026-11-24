package com.example.lumen

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LumenTheme { LumenScreen(this) }
        }
    }

    override fun onPause() {
        super.onPause()
        turnTorchOff(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        turnTorchOff(this)
    }
}

@Composable
fun LumenTheme(content: @Composable () -> Unit) {
    val ctx = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val isApi31Plus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val scheme: ColorScheme = when {
        isApi31Plus && isDark -> dynamicDarkColorScheme(ctx)
        isApi31Plus -> dynamicLightColorScheme(ctx)
        isDark -> darkColorScheme()
        else -> lightColorScheme()
    }
    MaterialTheme(colorScheme = scheme, content = content)
}

@Composable
fun LumenScreen(app: Context) {
    val maxStrengthLevel = remember(app) { readMaxStrengthLevel(app) }

    var torchOn by remember { mutableStateOf(false) }
    var strength by remember { mutableStateOf(1) }
    var sosOn by remember { mutableStateOf(false) }

    LaunchedEffect(sosOn) {
        if (sosOn) {
            val pattern = listOf(150L, 150L, 150L, 450L, 450L, 450L, 150L, 150L, 150L)
            try {
                while (true) {
                    for (onMs in pattern) {
                        turnTorchOn(app, strength)
                        delay(onMs)
                        turnTorchOff(app)
                        delay(150L)
                    }
                    delay(1000L)
                }
            } finally {
                turnTorchOff(app)
            }
        }
    }

    val torchOnColor = if (torchOn) Color(0xFFD8B755) else Color(0xFFF2F2F2)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (torchOn) "Torch: on" else "Torch: off",
            color = Color(0xFFF0F0F0),
            style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center),
            modifier = Modifier.padding(top = 64.dp)
        )

        Spacer(Modifier.height(44.dp))

        Surface(
            modifier = Modifier
                .width(300.dp)
                .height(520.dp)
                .clip(RoundedCornerShape(56.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x2E000000),
                            Color(0x1A000000)
                        )
                    )
                )
                .clickable {
                    if (sosOn) return@clickable
                    if (torchOn) {
                        turnTorchOff(app)
                        torchOn = false
                    } else {
                        turnTorchOn(app, strength)
                        torchOn = true
                    }
                },
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                FlashlightDotArc(
                    active = torchOn,
                    modifier = Modifier
                        .width(220.dp)
                        .height(120.dp)
                        .offset(y = -140.dp)
                )

                Icon(
                    imageVector = Icons.Filled.Lightbulb,
                    contentDescription = "Toggle torch",
                    tint = torchOnColor,
                    modifier = Modifier.height(130.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        if (maxStrengthLevel > 1) {
            Text(
                text = "Strength ${strength}/${maxStrengthLevel}",
                color = Color(0x80FFFFFF),
                style = TextStyle(fontSize = 13.sp),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text("SOS", color = Color(0x80FFFFFF), style = TextStyle(fontSize = 13.sp))
        Spacer(Modifier.height(4.dp))
        Switch(
            checked = sosOn,
            onCheckedChange = {
                sosOn = it
                if (it) torchOn = false
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun FlashlightDotArc(active: Boolean, modifier: Modifier = Modifier) {
    val color = if (active) Color(0xFFD8B755) else Color(0x66FFFFFF)
    val dots = remember {
        (10..31).map { i ->
            val t = i.toFloat() / 31f
            val x = t
            val y = 0.4f * Math.sin(Math.PI * t)
            Pair(x, y)
        }
    }
    Box(modifier = modifier) {
        dots.forEach { (x, y) ->
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(color)
                    .offset(x = (x * 220f).dp, y = (y * 110f).dp)
            )
        }
    }
}

private fun findFlashCameraId(ctx: Context): String? {
    val cm = ctx.getSystemService(CameraManager::class.java) ?: return null
    return cm.cameraIdList.firstOrNull { id ->
        try {
            cm.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        } catch (e: Exception) {
            false
        }
    }
}

private fun readMaxStrengthLevel(ctx: Context): Int {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return 1
    val cm = ctx.getSystemService(CameraManager::class.java) ?: return 1
    val id = findFlashCameraId(ctx) ?: return 1
    return try {
        cm.getCameraCharacteristics(id)
            .get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL) ?: 1
    } catch (e: Exception) {
        1
    }
}

private fun turnTorchOn(app: Context, strength: Int) {
    val cm = app.getSystemService(CameraManager::class.java) ?: return
    val id = findFlashCameraId(app) ?: return
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            cm.turnOnTorchWithStrengthLevel(id, strength)
        } else {
            cm.setTorchMode(id, true)
        }
    } catch (e: Exception) {
        try {
            cm.setTorchMode(id, true)
        } catch (e2: Exception) {
        }
    }
}

private fun turnTorchOff(app: Context) {
    val cm = app.getSystemService(CameraManager::class.java) ?: return
    val id = findFlashCameraId(app) ?: return
    try {
        cm.setTorchMode(id, false)
    } catch (e: Exception) {
    }
}
