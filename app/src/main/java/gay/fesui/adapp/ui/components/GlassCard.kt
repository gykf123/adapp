package gay.fesui.adapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 「液态玻璃」风格卡片：参考网页的轻盈观感——
 * 16-20dp 圆角、极淡阴影、充足留白，用半透明色阶代替 backdrop blur。
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    containerColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
    contentPadding: PaddingValues = PaddingValues(20.dp),
    elevation: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val base = modifier.clip(shape)
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = base,
            shape = shape,
            color = containerColor,
            shadowElevation = elevation,
        ) {
            Box(Modifier.padding(contentPadding)) { content() }
        }
    } else {
        Surface(
            modifier = base,
            shape = shape,
            color = containerColor,
            shadowElevation = elevation,
        ) {
            Box(Modifier.padding(contentPadding)) { content() }
        }
    }
}

/** 页面顶部大标题下方通用的内容留白容器 */
@Composable
fun PageBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
    ) { content() }
}

/** 小胶囊标签（首页 chips、项目 tags 等） */
@Composable
fun PillTag(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = containerColor,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
        )
    }
}
