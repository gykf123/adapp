package gay.fesui.adapp.ui.tools

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import gay.fesui.adapp.ui.theme.FeSuiTheme

/**
 * 内置全屏浏览器：承载工具页与 FKD 服务器。
 * - JavaScript 与 DOM Storage 均开启（MC 搜索是 ES Module + WASM，必须开 JS）
 * - 顶部显示加载进度条
 * - http(s) 链接留在 WebView 内打开；其他 scheme 交给系统处理
 * - 系统返回键优先回退 WebView 历史，无历史时关闭页面
 */
class WebViewActivity : ComponentActivity() {

    companion object {
        private const val EXTRA_URL = "extra_url"
        private const val EXTRA_TITLE = "extra_title"

        fun start(context: Context, url: String, title: String) {
            val intent = Intent(context, WebViewActivity::class.java)
                .putExtra(EXTRA_URL, url)
                .putExtra(EXTRA_TITLE, title)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra(EXTRA_URL).orEmpty()
        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()

        setContent {
            FeSuiTheme {
                WebViewPage(
                    url = url,
                    title = title,
                    onClose = { finish() },
                )
            }
        }
    }
}

/** 简单持有器：跨重组持有 WebView 实例，供返回键与销毁逻辑使用 */
private class WebViewHolder {
    var webView: WebView? = null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WebViewPage(url: String, title: String, onClose: () -> Unit) {
    val holder = remember { WebViewHolder() }
    var progress by remember { mutableFloatStateOf(0f) }

    // 返回键：有历史先回退，无历史关闭页面
    BackHandler(enabled = true) {
        val webView = holder.webView
        if (webView != null && webView.canGoBack()) {
            webView.goBack()
        } else {
            onClose()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, contentDescription = "关闭")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            WebViewContainer(
                modifier = Modifier.fillMaxSize(),
                url = url,
                onProgressChanged = { progress = it },
                onWebViewCreated = { holder.webView = it },
            )
            if (progress in 0.01f..0.99f) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }

    // 页面销毁时释放 WebView
    DisposableEffect(Unit) {
        onDispose {
            holder.webView?.let { webView ->
                (webView.parent as? ViewGroup)?.removeView(webView)
                webView.destroy()
            }
            holder.webView = null
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewContainer(
    url: String,
    modifier: Modifier = Modifier,
    onProgressChanged: (Float) -> Unit,
    onWebViewCreated: (WebView) -> Unit,
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.databaseEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest,
                    ): Boolean {
                        val target = request.url
                        return if (target.scheme == "http" || target.scheme == "https") {
                            // http(s) 留在 WebView 内打开
                            false
                        } else {
                            // mailto: / intent: 等其他 scheme 交给系统处理
                            try {
                                ctx.startActivity(Intent(Intent.ACTION_VIEW, target))
                            } catch (_: Exception) {
                            }
                            true
                        }
                    }

                    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                        onProgressChanged(0.01f)
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView, newProgress: Int) {
                        onProgressChanged(newProgress / 100f)
                    }
                }
                loadUrl(url)
                onWebViewCreated(this)
            }
        },
    )
}
