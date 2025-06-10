# Web Assistant Android App

A mobile application that helps users navigate websites using AI-powered assistance. The app features a WebView component that loads websites and an AI chat interface that helps users understand and navigate the content.

## Features

- WebView integration for loading websites (default: att.com)
- AI-powered chat interface for navigation assistance (LLM integration ready)
- Modern Material Design UI with Jetpack Compose
- Compatible with Android 8.0 (API 26) and above

## Prerequisites

- **Android Studio** (latest recommended)
- **Android SDK** (API 26+)
- **Java 17+**
- **OpenAI API key** (for LLM integration, optional for now)

## Project Structure

```
WebAssistant/
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/example/webassistant/
│           │   ├── MainActivity.kt
│           │   └── AIService.kt
│           └── res/
│               ├── drawable/
│               ├── mipmap-xxxhdpi/
│               └── values/
├── build.gradle
├── gradle.properties
├── gradlew, gradlew.bat
├── gradle/
├── local.properties
└── settings.gradle
```

## Setup & Build

1. **Clone the repository:**
   ```sh
   git clone https://github.com/anandhan/WebAssistant.git
   cd WebAssistant
   ```
2. **Configure Android SDK:**
   - Ensure your `local.properties` file contains the correct SDK path:
     ```
     sdk.dir=/Users/<your-username>/Library/Android/sdk
     ```
3. **Open in Android Studio:**
   - Open the project folder in Android Studio.
   - Let it sync and download dependencies.
4. **Build from command line:**
   ```sh
   ./gradlew build
   ```
5. **Run the app:**
   - Use Android Studio or:
     ```sh
     ./gradlew installDebug
     ```

## LLM Integration (Optional)
- The app is ready for OpenAI API integration. Add your API key and endpoint in `AIService.kt` and implement the network call in `MainActivity.kt`.

## Customization
- Change the default URL in `MainActivity.kt` (`url` variable).
- Update icons in `app/src/main/res/mipmap-xxxhdpi/` as needed.

## Troubleshooting
- Ensure your SDK and Java versions match the requirements.
- If you see version errors, check `build.gradle` and `app/build.gradle` for Kotlin and Compose versions.
- For missing icons or resources, rebuild the project after cleaning: `./gradlew clean build`

## License
This project is licensed under the MIT License. 