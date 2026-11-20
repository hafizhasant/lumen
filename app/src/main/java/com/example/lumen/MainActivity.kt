package com.example.lumen

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    val ctx = LocalContext.current
    val maxStrengthLevel = remember(ctx) { readMaxStrengthLevel(ctx) }

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
                // Runs when SOS is switched off or the composable leaves composition.
                turnTorchOff(app)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (maxStrengthLevel > 1) {
            Text("Strength")
            Spacer(Modifier.height(4.dp))
            Slider(
                value = strength.toFloat(),
                valueRange = 1f..maxStrengthLevel.toFloat(),
                steps = (maxStrengthLevel - 2).coerceAtLeast(0),
                onValueChange = {
                    strength = it.toInt()
                    if (torchOn) turnTorchOn(app, strength)
                }
            )
            Spacer(Modifier.height(24.dp))
        }

        Button(
            enabled = !sosOn,
            onClick = {
                if (torchOn) {
                    turnTorchOff(app)
                    torchOn = false
                } else {
                    turnTorchOn(app, strength)
                    torchOn = true
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Lightbulb,
                contentDescription = "Torch"
            )
            Spacer(Modifier.width(8.dp))
            Text("Torch")
        }

        Spacer(Modifier.height(24.dp))

        Text("SOS")
        Spacer(Modifier.height(4.dp))
        Switch(
            checked = sosOn,
            onCheckedChange = {
                sosOn = it
                if (it) torchOn = false
            }
        )
    }
}

private fun findFlashCameraId(ctx: Context): String? {
    val cm = ctx.getSystemService(CameraManager::class.java) ?: return null
    return cm.cameraIdList.firstOrNull { id ->
        try {
            cm.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
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
