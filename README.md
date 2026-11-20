# Lumen

A minimal Android flashlight (torch) app built with **Kotlin** and **Jetpack Compose** (Material 3).

## Features

| Feature | Details |
|---------|---------|
| **Torch** | On/off toggle for the camera flash, controlled via `CameraManager.setTorchMode()` |
| **Brightness slider** | Adjustable strength level (1..max) — shown only on API 33+ when the hardware supports multiple levels (`FLASH_INFO_STRENGTH_MAXIMUM_LEVEL > 1`) |
| **SOS mode** | Blinking Morse-code pattern (`SOS` = short-short-short, long-long-long, short-short-short) driven by a coroutine; auto-stops when the switch is off or the activity is paused/destroyed |
| **Dynamic Material 3 theme** | `dynamicLightColorScheme` / `dynamicDarkColorScheme` on API 31+, falls back to the standard Material 3 light/dark schemes below |

No runtime permissions are required — `setTorchMode` and `turnOnTorchWithStrengthLevel` do **not** need camera permissions on API 26+.

## Requirements

| | Value |
|--|-------|
| **minSdk** | 26 (Android O / API 26) |
| **compileSdk** | 34 |
| **Language** | Kotlin 1.9.24 |
| **UI** | Jetpack Compose BOM 2024.06.00, Material 3 |
| **Build** | Gradle (Kotlin DSL), AGP 8.5.2 |
| **Java** | JDK 17 |

## Architecture

```
app/
├── build.gradle.kts          # App config, signing config, compose feature flag
├── src/main/
│   ├── java/com/example/lumen/
│   │   └── MainActivity.kt   # Single-activity Compose app: theme + screen + torch/SOS logic
│   └── AndroidManifest.xml
├── lumen-release.jks         # Generated on CI via keytool, not checked into VCS
```

**`MainActivity.kt`**
- `LumenTheme` composable — picks the Material 3 color scheme (dynamic on API 31+, static fallback below).
- `LumenScreen` composable — centered column with:
  - `Slider` (visible when `maxStrengthLevel > 1` and API ≥ 33)
  - `Button` — torch on/off toggle (lightbulb icon + label)
  - `Switch` — SOS blinking Morse pattern via `LaunchedEffect`
- Helper functions:
  - `findFlashCameraId()` — enumerates cameras, picks the first with `FLASH_INFO_AVAILABLE`
  - `readMaxStrengthLevel()` — returns the hardware max strength level (API 33+, else `1`)
  - `turnTorchOn()/turnTorchOff()` — calls `turnOnTorchWithStrengthLevel` (API 33+) or falls back to `setTorchMode`

## Build & Release

Builds run in **GitHub Actions** on every push to a tag matching `v*` (and manually via `workflow_dispatch`).

### CI steps

1. **Setup JDK 17** (Temurin)
2. **Setup Gradle**
3. **Setup Android SDK** (`android-actions/setup-android@v4`) — installs `platform-tools`
4. **Install Android SDK platform and build-tools** — `sdkmanager "platforms;android-34" "build-tools;34.0.0"`
5. **Generate signing key** via `keytool` (RSA 2048, pkcs12) → `app/lumen-release.jks`
6. **Assemble release** — `./gradlew assembleRelease`
7. **Publish** — signed APK is uploaded as a GitHub Release with auto-generated changelog

### Local development (optional)

```bash
# Build debug APK (not required for CI)
./gradlew assembleDebug
```

### Release process

Tags `v0.1.0`, `v0.1.1`, … are managed by the **release automation script** (`~/tools/release-app.sh`). Each tag triggers the GitHub Actions workflow, which builds, signs, and publishes a release.

```bash
~/tools/release-app.sh   # pushes, tags next patch version, watches CI
```

## Downloading the APK

Each GitHub Release tag has an `app-release.apk` asset:

```bash
# List releases
gh release list

# Download the latest release APK
gh release download v0.1.9 --output lumen.apk

# Or via the release page
# https://github.com/hafizhasant/lumen/releases
```

## Project metadata

| Property | Value |
|----------|-------|
| **Package** | `com.example.lumen` |
| **Display name** | Lumen |
| **Current release** | `0.1.9` (versionCode 6) |
| **Language** | Kotlin |
| **UI** | Jetpack Compose (Material 3) |
| **License** | MIT |

## License

[MIT License](LICENSE)
