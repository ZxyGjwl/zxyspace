# ZXY博客网站开发步骤文档

## 项目概述

ZXY Space 是一个个人技术/娱乐博客网站，使用 Vue 3 和 Spring Boot 技术栈构建。本文档提供了项目开发的详细步骤指南，从环境搭建到功能实现，再到部署上线的全流程。

### 技术栈

#### 前端
- Vue 3 (组合式API)
- Vue Router
- Pinia 状态管理
- Element Plus UI组件库
- Vite 构建工具
- Axios HTTP请求库
- Marked (Markdown解析)
- Highlight.js (代码高亮)
- SCSS (样式预处理)

#### 后端
- Java 17
- Spring Boot 3.1.x
- Spring Data JPA
- Spring Security
- JWT 身份验证
- H2 数据库 (开发环境)
- MySQL 数据库 (生产环境)
- Maven 依赖管理
- Lombok 简化代码
- SpringDoc OpenAPI 接口文档

## 开发环境搭建

### 前提条件
- Node.js 16+
- JDK 17+
- Maven 3.8+
- MySQL 8.0+ (可选，开发可使用H2)
- IDE: VSCode (前端) / IntelliJ IDEA (后端)

### 第一步：克隆项目

```bash
git clone https://github.com/your-username/zxyspace.git
cd zxyspace
```

### 第二步：后端环境设置

1. 进入 backend 目录
```bash
cd backend
```

2. 运行 Maven 安装依赖
```bash
mvn clean install -DskipTests
```

3. 在 src/main/resources 目录下创建 application-dev.yml 配置文件
```yml
spring:
  datasource:
    url: jdbc:h2:mem:zxyspace
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console

# JWT配置
jwt:
  secret: your-secret-key-here
  expiration: 86400000 # 24小时
```

### 第三步：前端环境设置

1. 进入 frontend 目录
```bash
cd ../frontend
```

2. 安装依赖
```bash
npm install
```

## 开发步骤

### 第一阶段：后端开发

#### 步骤1：完善基础实体类

确保所有实体类都正确定义了关系和属性。

#### 步骤2：创建数据传输对象(DTO)

1. 创建 `UserDto.java`
```java
package com.zxyspace.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String avatar;
    private String role;
}
```

2. 创建 `PostDto.java`
```java
package com.zxyspace.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class PostDto {
    private Long id;
    private String title;
    private String excerpt;
    private String content;
    private String coverImage;
    private UserDto author;
    private CategoryDto category;
    private Set<TagDto> tags;
    private List<CommentDto> comments;
    private boolean published;
    private int views;
    private int likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

3. 创建其他必要的DTO类 (CategoryDto, TagDto, CommentDto 等)

#### 步骤3：实现服务层

1. 创建 `UserService.java` 和 `UserServiceImpl.java`
2. 创建 `PostService.java` 和 `PostServiceImpl.java`
3. 创建其他服务类 (CategoryService, TagService, CommentService 等)

#### 步骤4：实现控制器层

1. 创建 `AuthController.java` 处理认证相关请求
2. 创建 `UserController.java` 处理用户相关请求
3. 创建 `PostController.java` 处理博客文章相关请求
4. 创建其他控制器 (CategoryController, TagController, CommentController 等)

#### 步骤5：配置安全和异常处理

1. 实现 Spring Security 配置
2. 创建 JWT 认证过滤器和工具类
3. 实现全局异常处理

### 第二阶段：前端开发

#### 步骤1：配置路由和状态管理

1. 配置 Vue Router 路由
```javascript
// 更新 router/index.js 
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import BlogView from '../views/BlogView.vue'
import BlogDetailView from '../views/BlogDetailView.vue'
import AboutView from '../views/AboutView.vue'
import LoginView from '../views/auth/LoginView.vue'
import RegisterView from '../views/auth/RegisterView.vue'
import DashboardView from '../views/dashboard/DashboardView.vue'
import ProfileView from '../views/dashboard/ProfileView.vue'
import AdminPostsView from '../views/dashboard/AdminPostsView.vue'
import AdminCategoriesView from '../views/dashboard/AdminCategoriesView.vue'
import AdminTagsView from '../views/dashboard/AdminTagsView.vue'
import PostEditorView from '../views/dashboard/PostEditorView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/blog',
      name: 'blog',
      component: BlogView
    },
    {
      path: '/blog/:id',
      name: 'blog-detail',
      component: BlogDetailView
    },
    {
      path: '/about',
      name: 'about',
      component: AboutView
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardView,
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView,
      meta: { requiresAuth: true }
    },
    {
      path: '/dashboard/posts',
      name: 'admin-posts',
      component: AdminPostsView,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/dashboard/categories',
      name: 'admin-categories',
      component: AdminCategoriesView,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/dashboard/tags',
      name: 'admin-tags',
      component: AdminTagsView,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/dashboard/posts/new',
      name: 'post-create',
      component: PostEditorView,
      meta: { requiresAuth: true }
    },
    {
      path: '/dashboard/posts/edit/:id',
      name: 'post-edit',
      component: PostEditorView,
      meta: { requiresAuth: true }
    }
  ]
})

// 导航守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next({ name: 'home' })
  } else {
    next()
  }
})

