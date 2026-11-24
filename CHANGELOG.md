# Changelog

## [0.1.18] - 2026-11-24
- Fixed compile errors from CI run 45363835598:
  - `pointerInput` moved to `androidx.compose.ui.input.pointer` (was `androidx.compose.foundation.gestures`)
  - Removed invalid top-level imports for `awaitPointerEvent`, `awaitPointerEventScope`, `EventType`; these resolve through their receiver scopes inside the `pointerInput` block
  - Renamed `EventType.PointerDown`/`PointerMove`/`PointerUp` to `PointerEventType.Press`/`Move`/`Release`
  - Added `import androidx.compose.foundation.border.border`

## [0.1.17] - 2026-11-24
- Improved card/background contrast: brighter glass-card gradient (0xFF2A3B54 → 0xFF121B26) with a visible border
- Replaced "Strength X/N" text with dot indicators (one per level, gold when active)
- Swipe up on the card increases strength, swipe down decreases it; short tap toggles torch on/off
- SOS toggle remains in bottom-right row

## [0.1.14] - 2026-11-24
- Dark themed UI with darker background and card
- Reduced card size for better proportion
- Fixed icon alignment, SOS button now in bottom-right row layout

## [0.1.13] - 2026-11-24
- Fixed remaining compile errors:
  - `TextStyle` requires `TextUnit` not `Dp` — changed `fontSize = 30.dp` to `30.sp`, `fontSize = 13.dp` to `13.sp`
  - Added `import androidx.compose.ui.unit.sp`
- Bumped versionCode to 13, versionName to 0.1.13

## [0.1.10] - 2026-11-24
- Fixed compile errors from CI:
  - Removed invalid `import androidx.compose.foundation.layout.align`
  - Added missing `import androidx.compose.material3.Surface`
  - Added missing `import androidx.compose.ui.text.style.TextStyle`
  - Converted `Text` calls to use `style = TextStyle(...)` instead of separate `fontSize`/`fontWeight`/`textAlign` parameters
  - Fixed `.offset()` in `FlashlightDotArc` to use `.dp()` instead of `.toInt()`
- Bumped versionCode to 10, versionName to 0.1.10

## [0.1.6] - 2026-11-20
- Fixed CI build: `android-actions/setup-android@v4`'s `packages:` input did not install `platforms;android-34` or `build-tools;34.0.0` (only the default `platform-tools` was installed), so `compileSdk = 34` could not resolve Android platform classes (camera2 `enumerateCameraIds`, `FLASH_INFO_MAX_TORCH_MODE`, Material3 themes). Added an explicit `sdkmanager "platforms;android-34" "build-tools;34.0.0"` step after the setup step.
- Bumped versionCode to 6, versionName to 0.1.6
- Fixed CI build: `android-actions/setup-android@v4` only installed `platform-tools` by default. Added `packages: 'platforms;android-34 build-tools;34.0.0 platform-tools'` so compileSdk 34 resolves Android platform classes (camera2 `enumerateCameraIds`, `FLASH_INFO_MAX_TORCH_MODE`) in CI.
- Added missing `import androidx.compose.foundation.layout.align` in MainActivity.kt (line 119 `.align()` was unresolved).
- Bumped versionCode to 5, versionName to 0.1.5

## [0.1.3] - 2026-11-20
- Fixed Kotlin compile errors in MainActivity.kt:
  - Added missing `androidx.compose.foundation.layout.height`/`width` imports
  - Added missing `androidx.compose.runtime.LaunchedEffect` import
  - Replaced all `Spacer(height=)` with `Spacer(Modifier.height())`
  - Replaced `Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center)` with `Box(Modifier.fillMaxSize().align(Alignment.Center))`
  - Replaced `cm.findCameraIds()` with `cm.enumerateCameraIds()` (removed API)
  - Replaced `cm.turnOnTorchWithStrengthLevel(id, level, maxLevel - 1)` with `cm.setTorchMode(id, true)` (correct API signature)
- Added missing dependencies to app/build.gradle.kts:
  - `androidx.compose.material:material-icons-extended`
  - `androidx.lifecycle:lifecycle-runtime-compose:2.7.0`
  - `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3`
- Bumped versionCode to 3, versionName to 0.1.3
 
## [0.1.2] - 2026-11-20
- Fixed build: set Compose compiler version to 1.5.14 (compatible with Kotlin 1.9.24; `1.9.24` is not a valid Compose compiler artifact)
- Bumped versionCode to 2, versionName to 0.1.2

## [0.1.1] - 2026-11-20
- Added missing root build files and fixed `.gitignore`

## [0.1.0] - 2026-11-20
- Initial project setup
