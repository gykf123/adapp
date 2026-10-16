package gay.fesui.adapp.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import gay.fesui.adapp.R
import gay.fesui.adapp.config.AppConfig
import gay.fesui.adapp.ui.components.GlassCard
import gay.fesui.adapp.ui.home.SectionTitle

/**
 * Tab 5 工具：三个入口卡，点击用内置 WebView 全屏打开。
 */
@Composable
fun ToolsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Spacer(Modifier.height(20.dp))
        SectionTitle(stringResource(R.string.tools_title))
        Spacer(Modifier.height(2.dp))

        ToolEntryCard(
            title = stringResource(R.string.tools_fkd_title),
            desc = stringResource(R.string.tools_fkd_desc),
            onClick = { WebViewActivity.start(context, AppConfig.Urls.FKD_SERVER, "FKD 服务器") },
        )
        ToolEntryCard(
            title = stringResource(R.string.tools_ore_title),
            desc = stringResource(R.string.tools_ore_desc),
            onClick = { WebViewActivity.start(context, AppConfig.Urls.ORE_FINDER, "矿石查找器") },
        )
        ToolEntryCard(
            title = stringResource(R.string.tools_mcsearch_title),
            desc = stringResource(R.string.tools_mcsearch_desc),
            onClick = { WebViewActivity.start(context, AppConfig.Urls.MC_SEARCH, "MC 搜索") },
        )
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun ToolEntryCard(title: String, desc: String, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        contentPadding = PaddingValues(18.dp),
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
