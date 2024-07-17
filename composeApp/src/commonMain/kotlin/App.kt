import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import kwebview.composeapp.generated.resources.Res
import kwebview.composeapp.generated.resources.compose_multiplatform

@Composable
fun App() {

    var url by remember {
        mutableStateOf("")
    }
    var showWebView by remember {
        mutableStateOf(false)
    }
    val hostState = SnackbarHostState()

    val scope = rememberCoroutineScope()

    var loading by remember { mutableStateOf(false) }
    MaterialTheme {


        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState)
            }
        ) { padding ->

            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (!showWebView) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        TextField(
                            value = url,
                            onValueChange = {
                                url = it
                            },
                        )
                        Button(
                            onClick = {
                                if (url.isNotEmpty()) {
                                    showWebView = true
                                }
                            },
                        ) {
                            Text("Load ")
                        }
                    }
                }



                if (showWebView) {
                    Column {

                        Row {

                            IconButton(
                                onClick = {
                                    showWebView = false
                                },
                            ) {
                                Icon(
                                    Icons.Default.Close, contentDescription = "",
                                )
                            }
                        }

                        CustomWebView(
                            isLoading = {
                                loading = it
                            },
                            initialUrl = url,
                            onLoaded = { url, params ->

                                println(params)
                                scope.launch {
                                    hostState.showSnackbar(url)
                                }
                            },
                            onLoading = { url, params ->
                                println(params)
                                scope.launch {
                                    hostState.showSnackbar(url)
                                }
                            },
                        )

                    }
                }


            }

        }
        if (loading) {
            Dialog(
                onDismissRequest = {},
            ) {
                CircularProgressIndicator()
            }
        }

    }
}