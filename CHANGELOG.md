# Changelog
 
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
