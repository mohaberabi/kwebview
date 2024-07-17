import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


typealias QueryParamsMap = Map<String, String?>

@Composable
expect fun CustomWebView(
    modifier: Modifier = Modifier,
    initialUrl: String,
    onLoaded: (String, QueryParamsMap) -> Unit,
    onLoading: (String, QueryParamsMap) -> Unit,
    isLoading: (Boolean) -> Unit,
)

expect fun String.toQueryParams(): QueryParamsMap