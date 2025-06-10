package com.example.webassistant

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
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

class MainActivity : ComponentActivity() {
    private val TAG = "WebAssistant"
    private lateinit var networkService: NetworkService
    private var currentWebContent: String = ""
    var webView: WebView? = null

    inner class WebAppInterface {
        @JavascriptInterface
        fun onContentReceived(content: String) {
            currentWebContent = content
            Log.d(TAG, "Received web content: ${content.take(100)}...")
        }
    }

    fun extractWebContent() {
        webView?.evaluateJavascript("""
            function extractContent() {
                const content = {
                    title: document.title,
                    text: document.body.innerText,
                    links: Array.from(document.getElementsByTagName('a')).map(a => a.href),
                    headings: Array.from(document.getElementsByTagName('h1')).map(h => h.innerText)
                };
                Android.onContentReceived(JSON.stringify(content));
            }
            extractContent();
        """.trimIndent(), null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize ConfigManager and NetworkService
        ConfigManager.initialize(this)
        networkService = NetworkService()

        setContent {
            WebAssistantScreen(
                networkService = networkService,
                getWebContent = { currentWebContent },
                onExtractContent = { extractWebContent() }
            )
        }
    }
}

@Composable
fun WebAssistantScreen(
    networkService: NetworkService,
    getWebContent: () -> String,
    onExtractContent: () -> Unit
) {
    val TAG = "WebAssistant"
    var chatHistory by remember { mutableStateOf(listOf<Message>()) }
    var userInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var hasExtractedContent by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // WebView
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                    addJavascriptInterface((context as MainActivity).WebAppInterface(), "Android")
                    loadUrl("https://www.att.com")
                    (context as MainActivity).webView = this
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
            // Extract Content Button
            if (!hasExtractedContent) {
                Button(
                    onClick = {
                        onExtractContent()
                        hasExtractedContent = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Extract Page Content")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Chat History
            chatHistory.forEach { message ->
                Text(
                    text = "${message.role}: ${message.content}",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Loading Indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
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
                    placeholder = { Text("Ask about the webpage content...") },
                    enabled = hasExtractedContent
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (userInput.isNotBlank() && hasExtractedContent) {
                            scope.launch {
                                isLoading = true
                                try {
                                    val webContent = getWebContent()
                                    Log.d(TAG, "Sending request to OpenAI API")
                                    Log.d(TAG, "Web content length: ${webContent.length}")
                                    
                                    val systemMessage = """
                                        You are a helpful assistant analyzing a webpage. 
                                        Use the following webpage content to answer the user's question:
                                        $webContent
                                        
                                        If the question cannot be answered using the webpage content, 
                                        say so and provide a general helpful response.
                                    """.trimIndent()

                                    val response = networkService.aiService.processQuestion(
                                        AIRequest(
                                            model = "gpt-4.1",
                                            messages = listOf(
                                                Message("system", systemMessage),
                                                Message("user", userInput)
                                            )
                                        )
                                    )
                                    
                                    Log.d(TAG, "Received response: $response")
                                    if (response.choices.isNotEmpty()) {
                                        val aiResponse = response.choices[0].message.content
                                        Log.d(TAG, "AI response content: $aiResponse")
                                        chatHistory = chatHistory + listOf(
                                            Message("user", userInput),
                                            Message("assistant", aiResponse)
                                        )
                                    } else {
                                        Log.e(TAG, "Empty choices in response")
                                        chatHistory = chatHistory + listOf(
                                            Message("user", userInput),
                                            Message("assistant", "Sorry, I couldn't generate a response.")
                                        )
                                    }
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error processing request", e)
                                    Log.e(TAG, "Error message: ${e.message}")
                                    Log.e(TAG, "Error cause: ${e.cause}")
                                    e.printStackTrace()
                                    chatHistory = chatHistory + listOf(
                                        Message("user", userInput),
                                        Message("assistant", "Sorry, there was an error processing your request: ${e.message}")
                                    )
                                } finally {
                                    isLoading = false
                                    userInput = ""
                                }
                            }
                        }
                    },
                    enabled = hasExtractedContent && userInput.isNotBlank()
                ) {
                    Text("Send")
                }
            }
        }
    }
} 