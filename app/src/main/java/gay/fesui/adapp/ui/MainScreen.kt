package gay.fesui.adapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import gay.fesui.adapp.R
import gay.fesui.adapp.ui.friends.FriendsScreen
import gay.fesui.adapp.ui.guestbook.GuestbookScreen
import gay.fesui.adapp.ui.guestbook.GuestbookViewModel
import gay.fesui.adapp.ui.home.HomeScreen
import gay.fesui.adapp.ui.projects.ProjectsScreen
import gay.fesui.adapp.ui.tools.ToolsScreen

/** 底部导航 Tab 定义：首页 / 项目 / 友链 / 留言 / 工具 */
private data class TabItem(val labelRes: Int, val icon: ImageVector)

private val Tabs = listOf(
    TabItem(R.string.tab_home, Icons.Filled.Home),
    TabItem(R.string.tab_projects, Icons.Filled.Build),
    TabItem(R.string.tab_friends, Icons.Filled.Person),
    TabItem(R.string.tab_guestbook, Icons.AutoMirrored.Filled.Send),
    TabItem(R.string.tab_tools, Icons.Filled.Settings),
)

/**
 * App 主框架：Scaffold + 底部 5 Tab。
 * 留言墙 ViewModel 提升到本层，切换 Tab 不丢失留言状态。
 */
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val guestbookViewModel: GuestbookViewModel = viewModel()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                Tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(stringResource(tab.labelRes)) },
                        alwaysShowLabel = true,
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (selectedTab) {
                0 -> HomeScreen()
                1 -> ProjectsScreen()
                2 -> FriendsScreen()
                3 -> GuestbookScreen(viewModel = guestbookViewModel)
                else -> ToolsScreen()
            }
        }
    }
}
