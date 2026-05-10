# 课程笔记与复习助手（CourseReviewPlanner）

一款面向学生的学习管理 Android 应用，围绕“**学习计划—笔记记录—复习提醒**”形成闭环，帮助用户统一管理分散的课程信息，按计划学习、快速记录与整理知识点，并通过提醒机制提高复习执行力。

## 📱 项目简介

本项目是一款功能完整的学习管理 App，主要面向在校学生（大学生/研究生）及备考、自学人群。通过将学习计划、笔记管理与复习提醒整合在一个应用内，减少切换成本，提升学习效率。

### 解决的问题

学生常面临以下问题：
- **学习计划混乱**：课程多、任务多导致安排困难
- **笔记分散**：笔记碎片化难检索
- **复习不及时**：复习容易遗忘且缺少提醒

本项目提供：
- 计划管理、笔记管理与复习提醒的一体化入口
- 云同步功能降低数据丢失风险
- 多媒体笔记支持（文本、图片、录音、涂鸦）
- 智能复习提醒机制

## ✨ 核心功能

### 1. 用户认证与个人中心
- 手机号登录/注册（支持密码登录和验证码登录）
- 用户昵称管理
- 头像选择
- 云同步备份与恢复

### 2. 首页（Dashboard）
- **今日新闻**：拉取并轮播展示学习相关资讯
- **今日计划**：汇总当天学习事项（数量/时长/剩余时长）
- **今日剩余学习时间**：以分钟维度展示
- **上次打开笔记**：一键回到最近阅读/编辑内容
- **四大功能入口**：学习计划、笔记管理、复习提醒、使用说明

### 3. 学习计划管理
- 标签/分组管理（新建、重命名、删除）
- 学习计划 CRUD（新建、重命名、删除、列表展示）
- **计划详情页**：
  - 课程表模式：周视图时间格管理
  - 自定义事件模式：按日期添加学习任务
  - 学习压力分析：可视化图表展示学习负载
- **一键导入提醒**：从计划条目快速生成复习提醒
- 顶部刷新按钮：刷新当前列表/顺序

### 4. 笔记管理
- 标签/分组管理（新建、重命名、删除）
- 笔记 CRUD（新建、重命名、删除、列表展示）
- **笔记详情页（核心编辑能力）**：
  - **富文本编辑**：段落级样式（字体、字号、颜色、粗体、斜体、下划线）
  - **图片插入**：拍照/相册选择
  - **录音插入**：录制音频并嵌入笔记
  - **画笔涂鸦**：自由绘制辅助记忆
  - **知识点/批注**：结构化整理与知识导图
- 顶部刷新按钮：刷新当前列表/顺序

### 5. 复习提醒管理
- 标签/分组管理（新建、重命名、删除）
- 提醒 CRUD（新建、删除、启用/禁用、列表展示）
- **提醒详情页**：编辑提醒内容与触发时间
- **系统通知**：到点提醒弹出与跳转
- 顶部刷新按钮：刷新当前列表/顺序

### 6. 班级功能
- **加入班级**：用户可加入多个班级
- **班级计划发布**：管理员可通过 Web 管理端发布学习计划到班级
- **自动同步**：学生端自动拉取并导入班级发布的计划

### 7. 云同步
- 自动对比本地与云端数据量
- 智能决策上传或下载
- 支持跨设备数据恢复

## 🛠️ 技术栈

### Android 端
- **UI 框架**：Jetpack Compose + Material Design 3
- **架构模式**：MVVM（Model-View-ViewModel）
- **本地数据库**：Room Persistence Library
- **异步处理**：Kotlin Coroutines & Flow
- **数据存储**：
  - Room（计划/笔记/提醒等实体持久化）
  - DataStore Preferences（用户偏好、当前用户、加入的班级）
- **网络请求**：Java.net.URL + HttpURLConnection
- **JSON 解析**：org.json.JSONObject / JSONArray
- **系统能力**：
  - AlarmManager（复习提醒调度）
  - NotificationManagerCompat（通知展示）
  - MediaRecorder（录音功能）
  - ActivityResultContracts（相机/相册交互）

### 服务器端
- **运行环境**：Node.js
- **Web 框架**：Express
- **中间件**：
  - body-parser（JSON 请求体解析）
  - cors（跨域资源共享）
- **文件系统**：fs + path（JSON 文件持久化）
- **管理面板**：HTML + CSS + JavaScript（访问 `http://localhost:3000/admin`）

## 📁 项目结构

