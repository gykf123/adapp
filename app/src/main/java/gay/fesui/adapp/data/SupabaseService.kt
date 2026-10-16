package gay.fesui.adapp.data

import gay.fesui.adapp.config.AppConfig
import gay.fesui.adapp.data.model.GuestbookMessage
import gay.fesui.adapp.data.model.GuestbookPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Supabase REST 客户端：负责留言墙的读取与发布。
 * 使用 anon（publishable）key，密钥可公开，App 端不做登录门禁。
 */
class SupabaseService(
    private val client: OkHttpClient = defaultClient(),
) {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    /** 拉取留言列表（按创建时间倒序，最多 [AppConfig.GUESTBOOK_LIMIT] 条） */
    suspend fun fetchMessages(): List<GuestbookMessage> = withContext(Dispatchers.IO) {
        val url = "${AppConfig.SUPABASE_URL}/rest/v1/${AppConfig.GUESTBOOK_TABLE}" +
            "?select=*&order=created_at.desc&limit=${AppConfig.GUESTBOOK_LIMIT}"
        val request = Request.Builder()
            .url(url)
            .header("apikey", AppConfig.SUPABASE_ANON_KEY)
            .header("Authorization", "Bearer ${AppConfig.SUPABASE_ANON_KEY}")
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IOException("读取留言失败：HTTP ${response.code} ${body.take(200)}")
            }
            json.decodeFromString<List<GuestbookMessage>>(body)
        }
    }

    /** 发布一条留言；nickname 为空时传 null，由界面展示为「匿名访客」 */
    suspend fun postMessage(content: String, nickname: String?): Unit =
        withContext(Dispatchers.IO) {
            val payload = json.encodeToString(
                GuestbookPost.serializer(),
                GuestbookPost(content = content.trim(), nickname = nickname?.trim()?.ifEmpty { null }),
            )
            val request = Request.Builder()
                .url("${AppConfig.SUPABASE_URL}/rest/v1/${AppConfig.GUESTBOOK_TABLE}")
                .header("apikey", AppConfig.SUPABASE_ANON_KEY)
                .header("Authorization", "Bearer ${AppConfig.SUPABASE_ANON_KEY}")
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .post(payload.toRequestBody(JSON_MEDIA_TYPE))
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val body = response.body?.string().orEmpty()
                    throw IOException("发布失败：HTTP ${response.code} ${body.take(200)}")
                }
            }
        }

    companion object {
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

        private fun defaultClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
}
