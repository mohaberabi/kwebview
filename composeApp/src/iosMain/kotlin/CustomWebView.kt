import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem
import platform.Foundation.NSURLRequest
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIView
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.darwin.NSObject


@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CustomWebView(
    modifier: Modifier,
    initialUrl: String,
    onLoaded: (String, QueryParamsMap) -> Unit,
    onLoading: (String, QueryParamsMap) -> Unit,
    isLoading: (Boolean) -> Unit,
) {
    val webView = remember {
        WKWebView(
            frame = CGRectZero.readValue(),
        )
    }

    val delegate = rememberWebViewDelegate(
        onLoaded = onLoaded,
        onLoading = onLoading,
        isLoading = isLoading,
    )
    UIKitView(

        modifier = modifier.fillMaxSize(),
        onResize = { view: UIView, rect: CValue<CGRect> ->
            CATransaction.begin()
            CATransaction.setValue(true, kCATransactionDisableActions)
            view.layer.setFrame(rect)
            webView.setFrame(rect)
            CATransaction.commit()
        },
        factory = {
            webView.navigationDelegate = delegate
            webView.apply {
                WKWebViewConfiguration().apply {
                    allowsInlineMediaPlayback = true
                    allowsAirPlayForMediaPlayback = true
                    allowsPictureInPictureMediaPlayback = true
                }
            }
            webView.loadRequest(NSURLRequest.requestWithURL(NSURL.URLWithString(initialUrl)!!))
            webView
        },
    )
}

class WebViewDelegate(
    private val onLoaded: (String, QueryParamsMap) -> Unit,
    private val onLoading: (String, QueryParamsMap) -> Unit,
    private val isLoading: (Boolean) -> Unit,
) : NSObject(), WKNavigationDelegateProtocol {
    override fun webView(webView: WKWebView, didFinishNavigation: WKNavigation?) {
        val current = webView.URL?.absoluteString
        current?.let {
            onLoaded(it, it.toQueryParams())

        }
        isLoading(false)
    }

    override fun webView(
        webView: WKWebView,
        decidePolicyForNavigationAction: WKNavigationAction,
        decisionHandler: (WKNavigationActionPolicy) -> Unit
    ) {
        isLoading(true)
        val currentUrl =
            decidePolicyForNavigationAction.request.URL?.absoluteString ?: ""
        decisionHandler(platform.WebKit.WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
        onLoading(currentUrl, currentUrl.toQueryParams())
    }
}


@Composable
fun rememberWebViewDelegate(
    onLoaded: (String, QueryParamsMap) -> Unit,
    onLoading: (String, QueryParamsMap) -> Unit,
    isLoading: (Boolean) -> Unit,
): WebViewDelegate {
    return remember {
        WebViewDelegate(
            onLoaded = onLoaded,
            onLoading = onLoading,
            isLoading = isLoading
        )
    }
}

actual fun String.toQueryParams(): QueryParamsMap {
    val urlComponents = NSURLComponents.componentsWithString(this)
    val queryItems =
        urlComponents?.queryItems?.map { it as? NSURLQueryItem } ?: emptyList()
    val queryParams =
        queryItems.mapNotNull { it }.associate { it.name to it.value }
    return queryParams
}