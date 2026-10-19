# FeSui 个人主页 · 安卓客户端（adapp）

「FeSui 个人主页」的原生安卓客户端，Kotlin + Jetpack Compose 编写，后续还将承载 FKD 服务器相关内容。
App 内容与线上主页保持一致：首页、项目、友链、留言、工具五个 Tab。

## 功能列表

| Tab | 内容 |
| --- | --- |
| 首页 | 头像（网络加载，失败回退首字母「F」）、身份「极客人格 · Geek ／ Minecraft 玩家」、chips 标签（代码 / 红石 / 自动化 / 沙盒）、「关于我」原文两段、格言卡片 3 条、邮箱入口（点击调起邮件 App） |
| 项目 | 5 张项目卡片；无 href 的前 4 张带「规划中」角标，点击 toast 提示；「FKD 服务器主页」点击用浏览器打开 |
| 友链 | 白鹿原嚒、MCJS 两条友链，点击用浏览器打开 |
| 留言 | Supabase REST 留言墙：匿名发布（昵称限 30 字、内容限 500 字带计数）、时间倒序列表（最多 100 条）、友好相对时间、加载 / 空 / 失败三态处理，拉取失败提示「留言墙尚未开启，等待主人配置数据库。」 |
| 工具 | 🏰 FKD 服务器、⛏️ 矿石查找器、🧭 MC 搜索三个入口，均用内置全屏 WebView 打开（JS 与 DOM Storage 开启、加载进度条、返回键回退历史） |

- Material3 主题，品牌主色 `#4F7CFF`（深色模式 `#8FB2FF`），跟随系统深浅色；
- 「液态玻璃」卡片风格：16-20dp 圆角、淡阴影、半透明色阶；
- 图标使用 Material Icons 内置集（未引 material-icons-extended）。

## 技术栈

| 组件 | 版本 |
| --- | --- |
| AGP（Android Gradle Plugin） | 8.5.2 |
| Kotlin | 2.0.20（Compose Compiler 插件内置于 Kotlin 2.0） |
| Gradle Wrapper | 8.7 |
| Compose BOM | 2024.06.00 |
| compileSdk / targetSdk / minSdk | 34 / 34 / 24 |
| OkHttp | 4.12.0 |
| kotlinx-serialization-json | 1.7.1 |
| Coil | 2.7.0（头像加载） |
| JDK（构建） | 17+（本仓库在 JDK 20 上验证通过） |

## 目录结构

```
adapp/
├── settings.gradle.kts          # 仓库与模块配置
├── build.gradle.kts             # 根构建脚本（插件版本）
├── gradle.properties
├── gradlew / gradlew.bat        # Gradle Wrapper 8.7
├── gradle/wrapper/              # wrapper jar + properties（已提交）
├── apk/                         # 随仓库分发的 Debug APK
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/gay/fesui/adapp/
        │   ├── MainActivity.kt          # 入口 Activity
        │   ├── config/AppConfig.kt      # 所有数据源常量（Supabase、URL 等）
        │   ├── data/
        │   │   ├── SupabaseService.kt   # Supabase REST 客户端
        │   │   ├── StaticContent.kt     # 项目 / 友链 / 格言等静态内容
        │   │   └── model/Guestbook.kt   # 留言模型（kotlinx.serialization）
        │   └── ui/
        │       ├── MainScreen.kt        # Scaffold + 底部 5 Tab
        │       ├── components/          # 玻璃卡片、加载/空/错误三态
        │       ├── theme/               # 品牌色与 Material3 主题
        │       ├── home/                # Tab 1 首页
        │       ├── projects/            # Tab 2 项目
        │       ├── friends/             # Tab 3 友链
        │       ├── guestbook/           # Tab 4 留言墙（ViewModel + 界面 + 时间格式化）
        │       └── tools/               # Tab 5 工具 + WebViewActivity
        └── res/                         # 字符串 / 主题 / 启动图标（纯 vector + adaptive）
```

## 本地构建

### 1. 准备 Android SDK

需要 Android SDK（commandline-tools、platform-tools、platforms;android-34、build-tools;34.0.0）：

```bash
# 下载并解压 commandline-tools（linux 最新版）
mkdir -p /root/android-sdk/cmdline-tools
cd /tmp && curl -fL -o cmdline-tools.zip \
  https://dl.google.com/android/repository/commandlinetools-linux-13114758_latest.zip
unzip -q cmdline-tools.zip
mkdir -p /root/android-sdk/cmdline-tools/latest
cp -r cmdline-tools/. /root/android-sdk/cmdline-tools/latest/

# 接受许可并安装组件
export ANDROID_HOME=/root/android-sdk
yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --sdk_root=$ANDROID_HOME --licenses
$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --sdk_root=$ANDROID_HOME \
  "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

然后在项目根目录创建 `local.properties`（已被 .gitignore 忽略）：

```properties
sdk.dir=/root/android-sdk
```

或者设置环境变量 `ANDROID_HOME`。

### 2. 构建

```bash
# Debug APK
./gradlew assembleDebug
```

产物位于 `app/build/outputs/apk/debug/app-debug.apk`。

环境要求：JDK 17 或更高版本；Gradle 8.7 由 wrapper 自动下载，无需单独安装。

## APK 获取

- **CI 自动构建（推荐）**：每次推送 `main` 后，GitHub Actions 自动编 Debug APK。打开仓库 **Actions → Build APK → 最新一次运行 → 页面底部 Artifacts**，下载 `FeSui-adapp-apk-<commit>`（保留 30 天）；
- 仓库内 `apk/FeSui-adapp-debug.apk` 为随仓库分发的 Debug 包，也可直接下载安装（debug 签名，包名 `gay.fesui.adapp.debug`）；
- 打 `v*` 标签（如 `v1.0.0`）会额外生成 GitHub Release 并附上 APK；
- 也可以自行构建（见上文）。

## 说明

- 留言墙后端为 Supabase，使用可公开的 anon（publishable）key，App 端不做登录门禁，RLS 已允许匿名 insert；
- 配置项（Supabase 地址与密钥、头像与工具 URL）集中在 `app/src/main/java/gay/fesui/adapp/config/AppConfig.kt`，后续调整只改这一处；
- 「是.gay」为中文 IDN 域名，与 punycode `xn--0iv.gay` 等价。
