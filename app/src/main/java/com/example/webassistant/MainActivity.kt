package com.example.webassistant

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    WebAssistantScreen()
                }
            }
        }
    }
}

@Composable
fun WebAssistantScreen() {
    var url by remember { mutableStateOf("https://www.att.com") }
    var userQuestion by remember { mutableStateOf("") }
    var chatHistory by remember { mutableStateOf(listOf<String>()) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // WebView
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                    loadUrl(url)
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
            // Chat History
            chatHistory.forEach { message ->
                Text(
                    text = message,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Input Field
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = userQuestion,
                    onValueChange = { userQuestion = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask about the page...") }
                )
                
                Button(
                    onClick = {
                        if (userQuestion.isNotBlank()) {
                            // TODO: Implement AI processing here
                            chatHistory = chatHistory + "You: $userQuestion"
                            chatHistory = chatHistory + "Assistant: I'll help you navigate the page..."
                            userQuestion = ""
                        }
                    }
                ) {
                    Text("Send")
                }
            }
        }
    }
} 