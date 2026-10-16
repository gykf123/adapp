package gay.fesui.adapp.data

import gay.fesui.adapp.config.AppConfig

/**
 * 静态内容数据：文案与线上主页保持一致。
 * 数据与界面分离，后续要改文案只动这里。
 */

/** 首页 chips 标签 */
val HomeChips = listOf("代码", "红石", "自动化", "沙盒")

/** 「关于我」两段原文 */
val AboutParagraphs = listOf(
    "我在红石电路与代码逻辑的交界处游走——既会在 Minecraft 里用方块搭出真实的电路，" +
        "也会在终端前用脚本把重复劳动交给机器。不执拗，也不将就：" +
        "每一座方块、每一行代码，都该有它存在的道理。",
    "世界是开放的沙盒，而我乐此不疲地编辑它。",
)

/** 首页格言卡片 */
val Mottos = listOf(
    "在红石电路与代码逻辑的交界，我既是建筑师，也是工程师。",
    "不执拗，也不将就——每一座方块、每一行代码，都该有它的道理。",
    "世界是开放的沙盒，而我乐此不疲地编辑它。",
)

/** 项目卡片。href 为空表示「规划中」（与网页一致，角标标注） */
data class ProjectItem(
    val name: String,
    val desc: String,
    val tags: List<String>,
    val url: String? = null,
)

val Projects = listOf(
    ProjectItem(
        name = "红石自动化实验室",
        desc = "基于 Minecraft 红石与真实电路原理的自动化演示",
        tags = listOf("Minecraft"),
    ),
    ProjectItem(
        name = "终端工具箱",
        desc = "一组零依赖的命令行小工具",
        tags = listOf("CLI"),
    ),
    ProjectItem(
        name = "静态站点生成器",
        desc = "纯前端、无构建步骤的极简建站方案",
        tags = listOf("Web"),
    ),
    ProjectItem(
        name = "数据可视化",
        desc = "用原生 Canvas / SVG 讲故事",
        tags = listOf("Chart"),
    ),
    ProjectItem(
        name = "FKD 服务器主页",
        desc = "FKD 游戏服务器官方网站",
        tags = listOf("Minecraft", "服务器"),
        url = AppConfig.Urls.FKD_SERVER,
    ),
)

/** 友链卡片 */
data class FriendLink(
    val name: String,
    val desc: String,
    val url: String,
)

val FriendLinks = listOf(
    FriendLink(
        name = "白鹿原嚒",
        desc = "技术博客与开源项目分享，Minecraft 玩家的自留地。",
        url = "https://blym.top/",
    ),
    FriendLink(
        name = "MCJS",
        desc = "网页版 Minecraft 最佳中文版，无需下载即可在浏览器里游玩。",
        url = "https://mcjs.link",
    ),
)
