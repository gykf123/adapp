package gay.fesui.adapp.ui.guestbook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import gay.fesui.adapp.R
import gay.fesui.adapp.data.model.GuestbookMessage
import gay.fesui.adapp.ui.components.EmptyState
import gay.fesui.adapp.ui.components.ErrorState
import gay.fesui.adapp.ui.components.GlassCard
import gay.fesui.adapp.ui.components.LoadingState
import gay.fesui.adapp.ui.home.SectionTitle

/**
 * Tab 4 留言墙：Supabase REST 读写。
 * 顶部发布表单，下方留言列表（时间倒序，最多 100 条）。
 */
@Composable
fun GuestbookScreen(
    viewModel: GuestbookViewModel,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            SectionTitle(stringResource(R.string.guestbook_title))
        }
        item {
            PublishFormCard(
                viewModel = viewModel,
            )
        }
        when (val state = viewModel.uiState) {
            is GuestbookUiState.Loading -> item { LoadingState(Modifier.height(240.dp)) }
            is GuestbookUiState.Failed -> item {
                ErrorState(
                    message = stringResource(R.string.guestbook_hint_disabled),
                    onRetry = viewModel::refresh,
                    modifier = Modifier.height(240.dp),
                )
            }
            is GuestbookUiState.Success -> {
                if (state.messages.isEmpty()) {
                    item { EmptyState(stringResource(R.string.guestbook_empty), Modifier.height(240.dp)) }
                } else {
                    items(state.messages) { message ->
                        MessageCard(message)
                    }
                }
            }
        }
    }
}

/** 发布表单：昵称（限 30 字）+ 内容（限 500 字，带计数）+ 发布按钮 */
@Composable
private fun PublishFormCard(viewModel: GuestbookViewModel) {
    val canPublish = viewModel.contentInput.trim().isNotEmpty() && !viewModel.posting

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            OutlinedTextField(
                value = viewModel.nicknameInput,
                onValueChange = viewModel::onNicknameChange,
                label = { Text(stringResource(R.string.guestbook_nickname_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = viewModel.contentInput,
                onValueChange = viewModel::onContentChange,
                label = { Text(stringResource(R.string.guestbook_content_label)) },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(
                        R.string.guestbook_counter,
                        viewModel.contentInput.length,
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = viewModel::publish,
                    enabled = canPublish,
                ) {
                    Text(
                        if (viewModel.posting) {
                            stringResource(R.string.guestbook_publishing)
                        } else {
                            stringResource(R.string.guestbook_publish)
                        }
                    )
                }
            }
            val error = viewModel.postError
            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${stringResource(R.string.guestbook_publish_failed)}：$error",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

/** 单条留言卡片 */
@Composable
private fun MessageCard(message: GuestbookMessage) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 首字母圆形小头像
                val initial = (message.nickname?.trim()?.firstOrNull() ?: '匿').toString()
                Text(
                    text = initial,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 9.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = message.nickname?.trim()?.ifEmpty { null }
                        ?: stringResource(R.string.guestbook_anonymous),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = formatMessageTime(message.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
