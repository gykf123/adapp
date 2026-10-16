package gay.fesui.adapp.ui.guestbook

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gay.fesui.adapp.data.SupabaseService
import gay.fesui.adapp.data.model.GuestbookMessage
import kotlinx.coroutines.launch

/** 留言墙界面状态：加载中 / 成功（含空列表） / 拉取失败 */
sealed interface GuestbookUiState {
    data object Loading : GuestbookUiState
    data class Success(val messages: List<GuestbookMessage>) : GuestbookUiState
    data object Failed : GuestbookUiState
}

/**
 * 留言墙 ViewModel：负责 Supabase 留言的读取与发布。
 * App 端不做登录门禁，直接允许匿名发布（Supabase RLS 已允许 anon insert）。
 */
class GuestbookViewModel(
    private val service: SupabaseService = SupabaseService(),
) : ViewModel() {

    var uiState: GuestbookUiState by mutableStateOf(GuestbookUiState.Loading)
        private set

    /** 表单：昵称（限 30 字） */
    var nicknameInput by mutableStateOf("")
        private set

    /** 表单：内容（限 500 字） */
    var contentInput by mutableStateOf("")
        private set

    /** 是否正在发布 */
    var posting by mutableStateOf(false)
        private set

    /** 发布失败提示（成功或重新输入时清除） */
    var postError by mutableStateOf<String?>(null)
        private set

    init {
        refresh()
    }

    /** 重新拉取留言列表 */
    fun refresh() {
        uiState = GuestbookUiState.Loading
        viewModelScope.launch {
            uiState = try {
                GuestbookUiState.Success(service.fetchMessages())
            } catch (e: Exception) {
                GuestbookUiState.Failed
            }
        }
    }

    fun onNicknameChange(value: String) {
        if (value.length <= 30) nicknameInput = value
    }

    fun onContentChange(value: String) {
        if (value.length <= 500) contentInput = value
    }

    /** 发布留言：空内容忽略；成功后清空输入并刷新列表；失败给出错误提示 */
    fun publish() {
        val content = contentInput.trim()
        if (content.isEmpty() || posting) return
        posting = true
        postError = null
        viewModelScope.launch {
            try {
                service.postMessage(content, nicknameInput.trim().ifEmpty { null })
                nicknameInput = ""
                contentInput = ""
                uiState = GuestbookUiState.Success(service.fetchMessages())
            } catch (e: Exception) {
                postError = e.message ?: "发布失败"
            } finally {
                posting = false
            }
        }
    }
}