```
CourseReviewPlanner/
├── app/                          # Android 应用主目录
│   └── src/main/
│       ├── java/com/example/coursereviewplanner/
│       │   ├── MainActivity.kt              # 主界面入口
│       │   ├── HomeTabContent.kt            # 首页内容
│       │   ├── StudyPlanActivity.kt         # 学习计划管理页
│       │   ├── StudyPlanDetailActivity.kt   # 计划详情页
│       │   ├── NoteManagementActivity.kt    # 笔记管理页
│       │   ├── NoteDetailActivity.kt        # 笔记详情编辑页
│       │   ├── ReviewReminderActivity.kt    # 复习提醒管理页
│       │   ├── ReviewReminderDetailActivity.kt  # 提醒详情页
│       │   ├── StudyLoadAnalysisActivity.kt # 学习压力分析页
│       │   ├── ui/                          # UI 层
│       │   │   ├── auth/                   # 登录/注册
│       │   │   ├── studyplan/              # 计划相关 ViewModel
│       │   │   ├── note/                   # 笔记相关 ViewModel
│       │   │   ├── review/                 # 提醒相关 ViewModel
│       │   │   └── theme/                  # 主题配置
│       │   ├── data/                       # 数据层
│       │   │   ├── UserRepository.kt       # 用户数据仓库
│       │   │   ├── StudyPlanRepository.kt   # 计划数据仓库
│       │   │   ├── NoteRepository.kt       # 笔记数据仓库
│       │   │   ├── ReviewReminderRepository.kt  # 提醒数据仓库
│       │   │   ├── BackupRepository.kt     # 云同步仓库
│       │   │   └── local/                  # Room 数据库相关
│       │   └── util/                       # 工具类
│       │       ├── NewsApi.kt              # 新闻 API
│       │       ├── PasswordHasher.kt       # 密码哈希
│       │       ├── ReviewReminderScheduler.kt  # 提醒调度器
│       │       └── ReviewReminderAlarmReceiver.kt  # 闹钟接收器
│       └── res/                            # 资源文件
├── server/                      # Node.js 服务器
│   ├── index.js                # 服务器入口文件
│   ├── package.json            # 依赖配置
│   ├── backups/                # 用户备份存储目录
│   ├── class_plans/            # 班级计划存储目录
│   ├── timetables/             # 课表分享存储目录
│   ├── custom_schedules/       # 自定义计划分享存储目录
│   └── public/admin/           # 管理面板前端
│       ├── index.html          # 管理面板 HTML
│       ├── styles.css          # 管理面板样式
│       └── app.js               # 管理面板逻辑
├── build.gradle.kts            # 项目构建配置
├── settings.gradle.kts         # 项目设置
└── README.md                   # 本文件
```

## 🚀 快速开始

### 环境要求

#### Android 端
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 11 或更高版本
- Android SDK 24+（最低支持 Android 7.0）
- Gradle 8.0+

#### 服务器端
- Node.js 14+ 和 npm

### 安装步骤

#### 1. 克隆项目

```bash
git clone <repository-url>
cd CourseReviewPlanner
```

#### 2. 配置 Android 应用

1. 使用 Android Studio 打开项目
2. 等待 Gradle 同步完成
3. 连接 Android 设备或启动模拟器
4. 点击运行按钮或使用快捷键 `Shift+F10`

#### 3. 启动服务器

```bash
cd server
npm install
npm start
```

服务器将在 `http://localhost:3000` 启动。

#### 4. 配置服务器地址（可选）

如果服务器不在本地运行，需要修改 Android 应用中的服务器地址：

在 `app/src/main/java/com/example/coursereviewplanner/data/BackupRepository.kt` 中找到 `serverBaseUrl` 并修改为实际服务器地址。

### 访问管理面板

启动服务器后，在浏览器中访问：

```
http://localhost:3000/admin
```

管理面板功能：
- 查看服务器运行状态（用户备份数量、运行时间、磁盘占用等）
- 查看最近请求日志
- 查看短信验证码缓存状态
- **发布学习计划到班级**（支持多事件）
- 查看最新生成的验证码（调试用）

**注意**：管理面板需要设置 `ADMIN_TOKEN` 环境变量进行身份验证。

## 💡 核心创新点

### 1. 自建云服务器与云同步

- **技术实现**：Node.js + Express 构建轻量级 REST API
- **数据持久化**：JSON 文件直接存储，便于部署和查看
- **同步策略**：智能对比本地与云端数据量，自动决策上传或下载
- **代码位置**：
  - 服务器：`server/index.js`
  - 客户端：`app/src/main/java/com/example/coursereviewplanner/data/BackupRepository.kt`

### 2. 计划一键导入提醒

- **功能描述**：从学习计划条目快速生成复习提醒，减少重复输入
- **技术实现**：
  - 课程表模式：根据周几和节次自动计算提醒时间
  - 自定义事件模式：直接使用事件日期和时间
  - 默认禁用，需用户确认后开启
- **代码位置**：
  - UI：`app/src/main/java/com/example/coursereviewplanner/StudyPlanDetailActivity.kt`
  - 业务逻辑：`app/src/main/java/com/example/coursereviewplanner/ui/studyplan/StudyPlanDetailViewModel.kt`

### 3. 可视化图表学习压力分析

