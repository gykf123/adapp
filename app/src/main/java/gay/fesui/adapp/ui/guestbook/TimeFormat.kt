package gay.fesui.adapp.ui.guestbook

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * 留言时间格式化：
 * - Supabase 返回 ISO 8601（UTC），先解析再按本地时区展示
 * - 7 天内显示友好相对时间，更早显示「MM-dd HH:mm」
 */
fun parseIso8601(iso: String?): Date? {
    if (iso.isNullOrBlank() || iso.length < 19) return null
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        parser.timeZone = TimeZone.getTimeZone("UTC")
        // 取前 19 位核心部分，忽略毫秒与时区后缀
        parser.parse(iso.substring(0, 19))
    } catch (e: Exception) {
        null
    }
}

/** 展示用时间文本：刚刚 / N 分钟前 / N 小时前 / N 天前 / MM-dd HH:mm */
fun formatMessageTime(iso: String?): String {
    val date = parseIso8601(iso) ?: return ""
    val diff = System.currentTimeMillis() - date.time
    val absolute = SimpleDateFormat("MM-dd HH:mm", Locale.US).format(date)
    return when {
        diff < 0 -> absolute
        diff < 60_000 -> "刚刚"
        diff < 3_600_000 -> "${diff / 60_000} 分钟前"
        diff < 86_400_000 -> "${diff / 3_600_000} 小时前"
        diff < 7 * 86_400_000 -> "${diff / 86_400_000} 天前"
        else -> absolute
    }
}
