import android.content.Context
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraCharacteristics
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DarkTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.LightTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LightbulbOn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LumenTheme { LumenScreen(this) }
        }
    }

    override fun onPause() {
        super.onPause()
        torchOff(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        torchOff(this)
    }
}

@Composable
fun LumenTheme(content: @Composable () -> Unit) {
    val ctx = LocalContext.current
    val isDark = ctx.resources.configuration.uiMode and 0x02 != 0
    val isApi31Plus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val scheme: ColorScheme = when {
        isApi31Plus && isDark -> dynamicDarkColorScheme(ctx)
        isApi31Plus -> dynamicLightColorScheme(ctx)
        isDark -> darkColorScheme()
        else -> lightColorScheme()
    }
    if (isDark) {
        DarkTheme(colorScheme = scheme) { content() }
    } else {
        LightTheme(colorScheme = scheme) { content() }
    }
}

@Composable
fun LumenScreen(app: Context) {
    val ctx = LocalContext.current
    val isApi33Plus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    val maxStrengthLevel = remember(ctx) {
        findFlashCameraId(ctx)?.let { id ->
            val cm = ctx.getSystemService(CameraManager::class.java) ?: return@let 1
            try {
                cm.getCameraCharacteristics(id)
                    .get(CameraCharacteristics.FLASH_INFO_MAX_TORCH_MODE) ?: 1
            } catch (e: Exception) {
                1
            }
        } ?: 1
    }

    var torchOn by remember { mutableStateOf(false) }
    var strength by remember { mutableStateOf(1) }
    var sosOn by remember { mutableStateOf(false) }
    var sosScope by remember { mutableStateOf<CoroutineScope?>(null) }

    LaunchedEffect(sosOn) {
        if (sosOn) {
            val shortMs = 150L
            val longMs = 450L
            val gap = 150L
            val pattern = listOf(shortMs, shortMs, shortMs, longMs, longMs, longMs, shortMs, shortMs, shortMs)
            for (p in pattern) {
                torchOn(app, strength)
                delay(p + gap)
            }
        } else {
            torchOff(app)
        }
    }

    Box(Modifier.fillMaxSize().align(Alignment.Center)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isApi33Plus && maxStrengthLevel > 1) {
                Text("Strength")
                Spacer(Modifier.height(4.dp))
                val maxStrength = maxStrengthLevel - 1
                Slider(
                    value = strength.toFloat(),
                    valueRange = 1f..maxStrength.toFloat(),
                    onValueChange = {
                        strength = it.toInt()
                        torchOn(app, strength)
                    }
                )
            }

            Spacer(Modifier.height(100.dp))

            Button(
                onClick = {
                    if (torchOn) {
                        torchOff(app)
                        torchOn = false
                    } else {
                        torchOn(app, strength)
                        torchOn = true
                    }
                }
            ) {
                Icon(
                    imageVector = if (torchOn) Icons.Filled.LightbulbOn else Icons.Filled.Lightbulb,
                    contentDescription = "Torch"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(" Torch")
            }

            Spacer(Modifier.height(24.dp))

            Text("SOS")
            Spacer(Modifier.height(4.dp))
            Switch(
                checked = sosOn,
                onCheckedChange = { sosOn = it }
            )
        }
    }
}

private fun findFlashCameraId(ctx: Context): String? {
    val cm = ctx.getSystemService(CameraManager::class.java) ?: return null
    return cm.enumerateCameraIds().firstOrNull { id ->
        try {
            cm.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        } catch (e: Exception) {
            false
        }
    }
}

private fun torchOn(app: Context, strength: Int) {
    val cm = app.getSystemService(CameraManager::class.java) ?: return
    val id = findFlashCameraId(app) ?: return
    try {
        val props = cm.getCameraCharacteristics(id)
        val maxLevel = props.get(CameraCharacteristics.FLASH_INFO_MAX_TORCH_MODE) ?: 1
        cm.setTorchMode(id, true)
    } catch (e: Exception) {
        try {
            cm.setTorchMode(id, true)
        } catch (e2: Exception) {
        }
    }
}

private fun torchOff(app: Context) {
    val cm = app.getSystemService(CameraManager::class.java) ?: return
    val id = findFlashCameraId(app) ?: return
    try {
        cm.setTorchMode(id, false)
    } catch (e: Exception) {
    }
}
