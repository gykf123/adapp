package gay.fesui.adapp.ui.projects

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import gay.fesui.adapp.R
import gay.fesui.adapp.data.ProjectItem
import gay.fesui.adapp.data.Projects
import gay.fesui.adapp.ui.components.GlassCard
import gay.fesui.adapp.ui.components.PillTag
import gay.fesui.adapp.ui.home.SectionTitle

/**
 * Tab 2 项目：5 张卡片。
 * 无 href 的前 4 张标注「规划中」角标（与网页一致），点击 toast 提示；
 * FKD 服务器主页卡片点击用浏览器 Intent 打开。
 */
@Composable
fun ProjectsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { SectionTitle(stringResource(R.string.projects_title)) }
        items(Projects) { project ->
            ProjectCard(
                project = project,
                onClick = {
                    val url = project.url
                    if (url.isNullOrEmpty()) {
                        Toast.makeText(
                            context,
                            R.string.projects_toast_planning,
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else {
                        openInBrowser(context, url)
                    }
                },
            )
        }
    }
}

@Composable
private fun ProjectCard(project: ProjectItem, onClick: () -> Unit) {
    val planning = project.url.isNullOrEmpty()
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        contentPadding = PaddingValues(18.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = project.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (planning) {
                    PlanningBadge()
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = project.desc,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                project.tags.forEach { tag ->
                    PillTag(
                        text = tag,
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}

/** 「规划中」角标：右上角小标签 */
@Composable
private fun PlanningBadge() {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = stringResource(R.string.projects_badge_planning),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

/** 用浏览器 Intent 打开 http(s) 链接 */
fun openInBrowser(context: android.content.Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, R.string.action_no_browser, Toast.LENGTH_SHORT).show()
    }
}
