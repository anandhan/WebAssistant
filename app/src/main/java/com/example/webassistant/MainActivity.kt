/**
 * MainActivity.kt
 * 
 * Purpose:
 * This file defines the main entry point of the WebAssistant app. It sets up the UI using Jetpack Compose
 * and manages the WebView for displaying web content. The app allows users to navigate to a URL, view the
 * webpage, and interact with an AI assistant that can answer questions about the webpage content.
 * 
 * Key Components:
 * - formatUrl: A utility function that ensures URLs are properly formatted with 'https://' if missing.
 * - MainActivity: The main activity class that initializes the NetworkService and sets up the Compose UI.
 * - WebAssistantScreen: A Composable function that defines the UI layout, including the URL input, WebView,
 *   and chat interface. It also handles user interactions, such as sending messages to the AI assistant.
 * - WebAppInterface: A class that provides a JavaScript interface for the WebView to extract webpage content.
 * - ChatMessage: A Composable function that displays individual chat messages with different styles for user
 *   and assistant messages.
 * 
 * The app integrates with an AI service (via NetworkService) to process user questions and provide responses
 * based on the current webpage content.
 */

package com.example.webassistant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch
import com.example.webassistant.AIRequest
import com.example.webassistant.Message

// Add this at the top-level, outside any class
fun formatUrl(url: String): String {
    return if (!url.startsWith("http://") && !url.startsWith("https://")) {
        "https://$url"
    } else {
        url
    }
}

class MainActivity : ComponentActivity() {
    private val TAG = "WebAssistant"
    private lateinit var networkService: NetworkService
    private var currentWebContent: String = ""
    var webView: WebView? = null
    var onPageChanged: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize ConfigManager and NetworkService
        ConfigManager.initialize(this)
        networkService = NetworkService()

        setContent {
            WebAssistantScreen(
                networkService = networkService,
                getWebContent = { currentWebContent },
                onPageChanged = { onPageChanged?.invoke() },
                onLaunchProviderView = { url ->
                    // Launch WebViewActivity with the current URL
                    val intent = Intent(this, WebViewActivity::class.java).apply {
                        putExtra("url", url)
                    }
                    startActivity(intent)
                }
            )
        }
    }

    fun extractWebContent() {
        webView?.evaluateJavascript("""
            function extractContent() {
                const content = {
                    title: document.title,
                    text: document.body.innerText,
                    links: Array.from(document.getElementsByTagName('a')).map(a => a.href),
                    headings: Array.from(document.getElementsByTagName('h1')).map(h => h.innerText),
                    paragraphs: Array.from(document.getElementsByTagName('p')).map(p => p.innerText),
                    lists: Array.from(document.getElementsByTagName('ul')).map(ul => Array.from(ul.getElementsByTagName('li')).map(li => li.innerText))
                };
                Android.onContentReceived(JSON.stringify(content));
            }
            extractContent();
        """.trimIndent(), null)
    }
}

@Composable
fun WebAssistantScreen(
    networkService: NetworkService,
    getWebContent: () -> String,
    onPageChanged: (() -> Unit) -> Unit,
    onLaunchProviderView: (String) -> Unit
) {
    val TAG = "WebAssistant"
    var chatHistory by remember { mutableStateOf(listOf<Message>()) }
    var userInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var hasExtractedContent by remember { mutableStateOf(false) }
    var urlInput by remember { mutableStateOf("https://www.att.com") }
    var webView by remember { mutableStateOf<WebView?>(null) }
    val scope = rememberCoroutineScope()

    class WebAppInterface {
        @JavascriptInterface
        fun getWebContent(): String {
            return webView?.let { view ->
                var result = "{}"
                view.evaluateJavascript("""
                    (function() {
                        return {
                            title: document.title,
                            text: document.body.innerText,
                            links: Array.from(document.getElementsByTagName('a')).map(a => a.href),
                            headings: Array.from(document.getElementsByTagName('h1')).map(h => h.innerText),
                            paragraphs: Array.from(document.getElementsByTagName('p')).map(p => p.innerText),
                            lists: Array.from(document.querySelectorAll('ul, ol')).map(list => 
                                Array.from(list.getElementsByTagName('li')).map(li => li.innerText)
                            )
                        }
                    })()
                """.trimIndent()) { value -> result = value }
                result
            } ?: "{}"
        }
    }

    // Set up page change callback
    LaunchedEffect(Unit) {
        onPageChanged {
            hasExtractedContent = false
            chatHistory = listOf()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // URL Input and Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter URL...") },
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    webView?.loadUrl(formatUrl(urlInput))
                    hasExtractedContent = false
                    chatHistory = listOf()
                }
            ) {
                Text("Go")
            }
        }

        // WebView
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    }
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            Log.d(TAG, "Page finished loading: $url")
                            hasExtractedContent = false
                            chatHistory = listOf()
                        }
                    }
                    addJavascriptInterface(WebAppInterface(), "Android")
                    loadUrl(formatUrl(urlInput))
                    webView = this
                }
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        // Chat Interface
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Welcome Message
            if (!hasExtractedContent) {
                Text(
                    text = "Ask me about this website",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Chat History
            chatHistory.forEach { message ->
                ChatMessage(message)
            }

            // Loading Indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            // Input Field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask a question...") },
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (userInput.isNotBlank()) {
                            scope.launch {
                                isLoading = true
                                try {
                                    val webContent = getWebContent()
                                    val systemMessage = """
                                        You are a helpful assistant analyzing a webpage. 
                                        Use the following webpage content to answer the user's question:
                                        $webContent
                                        
                                        If the question cannot be answered using the webpage content, 
                                        say so and provide a general helpful response.
                                    """.trimIndent()

                                    val response = networkService.aiService.processQuestion(
                                        AIRequest(
                                            model = ConfigManager.getOpenAIModel(),
                                            messages = listOf(
                                                Message("system", systemMessage),
                                                Message("user", userInput)
                                            )
                                        )
                                    )
                                    
                                    if (response.choices.isNotEmpty()) {
                                        val aiResponse = response.choices[0].message.content
                                        chatHistory = chatHistory + Message("user", userInput) + Message("assistant", aiResponse)
                                    } else {
                                        chatHistory = chatHistory + Message("user", userInput) + Message("assistant", "Sorry, I couldn't generate a response.")
                                    }
                                    userInput = ""
                                    hasExtractedContent = true
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error sending message", e)
                                    chatHistory = chatHistory + Message("user", userInput) + Message("assistant", "Sorry, I encountered an error. Please try again.")
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    enabled = !isLoading && userInput.isNotBlank()
                ) {
                    Text("Send")
                }
            }
        }

        // Add a button to launch the provider-specific view
        Button(
            onClick = { onLaunchProviderView(urlInput) },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Open Provider View")
        }
    }
}

@Composable
fun ChatMessage(message: Message) {
    val alignment = if (message.role == "user") Alignment.End else Alignment.Start
    val backgroundColor = if (message.role == "user") 
        MaterialTheme.colorScheme.primary 
    else 
        MaterialTheme.colorScheme.secondary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = backgroundColor,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = message.content,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
} 