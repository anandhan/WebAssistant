# Web Assistant Android App

A mobile application that helps users navigate websites using AI-powered assistance. The app features a WebView component that loads websites and an AI chat interface that helps users understand and navigate the content.

## Features

- WebView integration for loading websites (default: att.com)
- AI-powered chat interface for navigation assistance
- Manual content extraction to analyze webpage content
- Configurable LLM model (GPT-4.1, GPT-4, GPT-3.5-turbo)
- Modern Material Design UI with Jetpack Compose
- Compatible with Android 8.0 (API 26) and above

## Prerequisites

- **Android Studio** (latest recommended)
- **Android SDK** (API 26+)
- **Java 17+**
- **OpenAI API key** (for LLM integration)

## Project Structure

```
WebAssistant/
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── assets/
│           │   └── config.properties    # Configuration file
│           ├── java/com/example/webassistant/
│           │   ├── MainActivity.kt
│           │   ├── AIService.kt
│           │   ├── NetworkService.kt
│           │   └── ConfigManager.kt
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

2. **Configure API Key and Model:**
   - Open `app/src/main/assets/config.properties`
   - Set your OpenAI API key:
     ```
     OPENAI_API_KEY=your-api-key-here
     ```
   - Choose your preferred LLM model:
     ```
     OPENAI_MODEL=gpt-4.1  # Options: gpt-4.1, gpt-4, gpt-3.5-turbo
     ```

3. **Configure Android SDK:**
   - Ensure your `local.properties` file contains the correct SDK path:
     ```
     sdk.dir=/Users/<your-username>/Library/Android/sdk
     ```

4. **Open in Android Studio:**
   - Open the project folder in Android Studio.
   - Let it sync and download dependencies.

5. **Build from command line:**
   ```sh
   ./gradlew build
   ```

6. **Run the app:**
   - Use Android Studio or:
     ```sh
     ./gradlew installDebug
     ```

## Usage

1. **Load a Webpage:**
   - The app opens with att.com by default
   - You can modify the URL in `MainActivity.kt`

2. **Analyze Content:**
   - Tap "Extract Page Content" to analyze the current webpage
   - Wait for content extraction to complete

3. **Ask Questions:**
   - Type your question about the webpage content
   - Press "Send" to get an AI-powered response
   - The LLM will analyze the webpage content and provide relevant answers

## Customization

- Change the default URL in `MainActivity.kt` (`url` variable)
- Modify the LLM model in `config.properties`
- Update icons in `app/src/main/res/mipmap-xxxhdpi/` as needed

## Troubleshooting

- Ensure your SDK and Java versions match the requirements
- If you see version errors, check `build.gradle` and `app/build.gradle` for Kotlin and Compose versions
- For missing icons or resources, rebuild the project after cleaning: `./gradlew clean build`
- If you get HTTP 429 errors, check your OpenAI API rate limits and quota

## License

This project is licensed under the MIT License. 