export default router
```

2. 添加 API 服务类
```javascript
// 创建 services/api.js
import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      // Token 过期或无效
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api
```

#### 步骤2：创建认证相关页面

1. 创建登录页面 `views/auth/LoginView.vue`
2. 创建注册页面 `views/auth/RegisterView.vue`

#### 步骤3：创建博客相关页面

1. 完善首页 `views/HomeView.vue`
2. 完善博客列表页 `views/BlogView.vue`
3. 完善博客详情页 `views/BlogDetailView.vue`
4. 完善关于页面 `views/AboutView.vue`

#### 步骤4：创建管理后台页面

1. 创建控制面板 `views/dashboard/DashboardView.vue`
2. 创建用户资料页面 `views/dashboard/ProfileView.vue`
3. 创建文章管理页面 `views/dashboard/AdminPostsView.vue`
4. 创建文章编辑器 `views/dashboard/PostEditorView.vue`
5. 创建分类管理页面 `views/dashboard/AdminCategoriesView.vue`
6. 创建标签管理页面 `views/dashboard/AdminTagsView.vue`

### 第三阶段：功能实现和集成

#### 步骤1：用户认证功能

1. 实现登录表单及逻辑
2. 实现注册表单及逻辑
3. 实现注销功能
4. 实现用户资料修改功能

#### 步骤2：博客内容管理功能

1. 实现博客文章列表和筛选功能
2. 实现博客文章详情页及阅读功能
3. 实现博客文章点赞功能
4. 实现评论功能
5. 实现文章发布、编辑和删除功能

#### 步骤3：管理后台功能

1. 实现文章管理功能
2. 实现分类管理功能
3. 实现标签管理功能
4. 实现用户管理功能 (如果需要)

### 第四阶段：优化和测试

#### 步骤1：前端优化

1. 优化页面响应式设计
2. 实现懒加载和性能优化
3. 添加加载状态和错误处理
4. 实现主题切换功能 (可选)

#### 步骤2：后端优化

1. 优化数据库查询
2. 实现缓存机制
3. 优化异常处理

#### 步骤3：测试

1. 编写前端单元测试
2. 编写后端单元测试和集成测试
3. 进行端到端测试
4. 进行安全测试

### 第五阶段：部署和维护

#### 步骤1：前后端分离部署

1. 打包前端应用
```bash
cd frontend
npm run build
```

2. 打包后端应用
```bash
cd backend
mvn clean package -DskipTests
```

3. 配置生产环境
   - 为前端配置 Nginx 或其他静态文件服务器
   - 为后端配置生产数据库和环境变量

#### 步骤2：持续集成/持续部署 (CI/CD)

1. 配置 GitHub Actions 或其他 CI/CD 工具
2. 自动化测试和部署过程

#### 步骤3：监控和维护

1. 设置日志和监控系统
2. 建立定期备份机制
3. 计划定期维护和更新

## 扩展功能（后续开发）

1. **搜索功能增强**
   - 实现全文搜索
   - 添加高级筛选选项

2. **用户互动功能**
   - 用户关注功能
   - 私信系统
   - 文章收藏功能

3. **内容增强**
   - 添加图片画廊
   - 支持视频内容
   - 创建系列文章

4. **分析和统计**
   - 实现访问统计
   - 用户行为分析
   - 内容热度分析

5. **多语言支持**
   - 添加国际化功能
   - 支持多语言内容

## 技术架构图

```
+-------------------+      +-------------------+
|    前端应用        |      |    后端应用        |
|  (Vue 3 + Vite)   |<---->| (Spring Boot)     |
+-------------------+      +-------------------+
        |                            |
+-------------------+      +-------------------+
|  状态管理 (Pinia)   |      |   数据访问层       |
+-------------------+      |  (Spring Data)    |
        |                  +-------------------+
+-------------------+              |
|  UI组件 (Element+) |      +-------------------+
+-------------------+      |    数据库          |
                           | (MySQL/H2)        |
                           +-------------------+
```

## 目录结构

### 前端目录结构

```
frontend/
├── public/              # 静态资源
├── src/
│   ├── assets/          # 图片等资源
│   │   ├── common/      # 通用组件
│   │   ├── layout/      # 布局组件
│   │   └── blog/        # 博客相关组件
│   ├── router/          # 路由配置
│   ├── store/           # 状态管理
│   ├── services/        # API服务
│   ├── utils/           # 工具函数
│   ├── views/           # 页面
│   │   ├── auth/        # 认证相关页面
│   │   └── dashboard/   # 管理后台页面
│   ├── App.vue          # 根组件
│   └── main.js          # 入口文件
└── package.json         # 依赖配置
```

### 后端目录结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/zxyspace/
│   │   │       ├── config/        # 配置类
│   │   │       ├── controller/    # 控制器
│   │   │       ├── dto/           # 数据传输对象
│   │   │       ├── exception/     # 异常处理
│   │   │       ├── model/         # 实体类
│   │   │       ├── repository/    # 数据访问
│   │   │       ├── security/      # 安全相关
│   │   │       ├── service/       # 服务类
│   │   │       └── util/          # 工具类
│   │   └── resources/             # 资源文件
│   └── test/                      # 测试代码
└── pom.xml                        # Maven配置
```

## 结语

按照本文档的步骤进行开发，可以逐步构建出一个功能完整、性能优良的个人博客网站。开发过程中可以根据实际需求进行适当调整和扩展，最终构建出符合个人风格的技术/娱乐博客平台。 