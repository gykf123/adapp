package gay.fesui.adapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 留言墙中的一条留言。
 * 对应 Supabase 表 guestbook：id / content / nickname / created_at。
 */
@Serializable
data class GuestbookMessage(
    val id: Long? = null,
    val content: String = "",
    val nickname: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

/** 发布留言的请求体（nickname 可空，为空时展示为「匿名访客」） */
@Serializable
data class GuestbookPost(
    val content: String,
    val nickname: String? = null,
)
