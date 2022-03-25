// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.plugins.configureHTTP
import com.example.plugins.configureMonitoring
import com.example.plugins.configureRouting
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() {
    val serverJob: Job
    val clientJob: Job
    runBlocking {
        clientJob = launch(Dispatchers.Default) {
            application {
                Window(onCloseRequest = ::exitApplication) {
                    App()
                }
            }
        }
        serverJob = launch(Dispatchers.Default) {
            io.ktor.server.engine.embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
                configureRouting()
                configureMonitoring()
                configureHTTP()
            }.start(wait = true)
        }
        clientJob.join()
        serverJob.join()
    }
}
