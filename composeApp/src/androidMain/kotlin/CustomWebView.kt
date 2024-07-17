import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView


@Composable
actual fun CustomWebView(
    modifier: Modifier,
    initialUrl: String,
    onLoaded: (String, QueryParamsMap) -> Unit,
    onLoading: (String, QueryParamsMap) -> Unit,
    isLoading: (Boolean) -> Unit,
) {


    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->


            WebView(context).apply {
                scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
                setBackgroundColor(Color.TRANSPARENT)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                settings.apply {
                    javaScriptEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(
                        view: WebView?,
                        url: String?,
                        favicon: Bitmap?,
                    ) {
                        isLoading(true)
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val current = request?.url.toString()
                        onLoading(current, current.toQueryParams())
                        return false
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        isLoading(false)
                        url?.let { onLoaded(it, url.toQueryParams()) }
                    }
                }
                loadUrl(initialUrl)
            }
        },
    )


}

actual fun String.toQueryParams(): QueryParamsMap {
    val uri = Uri.parse(this)
    val queryParams = uri.queryParameterNames.associateWith { uri.getQueryParameter(it) }
    return queryParams
}