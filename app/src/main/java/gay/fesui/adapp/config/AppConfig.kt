package gay.fesui.adapp.config

/**
 * 全局配置对象：所有数据源（Supabase 配置、站点与工具 URL）集中在这里，方便后续修改。
 */
object AppConfig {

    /** FeSui 个人主页站点（punycode 域名） */
    const val SITE_URL = "https://fesui.xn--0iv.gay"

    /** 头像地址，加载失败时回退为首字母「F」占位 */
    const val AVATAR_URL = "https://fesui.xn--0iv.gay/avatar.png"

    /** 联系邮箱 */
    const val EMAIL = "fesui_a@sina.com"

    // ---------- Supabase（留言墙后端） ----------

    const val SUPABASE_URL = "https://yowwyasttkywyqgseuqz.supabase.co"
    const val SUPABASE_ANON_KEY = "sb_publishable_j4rpi2ejPTMGxo79uaaX3g_kQYrinS4"
    const val GUESTBOOK_TABLE = "guestbook"

    /** 留言读取上限 */
    const val GUESTBOOK_LIMIT = 100

    // ---------- 工具 / 外部链接 ----------

    object Urls {
        /** 矿石查找器（基岩版矿石分布预测） */
        const val ORE_FINDER = "https://fesui.xn--0iv.gay/tool/ore-finder.html"

        /** MC 搜索（minecraftsearch.com 功能复刻，9 款工具，WASM 本地运行） */
        const val MC_SEARCH = "https://fesui.xn--0iv.gay/tool/minecraft-search/index.html"

        /** FKD 服务器主页（「是.gay」中文域名，与 punycode xn--0iv.gay 等价） */
        const val FKD_SERVER = "https://FKD.是.gay"
    }
}
