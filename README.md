# WebAssistant

## Overview
WebAssistant is an Android application designed to provide users with an interactive web browsing experience combined with an AI assistant. The app allows users to navigate to a URL, view the webpage content, and interact with an AI assistant that can answer questions about the webpage content.

## Features
- **WebView Integration:** Displays web content within the app.
- **AI Assistant:** Provides context-aware responses to user questions based on the current webpage content and a knowledge base.
- **Knowledge Base:** Contains provider-specific information (e.g., porting steps, common issues) and general porting information.
- **Configuration Management:** Loads configuration properties from a 'config.properties' file, including the OpenAI API key and model settings.

## Components
- **MainActivity:** The main entry point of the app, setting up the UI using Jetpack Compose and managing the WebView.
- **WebViewActivity:** Displays a WebView and manages a chat interface for interacting with the AI assistant.
- **KnowledgeBaseManager:** Loads and manages a JSON-based knowledge base stored in the app's assets folder.
- **ConfigManager:** Loads and manages configuration properties from a 'config.properties' file.
- **NetworkService:** Handles network requests to external APIs, specifically the OpenAI API.
- **AIService:** Defines the API endpoints for processing questions using the OpenAI API.

## Setup
1. Clone the repository.
2. Open the project in Android Studio.
3. Ensure that the 'config.properties' file is present in the assets folder with the necessary configuration settings.
4. Build and run the application on an Android device or emulator.

## Dependencies
- Jetpack Compose for UI
- OkHttp for network requests
- Retrofit for API service definition
- OpenAI API for AI responses

## License
This project is licensed under the MIT License - see the LICENSE file for details. 