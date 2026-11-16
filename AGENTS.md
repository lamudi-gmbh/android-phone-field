# AGENTS.md — guiComponents

macOS (Apple Silicon or Intel) command-line build setup — **no Android Studio required**.

**What this is:** home of the shared **`:gui`** widget library (e.g. `FractionButton`, `AppRate`) that
Real-Piano-Keyboard, Real-Ear-Training and Real-Music-Dictations consume by relative path
(`../guiComponents/gui`). Self-contained — no external sibling modules.

| Gradle module | compileSdk | Role |
|---------------|------------|------|
| `:gui`        | **37.2**   | shared widget library (consumed by the three Real-* apps) |

## Toolchain (2026-11-16)

| Tool | Version | Notes |
|------|---------|-------|
| JDK (runs Gradle) | **21** (Temurin) | 17 and 25 also OK (25 checked on Gradle 9.9.1, 2026-11-16). |
| Gradle | 9.9.1 | via `./gradlew`; nothing to install |
| Android Gradle Plugin | 9.5.1 | builds compileSdk 37.2 with no warning |
| Platform | `android-37.2` | API 37 uses *minor-version* packaging |
| Build-tools | 36.0.0 | the plugin's default |
| Java source/target | 17 | |

## 1 — JDK 21

Pick one:
```bash
# Homebrew
brew install --cask temurin@21
export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
```
```bash
# …or SDKMAN
curl -s "https://get.sdkman.io" | bash && source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 21.0.11-tem
```
Verify with `java -version` → `21.x`.

## 2 — Android SDK + `ANDROID_HOME`

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"
```
If you don't already have the command-line tools, download **"Command line tools only" (Mac)** from
<https://developer.android.com/studio#command-line-tools-only> and unzip into place:
```bash
mkdir -p "$ANDROID_HOME/cmdline-tools"
cd /tmp && unzip -q commandlinetools-mac-*_latest.zip     # creates ./cmdline-tools/
mv cmdline-tools "$ANDROID_HOME/cmdline-tools/latest"
```
(Persist the two `export` lines in `~/.zshrc`. Alternatively `brew install --cask android-commandlinetools`,
but then point `ANDROID_HOME` at its Homebrew prefix instead.)

### ⚠️ API 37 = `android-37.2` (minor-version packaging)
The platform package is **`platforms;android-37.2`**, *not* `platforms;android-37` — the latter fails with
*"Failed to find package."* Older `cmdline-tools` can't even list it. If `sdkmanager --list | grep android-37`
comes back empty, update the tools first:
```bash
sdkmanager --install "cmdline-tools;latest"
# If it can't overwrite the in-use 'latest', it installs 'latest-2' — promote it:
rm -rf "$ANDROID_HOME/cmdline-tools/latest" && mv "$ANDROID_HOME/cmdline-tools/latest-2" "$ANDROID_HOME/cmdline-tools/latest"
```
(The `SDK XML version 3` warning sdkmanager prints is harmless.)

## 3 — SDK packages
```bash
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-37.2" "build-tools;36.0.0"
```

## 4 — Build
```bash
chmod +x gradlew                            # exec bit is often lost on checkout (or run: sh gradlew …)
./gradlew :gui:assembleDebug                # just the shared widget library (.aar)
```

## Troubleshooting
- **`Failed to find package platforms;android-37`** → use `android-37.2`, and update cmdline-tools (§2).
- **Gradle dies under an unsupported JDK** (`Unsupported class file major version`, toolchain errors) → point `JAVA_HOME` at JDK 17–25.
- **`./gradlew: Permission denied`** → `chmod +x gradlew`, or run `sh gradlew …`.
- **SSL / `PKIX` / handshake errors from sdkmanager or Gradle** (corporate proxy) → import the proxy's root CA into the JDK that runs the build:
  ```bash
  sudo keytool -importcert -alias corp-proxy -cacerts -file /path/to/proxy-ca.cer   # default store password: changeit
  ```
