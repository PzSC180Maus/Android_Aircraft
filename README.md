# AircraftWar2026 Android 单机版

AircraftWar2026 是由 Java Swing 版飞机大战迁移而来的 Android 单机游戏。本项目保留了原游戏的飞机、子弹、道具及部分设计模式，并使用 Android 的绘图、触屏、音频和本地数据库实现平台适配。

## 功能

- 选择简单、普通或困难模式；普通和困难模式会随游戏时间提高难度，并在达到相应分数后出现 Boss。
- 拖动屏幕控制英雄机，自动发射子弹；击败敌机获得分数，拾取道具获得不同效果。
- 支持背景音乐、Boss 音乐和游戏音效，可在主菜单关闭，设置会保存在本机。
- 游戏结束后输入昵称并保存成绩，可选择查看排行榜、再来一局或返回菜单。
- 本地排行榜使用 SQLite 保存昵称、分数、难度和时间，按分数排序，支持删除记录。

## 环境与运行

- Android Studio，安装 Android SDK 36.1 和 Build-Tools 36.1.0。
- JDK 21；项目使用 Gradle Wrapper 9.3.1 和 Android Gradle Plugin 9.1.0。
- Android 11（API 30）或以上的真机或模拟器。

在 Android Studio 中打开仓库根目录，等待 Gradle 同步完成，选择设备后点击 **Run**。首次同步可能需要联网下载 Gradle 和依赖。`local.properties` 由本机 Android Studio 生成，用于指定本机 SDK 路径。

也可以在 Windows PowerShell 中构建：

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest
```

生成的调试安装包位于 `app/build/outputs/apk/debug/app-debug.apk`。

## 操作说明

1. 在主菜单选择难度，并按需打开或关闭音乐与音效。
2. 在游戏画面中拖动屏幕移动飞机，躲避敌机及敌方子弹。
3. 游戏结束后输入昵称；留空时使用默认昵称“玩家”。选择保存后查看排行榜、再来一局或返回菜单。
4. 从主菜单进入排行榜，可查看和删除本机保存的成绩。

## 主要结构

- `app/src/main/java/com/example/aircraft_android/`：主菜单、游戏页面和排行榜页面。
- `app/src/main/java/edu/hitsz/application/`：游戏循环、绘制与难度规则。
- `app/src/main/java/edu/hitsz/aircraft/`、`bullet/`、`prop/`：飞机、子弹和道具。
- `app/src/main/java/edu/hitsz/factory/`、`strategy/`、`observer/`：工厂、射击策略及道具效果相关接口与实现。
- `app/src/main/java/edu/hitsz/music/`：背景音乐和音效管理。
- `app/src/main/java/edu/hitsz/rank/`：成绩模型、DAO 与 SQLite 实现。
- `app/src/main/res/`：游戏图片、音频和界面资源。

## 数据与版本范围

排行榜数据保存在应用私有 SQLite 数据库中，仅供本机使用；卸载应用或清除应用数据会删除这些成绩。`main` 为单机版本，云端排行榜和双人联网对战尚未实现。`multiplayer` 分支为后续联机开发预留。

本项目仍在课程开发中。游戏循环目前按约 25 次/秒更新；排行榜的完整设备操作流程仍需要进一步验证。