- **功能描述**：统计学习计划中的任务分布，以柱状图形式展示每日学习压力
- **技术实现**：
  - 使用 Jetpack Compose 自绘柱状图（无需第三方库）
  - 压力模型：以每天 6 小时学习为 100 压力进行线性缩放
  - 支持课程表模式和自定义事件模式
- **代码位置**：
  - UI：`app/src/main/java/com/example/coursereviewplanner/StudyLoadAnalysisActivity.kt`
  - 计算逻辑：`app/src/main/java/com/example/coursereviewplanner/ui/studyplan/StudyLoadAnalysisViewModel.kt`

### 4. 文本编辑器富文本功能

- **功能描述**：段落级富文本编辑，支持字体、字号、颜色、粗体、斜体、下划线等样式
- **技术实现**：
  - 段落块模型：每个文本块自带格式信息
  - 样式切换策略：切换前固化当前输入，避免覆盖旧文本样式
  - 双存储策略：纯文本（便于检索）+ JSON（完整还原）
  - 防抖自动保存（500ms）
- **代码位置**：
  - UI：`app/src/main/java/com/example/coursereviewplanner/NoteDetailActivity.kt`
  - 状态管理：`app/src/main/java/com/example/coursereviewplanner/ui/note/NoteDetailViewModel.kt`

## 📖 使用说明

### 首次使用

1. **注册/登录**：使用手机号注册账号，支持密码登录或验证码登录
2. **完善个人信息**：点击左上角头像，修改昵称和头像
3. **加入班级**（可选）：在侧边栏点击“加入班级”，输入班级名称

### 学习计划管理

1. 在首页点击“学习计划”或通过底部导航进入
2. 点击右上角“+”创建标签或学习计划
3. 进入计划详情页：
   - **课程表模式**：点击时间格添加课程信息
   - **自定义事件模式**：点击“添加事件”按日期添加学习任务
4. 点击“学习压力分析”查看学习负载可视化图表
5. 在时间格或事件中点击“导入提醒”快速生成复习提醒

### 笔记管理

1. 在首页点击“笔记管理”或通过底部导航进入
2. 点击右上角“+”创建标签或笔记
3. 进入笔记详情页进行编辑：
   - **文本编辑**：输入内容，使用工具栏切换样式
   - **插入图片**：点击图片按钮，选择拍照或相册
   - **插入录音**：点击录音按钮，开始录制
   - **画笔涂鸦**：点击画笔按钮，自由绘制
   - **添加批注**：选中文本后添加知识点批注

### 复习提醒管理

1. 在首页点击“复习提醒”或通过底部导航进入
2. 点击右上角“+”创建标签或提醒
3. 进入提醒详情页：
   - 设置提醒标题和内容
   - 选择提醒时间
   - 开启/关闭提醒开关
   - 保存后系统将自动调度闹钟

### 云同步

1. 点击左上角头像打开侧边栏
2. 点击“云同步”按钮
3. 系统自动对比本地与云端数据，提示同步结果

### 班级计划（管理员）

1. 访问 `http://localhost:3000/admin`
2. 在“发布学习计划到班级”区域：
   - 输入班级名称
   - 输入计划标题
   - 点击“新增事件”添加多个学习事件
   - 为每个事件设置日期、时间、标题和内容
   - 点击“发布到班级”
3. 学生端会自动拉取并导入班级计划

## 🔧 开发说明

### 架构设计

项目采用 **MVVM 架构模式**：

- **UI 层（Activity/Composable）**：负责界面展示和用户交互
- **ViewModel 层**：管理 UI 状态和业务逻辑
- **Repository 层**：封装数据访问（Room、DataStore、网络请求）
- **数据层**：Room 数据库、DataStore Preferences、网络 API

### 数据流

```
用户操作 → UI → ViewModel → Repository → DAO/DataStore/Network → 返回结果 → ViewModel 更新状态 → UI 自动重组
```

### 关键依赖

- `androidx.compose.*`：Jetpack Compose UI 框架
- `androidx.room.*`：Room 数据库
- `androidx.datastore.*`：DataStore Preferences
- `kotlinx.coroutines.*`：协程支持
- `coil-compose`：图片加载

## 📝 后续迭代方向

- [ ] 更智能的复习策略（间隔重复算法）
- [ ] 更强搜索与标签体系
- [ ] 知识图谱可视化
- [ ] 更细粒度的富文本编辑（行内样式）
- [ ] 多端同步策略优化
- [ ] 导出功能（PDF、Markdown）
- [ ] 数据统计与分析（学习时长、完成率等）

## 📄 许可证

本项目为课程项目，仅供学习交流使用。

## 👥 贡献

欢迎提交 Issue 和 Pull Request！

## 📧 联系方式

如有问题或建议，请通过 Issue 反馈。

---

**注意**：本项目为 Android 课程项目，部分功能（如短信验证码）在演示环境中可能使用模拟数据。生产环境使用前请进行充分测试。

