<template>
  <div class="tech-stack" :class="{ 'fade-in': isVisible, 'transition-mode': showStripeTransition }">
    <!-- 黄色条带进入过渡效果 -->
    <div v-if="showStripeEnter" class="stripe-enter-transition">
      <div class="enter-stripe" v-for="n in 6" :key="n"></div>
    </div>
    
    <!-- 黄色条带过渡效果 -->
    <div class="stripe-transition" 
         :class="{ 'revealing': showStripeReveal }"
         v-if="showStripeTransition">
      <div 
        v-for="i in 6" 
        :key="i" 
        class="stripe"
      ></div>
    </div>
    
    <!-- 主要内容 -->
    <div class="main-content">
      <!-- 左上角Zxy space文本图标 -->
      <AppLogo routeTo="/home" :noMargin="true" />
      
      <!-- 固定在中心的可爱圆脸 -->
      <div class="center-face fixed-center" :class="{ 'bounce-in': isVisible, 'shrink-left': showTechDetail }">
        <div class="face-container" :style="{ transform: faceContainerTransform }">
          <!-- 脸部主体 -->
          <div class="face-main">
            <!-- 左眼 -->
            <div class="eye left-eye">
              <div class="eye-white">
                <div class="eyeball" :style="{ transform: leftEyeTransform }">
                  <div class="eye-shine"></div>
                </div>
              </div>
            </div>
            <!-- 右眼 -->
            <div class="eye right-eye">
              <div class="eye-white">
                <div class="eyeball" :style="{ transform: rightEyeTransform }">
                  <div class="eye-shine"></div>
                </div>
              </div>
            </div>
            <!-- 鼻子 -->
            <div class="nose"></div>
            <!-- 嘴巴 -->
            <div class="mouth" :style="{ transform: mouthTransform }"></div>
            <!-- 腮红 -->
            <div class="blush left-blush"></div>
            <div class="blush right-blush"></div>
          </div>
        </div>
        <div class="collision-zone"></div>
      </div>
      
      <!-- 无限拖拽背景容器 -->
      <div 
        class="infinite-grid-container"
        @mousedown="startDrag"
        @touchstart="startDrag"
        ref="gridContainer"
      >
        <!-- 动态渲染的技术标签网格 -->
        <div 
          class="tech-grid-infinite"
          :style="{ transform: `translate(${dragOffset.x}px, ${dragOffset.y}px)` }"
        >
          <div 
            v-for="(tech, index) in visibleTechItems" 
            :key="`${tech.name}-${tech.gridX}-${tech.gridY}`"
            class="tech-item" 
            :class="{ 
              'selected-item': selectedTech && selectedTech.name === tech.name && selectedTech.gridX === tech.gridX && selectedTech.gridY === tech.gridY,
              'fade-out-others': showTechDetail && !(selectedTech && selectedTech.name === tech.name && selectedTech.gridX === tech.gridX && selectedTech.gridY === tech.gridY)
            }"
            :style="{ 
              '--grid-x': tech.gridX,
              '--grid-y': tech.gridY
            }"
            @click="handleTechClick(tech, $event)"
          >
            <div class="tech-badge-large" :class="tech.class">
              <div class="tech-name">{{ tech.name }}</div>
              <div class="tech-category">{{ tech.category }}</div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 选中的技术标签（用于动画过渡，独立于overlay） -->
                <div 
            v-if="selectedTech" 
            class="selected-tech-badge"
            :class="{ 
              'moving-to-detail': showTechDetail,
              'moving-back': isClosingDetail,
              'initial-state': !showTechDetail && !isClosingDetail
            }"
            :style="{
              '--start-x': selectedTechPosition.x + 'px',
              '--start-y': selectedTechPosition.y + 'px'
            }"
          >
        <div class="tech-badge-large" :class="selectedTech.class">
          <div class="tech-name">{{ selectedTech.name }}</div>
          <div class="tech-category">{{ selectedTech.category }}</div>
        </div>
      </div>

      <!-- 技术详情页面 -->
      <div 
        class="tech-detail-overlay" 
        :class="{ 'active': showTechDetail }"
        @click="closeTechDetail"
      >
        <!-- 详情内容 -->
        <div 
          class="tech-detail-content"
          :class="{ 'visible': showTechDetail }"
          v-if="selectedTech"
          @click.stop
        >
          <div class="detail-header">
            <h2 class="detail-title">{{ selectedTech.name }}</h2>
            <span class="detail-category">{{ selectedTech.category }}</span>
          </div>
          
          <div class="detail-body">
            <div class="detail-section">
              <h3>技术介绍</h3>
              <p>{{ getTechDescription(selectedTech.name) }}</p>
            </div>
            
            <div class="detail-section">
              <h3>主要特点</h3>
              <ul class="feature-list">
                <li v-for="feature in getTechFeatures(selectedTech.name)" :key="feature">
                  {{ feature }}
                </li>
              </ul>
            </div>
            
            <div class="detail-section">
              <h3>应用场景</h3>
              <div class="usage-tags">
                <span 
                  v-for="usage in getTechUsage(selectedTech.name)" 
                  :key="usage"
                  class="usage-tag"
                >
                  {{ usage }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 拖拽提示 -->
      <div class="drag-hint" :class="{ 'show': !isDragging && isVisible }">
        <div class="hint-text">🖱️ 拖拽探索无限技术宇宙</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted, watch, computed } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import AppLogo from '../components/common/AppLogo.vue'

const route = useRoute()
const router = useRouter()
const isVisible = ref(false)
const showStripeReveal = ref(false)
const showStripeTransition = ref(false)
const showStripeEnter = ref(false)
const gridContainer = ref(null)

// 拖拽相关状态
const isDragging = ref(false)
const dragOffset = ref({ x: 0, y: 0 })
const dragStart = ref({ x: 0, y: 0 })
const dragPending = ref(false) // 添加拖拽更新标志
const lastUpdateTime = ref(0) // 添加时间戳用于防抖

// 鼠标跟踪相关状态
const mousePosition = ref({ x: 0, y: 0 })
const facePosition = ref({ x: 0, y: 0 })

// 技术详情相关状态
const showTechDetail = ref(false)
const selectedTech = ref(null)
const selectedTechPosition = ref({ x: 0, y: 0 })
const isClosingDetail = ref(false)

// 网格配置
const GRID_SIZE_X = 500 // 增加网格横向单元大小
const GRID_SIZE_Y = 380 // 增加网格纵向单元大小
const RENDER_BUFFER = 8 // 增加渲染缓冲区（超出屏幕的网格数量），确保无限拖拽时有足够标签

// 技术数据库
const techDatabase = [
  // 前端技术
  { name: 'Vue 3', category: '前端框架', class: 'vue' },
  { name: 'React', category: '前端框架', class: 'react' },
  { name: 'Angular', category: '前端框架', class: 'angular' },
  { name: 'TypeScript', category: '编程语言', class: 'typescript' },
  { name: 'JavaScript', category: '编程语言', class: 'javascript' },
  { name: 'HTML5', category: '标记语言', class: 'html' },
  { name: 'CSS3', category: '样式语言', class: 'css' },
  { name: 'Sass', category: '预处理器', class: 'sass' },
  { name: 'Vite', category: '构建工具', class: 'vite' },
  { name: 'Webpack', category: '构建工具', class: 'webpack' },
  { name: 'Tailwind', category: 'CSS框架', class: 'tailwind' },
  { name: 'Bootstrap', category: 'CSS框架', class: 'bootstrap' },
  
  // 后端技术
  { name: 'Java', category: '编程语言', class: 'java' },
  { name: 'Spring Boot', category: '后端框架', class: 'spring' },
  { name: 'Python', category: '编程语言', class: 'python' },
  { name: 'Django', category: '后端框架', class: 'django' },
  { name: 'FastAPI', category: '后端框架', class: 'fastapi' },
  { name: 'Node.js', category: '运行时', class: 'nodejs' },
  { name: 'Express', category: '后端框架', class: 'express' },
  { name: 'NestJS', category: '后端框架', class: 'nestjs' },
  { name: 'Go', category: '编程语言', class: 'golang' },
  { name: 'Rust', category: '编程语言', class: 'rust' },
  { name: 'C++', category: '编程语言', class: 'cpp' },
  { name: 'C#', category: '编程语言', class: 'csharp' },
  
  // 数据库技术
  { name: 'MySQL', category: '关系型数据库', class: 'mysql' },
  { name: 'PostgreSQL', category: '关系型数据库', class: 'postgresql' },
  { name: 'MongoDB', category: 'NoSQL数据库', class: 'mongodb' },
  { name: 'Redis', category: '缓存数据库', class: 'redis' },
  { name: 'Elasticsearch', category: '搜索引擎', class: 'elasticsearch' },
  { name: 'SQLite', category: '轻量数据库', class: 'sqlite' },
  
  // 云计算与部署
  { name: 'Docker', category: '容器技术', class: 'docker' },
  { name: 'Kubernetes', category: '容器编排', class: 'kubernetes' },
  { name: 'AWS', category: '云服务', class: 'aws' },
  { name: 'Azure', category: '云服务', class: 'azure' },
  { name: 'GCP', category: '云服务', class: 'gcp' },
  { name: 'Nginx', category: 'Web服务器', class: 'nginx' },
  { name: 'Apache', category: 'Web服务器', class: 'apache' },
  { name: 'Jenkins', category: 'CI/CD', class: 'jenkins' },
  { name: 'GitLab CI', category: 'CI/CD', class: 'gitlab' },
  
  // AI/大模型技术
  { name: 'Claude', category: 'AI大模型', class: 'ai-badge' },
  { name: 'GPT-4o', category: 'AI大模型', class: 'ai-badge' },
  { name: 'Deepseek', category: 'AI大模型', class: 'ai-badge' },
  { name: 'Qwen', category: 'AI大模型', class: 'ai-badge' },
  { name: 'Gemini', category: 'AI大模型', class: 'ai-badge' },
  { name: 'LLaMA', category: 'AI大模型', class: 'ai-badge' },
  { name: 'RAG', category: 'AI技术', class: 'ai-badge' },
  { name: 'LoRA微调', category: 'AI技术', class: 'ai-badge' },
  { name: 'MCP', category: 'AI协议', class: 'ai-badge' },
  { name: 'TensorFlow', category: 'AI框架', class: 'tensorflow' },
  { name: 'PyTorch', category: 'AI框架', class: 'pytorch' },
  
  // 工具链
  { name: 'Git', category: '版本控制', class: 'git' },
  { name: 'GitHub', category: '代码托管', class: 'github' },
  { name: 'GitLab', category: '代码托管', class: 'gitlab' },
  { name: 'Cursor', category: '代码编辑器', class: 'cursor' },
  { name: 'VS Code', category: '代码编辑器', class: 'vscode' },
  { name: 'WebStorm', category: '代码编辑器', class: 'webstorm' },
  { name: 'Postman', category: 'API测试', class: 'postman' },
  { name: 'Figma', category: '设计工具', class: 'figma' }
]

// 计算当前视口需要渲染的网格范围
const viewportGrid = computed(() => {
  if (!gridContainer.value) return { minX: 0, maxX: 0, minY: 0, maxY: 0 }
  
  // 简化计算：直接基于拖拽偏移计算中心网格位置
  const centerGridX = Math.round(-dragOffset.value.x / GRID_SIZE_X)
  const centerGridY = Math.round(-dragOffset.value.y / GRID_SIZE_Y)
  
  // 优化渲染区域大小：每个方向渲染6个网格（总共12x12=144个标签）
  const RENDER_SIZE = 6
  
  const minX = centerGridX - RENDER_SIZE
  const maxX = centerGridX + RENDER_SIZE
  const minY = centerGridY - RENDER_SIZE
  const maxY = centerGridY + RENDER_SIZE
  
  return { minX, maxX, minY, maxY }
})

// 动态生成当前视口的技术标签
const visibleTechItems = computed(() => {
  const items = []
  const { minX, maxX, minY, maxY } = viewportGrid.value
  
  // 确保循环范围有效
  if (maxX <= minX || maxY <= minY) {
    console.warn('Invalid grid range:', { minX, maxX, minY, maxY })
    return items
  }
  
  // 生成网格内的技术标签
  for (let x = minX; x <= maxX; x++) {
    for (let y = minY; y <= maxY; y++) {
      // 使用稳定的哈希函数生成技术标签索引
      const seed = ((x & 0xFFFF) << 16) | (y & 0xFFFF)
      const techIndex = Math.abs(seed * 2654435761) % techDatabase.length
      const tech = techDatabase[techIndex]
      
      const item = {
        ...tech,
        gridX: x,
        gridY: y
      }
      
      items.push(item)
    }
  }
  
  return items
})

// 计算眼球的跟随位置
const leftEyeTransform = computed(() => {
  const deltaX = mousePosition.value.x - facePosition.value.x
  const deltaY = mousePosition.value.y - facePosition.value.y
  const distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY)
  const maxDistance = 12 // 眼球最大移动距离
  
  const eyeX = distance > 0 ? (deltaX / distance) * Math.min(distance / 15, maxDistance) : 0
  const eyeY = distance > 0 ? (deltaY / distance) * Math.min(distance / 15, maxDistance) : 0
  
  return `translate(calc(-50% + ${eyeX}px), calc(-50% + ${eyeY}px))`
})

const rightEyeTransform = computed(() => {
  const deltaX = mousePosition.value.x - facePosition.value.x
  const deltaY = mousePosition.value.y - facePosition.value.y
  const distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY)
  const maxDistance = 12 // 眼球最大移动距离
  
  const eyeX = distance > 0 ? (deltaX / distance) * Math.min(distance / 15, maxDistance) : 0
  const eyeY = distance > 0 ? (deltaY / distance) * Math.min(distance / 15, maxDistance) : 0
  
  return `translate(calc(-50% + ${eyeX}px), calc(-50% + ${eyeY}px))`
})

// 计算嘴巴的跟随位置
const mouthTransform = computed(() => {
  const deltaX = mousePosition.value.x - facePosition.value.x
  const deltaY = mousePosition.value.y - facePosition.value.y
  const distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY)
  const maxDistance = 8 // 嘴巴最大移动距离，比眼球小一些
  
  const mouthX = distance > 0 ? (deltaX / distance) * Math.min(distance / 20, maxDistance) : 0
  const mouthY = distance > 0 ? (deltaY / distance) * Math.min(distance / 20, maxDistance) : 0
  
  return `translate(calc(-50% + ${mouthX}px), ${mouthY}px)`
})

// 计算脸部容器的转向跟随位置
const faceContainerTransform = computed(() => {
  const deltaX = mousePosition.value.x - facePosition.value.x
  const deltaY = mousePosition.value.y - facePosition.value.y
  const distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY)
  
  // 限制转向角度范围，避免过度转向
  const maxRotateX = 8 // 最大X轴旋转角度
  const maxRotateY = 8 // 最大Y轴旋转角度
  const maxTranslate = 6 // 最大移动距离
  
  // 根据鼠标距离中心的位置计算旋转角度
  const rotateY = distance > 0 ? (deltaX / distance) * Math.min(distance / 30, maxRotateY) : 0
  const rotateX = distance > 0 ? -(deltaY / distance) * Math.min(distance / 30, maxRotateX) : 0
  
  // 轻微的位移跟随效果
  const translateX = distance > 0 ? (deltaX / distance) * Math.min(distance / 50, maxTranslate) : 0
  const translateY = distance > 0 ? (deltaY / distance) * Math.min(distance / 50, maxTranslate) : 0
  
  return `perspective(500px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) translateX(${translateX}px) translateY(${translateY}px)`
})

// 技术详情数据获取方法
const getTechDescription = (techName) => {
  const descriptions = {
    'Vue 3': 'Vue 3是现代化的渐进式JavaScript框架，专为构建用户界面而设计。它采用响应式数据绑定和组合式API，提供了出色的开发体验和性能优化。',
    'React': 'React是Facebook开发的用于构建用户界面的JavaScript库，采用组件化开发模式，拥有庞大的生态系统和活跃的社区支持。',
    'Angular': 'Angular是Google开发的全功能前端框架，提供完整的开发解决方案，包括依赖注入、路由、表单处理等企业级功能。',
    'TypeScript': 'TypeScript是JavaScript的超集，添加了静态类型检查功能，提供更好的代码智能提示、重构和错误检测能力。',
    'JavaScript': 'JavaScript是动态编程语言，是Web开发的核心技术之一，支持多种编程范式，具有灵活性和广泛的应用场景。',
    'HTML5': 'HTML5是超文本标记语言的第五个主要版本，引入了语义化标签、多媒体支持和离线应用等现代Web功能。',
    'CSS3': 'CSS3是层叠样式表的最新版本，支持动画、转换、网格布局等高级特性，让Web页面更加美观和交互。',
    'Sass': 'Sass是CSS预处理器，提供变量、嵌套、混合等功能，让CSS编写更加高效和可维护。',
    'Vite': 'Vite是下一代前端构建工具，基于ES模块提供极快的开发服务器和优化的生产构建。',
    'Webpack': 'Webpack是模块打包器，将现代JavaScript应用程序的所有资源打包成一个或多个bundle。',
    'Tailwind': 'Tailwind CSS是实用优先的CSS框架，通过组合低级工具类来构建自定义设计。',
    'Bootstrap': 'Bootstrap是流行的CSS框架，提供响应式网格系统和预构建组件，快速构建现代Web界面。',
    'Node.js': 'Node.js是基于Chrome V8引擎的JavaScript运行时环境，使JavaScript能够在服务器端运行，广泛用于后端开发。',
    'Express': 'Express是Node.js的极简Web应用框架，提供强大的功能集合来创建Web和移动应用程序。',
    'NestJS': 'NestJS是构建高效且可扩展的Node.js服务器端应用程序的框架，深受Angular启发。',
    'Java': 'Java是面向对象的编程语言，具有"一次编写，到处运行"的特性，广泛用于企业级应用开发。',
    'Spring Boot': 'Spring Boot是基于Spring框架的快速应用开发框架，简化了Spring应用的初始搭建和开发过程。',
    'Python': 'Python是一种高级编程语言，以其简洁清晰的语法和强大的功能库而闻名，广泛应用于数据科学、机器学习和Web开发。',
    'Django': 'Django是高级Python Web框架，鼓励快速开发和干净、实用的设计，遵循DRY原则。',
    'FastAPI': 'FastAPI是现代、快速的Web框架，用于构建APIs，基于Python类型提示，具有自动API文档生成功能。',
    'Go': 'Go是Google开发的开源编程语言，以其简洁、高效和并发处理能力而著称。',
    'Rust': 'Rust是系统编程语言，专注于安全、速度和并发，无需垃圾回收器即可保证内存安全。',
    'C++': 'C++是通用编程语言，支持面向对象、泛型和过程化编程，广泛用于系统软件和游戏开发。',
    'C#': 'C#是Microsoft开发的面向对象编程语言，运行在.NET平台上，用于构建各种类型的应用程序。',
    'MySQL': 'MySQL是开源关系型数据库管理系统，以其高性能、可靠性和易用性而广受欢迎。',
    'PostgreSQL': 'PostgreSQL是功能强大的开源关系型数据库，支持SQL和JSON查询，具有高级特性和扩展性。',
    'MongoDB': 'MongoDB是面向文档的NoSQL数据库，使用JSON样式的文档存储数据，具有高度的灵活性。',
    'Redis': 'Redis是内存数据结构存储系统，可用作数据库、缓存和消息代理，支持多种数据类型。',
    'Elasticsearch': 'Elasticsearch是分布式搜索和分析引擎，基于Lucene构建，提供实时搜索和数据分析功能。',
    'SQLite': 'SQLite是轻量级的嵌入式关系型数据库，无需服务器，广泛用于移动应用和小型项目。',
    'Docker': 'Docker是开源的容器化平台，通过将应用程序及其依赖项打包到轻量级容器中，实现了一致的部署和运行环境。',
    'Kubernetes': 'Kubernetes是容器编排平台，自动化容器化应用程序的部署、扩展和管理。',
    'AWS': 'Amazon Web Services是亚马逊提供的云计算平台，提供计算、存储、数据库等200多种云服务，是全球领先的云服务提供商。',
    'Azure': 'Microsoft Azure是微软的云计算平台，提供全面的云服务，包括计算、分析、存储和网络功能。',
    'GCP': 'Google Cloud Platform是谷歌的云计算服务平台，提供机器学习、数据分析和计算服务。',
    'Nginx': 'Nginx是高性能的HTTP和反向代理服务器，也是IMAP/POP3代理服务器，以其稳定性和低资源消耗著称。',
    'Apache': 'Apache HTTP Server是开源的跨平台Web服务器软件，是世界上最流行的Web服务器之一。',
    'Jenkins': 'Jenkins是开源的自动化服务器，用于持续集成和持续部署，支持构建、测试和部署应用程序。',
    'GitLab CI': 'GitLab CI/CD是GitLab内置的持续集成和部署工具，提供完整的DevOps生命周期管理。',
    'Claude': 'Claude是Anthropic开发的AI大模型，具有强大的对话能力、代码理解和推理能力，专注于安全、有用和诚实的AI助手。',
    'GPT-4o': 'GPT-4o是OpenAI开发的多模态大语言模型，能够处理文本、图像、音频等多种输入，具有出色的理解和生成能力。',
    'Deepseek': 'DeepSeek是国内领先的AI大模型，在代码生成和数学推理方面表现出色，支持多种编程语言。',
    'Qwen': 'Qwen（千问）是阿里巴巴开发的大语言模型，具有强大的中文理解能力和多模态处理功能。',
    'Gemini': 'Gemini是Google开发的多模态AI模型，能够理解和生成文本、图像、音频和视频内容。',
    'LLaMA': 'LLaMA是Meta开发的大语言模型家族，以其高效的架构和强大的性能在开源社区广受欢迎。',
    'RAG': 'RAG（检索增强生成）是结合信息检索和生成式AI的技术，通过检索相关文档来增强模型的回答准确性。',
    'LoRA微调': 'LoRA是低秩适应技术，用于高效地微调大型语言模型，显著减少训练参数和计算资源需求。',
    'MCP': 'MCP（模型控制协议）是连接AI模型与外部工具和数据源的标准化协议，实现更强大的AI应用。',
    'TensorFlow': 'TensorFlow是Google开发的开源机器学习框架，广泛用于深度学习和神经网络应用开发。',
    'PyTorch': 'PyTorch是Facebook开发的开源机器学习框架，以其动态计算图和易用性在研究社区广受欢迎。',
    'Git': 'Git是分布式版本控制系统，用于跟踪代码变更历史，支持多人协作开发，是现代软件开发不可或缺的工具。',
    'GitHub': 'GitHub是基于Git的代码托管平台，提供代码存储、协作开发、项目管理等功能。',
    'GitLab': 'GitLab是完整的DevOps平台，提供Git存储库管理、CI/CD、项目管理等一站式解决方案。',
    'Cursor': 'Cursor是AI驱动的代码编辑器，集成了GPT-4等大模型，提供智能代码补全和编程助手功能。',
    'VS Code': 'Visual Studio Code是微软开发的轻量级代码编辑器，支持丰富的扩展生态和多种编程语言。',
    'WebStorm': 'WebStorm是JetBrains开发的JavaScript IDE，提供智能代码辅助和强大的调试功能。',
    'Postman': 'Postman是API开发和测试工具，提供API设计、测试、文档化和监控的完整解决方案。',
    'Figma': 'Figma是基于云的设计工具，支持实时协作，广泛用于UI/UX设计和原型制作。'
  }
  return descriptions[techName] || `${techName}是一项重要的技术，在现代软件开发中发挥着重要作用。`
}

const getTechFeatures = (techName) => {
  const features = {
    'Vue 3': ['响应式数据绑定', '组合式API', '优秀的性能', '渐进式框架', '丰富的生态系统'],
    'React': ['组件化开发', '虚拟DOM', '单向数据流', '丰富的生态', 'JSX语法'],
    'Angular': ['完整的框架', '依赖注入', 'TypeScript支持', '强大的CLI', '企业级特性'],
    'TypeScript': ['静态类型检查', '更好的IDE支持', '增强的代码可维护性', '与JavaScript兼容', '强大的类型推断'],
    'JavaScript': ['动态类型', '函数式编程', '事件驱动', '跨平台运行', '庞大的生态系统'],
    'HTML5': ['语义化标签', '多媒体支持', '离线应用', 'Canvas绘图', '地理定位API'],
    'CSS3': ['动画和过渡', '网格布局', 'Flexbox', '媒体查询', '自定义属性'],
    'Sass': ['变量支持', '嵌套规则', '混合功能', '继承机制', '模块化导入'],
    'Vite': ['极快的冷启动', 'HMR热更新', 'ES模块支持', '优化的构建', '插件生态'],
    'Webpack': ['模块打包', '代码分割', '热模块替换', '丰富的loader', '灵活的配置'],
    'Tailwind': ['原子化CSS', '响应式设计', '暗色模式', '自定义主题', 'JIT编译'],
    'Bootstrap': ['响应式网格', '预构建组件', '一致性设计', '浏览器兼容', '快速开发'],
    'Node.js': ['事件驱动', '非阻塞I/O', 'NPM包管理', '跨平台运行', '高性能'],
    'Express': ['轻量级框架', '中间件支持', '路由系统', '模板引擎', '易于扩展'],
    'NestJS': ['装饰器模式', '模块化架构', 'TypeScript原生支持', '依赖注入', '微服务支持'],
    'Java': ['面向对象', '跨平台运行', '内存管理', '强类型系统', '企业级支持'],
    'Spring Boot': ['自动配置', '嵌入式服务器', '生产就绪', '微服务支持', '丰富的starter'],
    'Python': ['简洁的语法', '丰富的库生态', '跨平台支持', '解释型语言', '动态类型'],
    'Django': ['MTV架构', 'ORM支持', '管理后台', '安全性', '国际化支持'],
    'FastAPI': ['高性能', '自动文档', '类型提示', '异步支持', 'OpenAPI标准'],
    'Go': ['并发编程', '快速编译', '静态类型', '垃圾回收', '简洁语法'],
    'Rust': ['内存安全', '零成本抽象', '并发安全', '高性能', '模式匹配'],
    'C++': ['高性能', '内存控制', '面向对象', '泛型编程', '标准库丰富'],
    'C#': ['面向对象', '.NET生态', '内存管理', '强类型', 'LINQ查询'],
    'MySQL': ['高性能', '可靠性', 'ACID特性', '复制支持', '广泛兼容'],
    'PostgreSQL': ['功能丰富', 'JSON支持', '扩展性', 'ACID兼容', '开源免费'],
    'MongoDB': ['文档存储', '水平扩展', '灵活schema', '聚合框架', '分片支持'],
    'Redis': ['内存存储', '多数据类型', '持久化', '主从复制', '集群支持'],
    'Elasticsearch': ['全文搜索', '分布式架构', '实时分析', 'RESTful API', '可扩展'],
    'SQLite': ['嵌入式数据库', '零配置', '跨平台', '轻量级', 'ACID兼容'],
    'Docker': ['轻量级容器', '一致的运行环境', '快速部署', '资源隔离', '易于扩展'],
    'Kubernetes': ['容器编排', '自动扩展', '服务发现', '滚动更新', '配置管理'],
    'AWS': ['全球基础设施', '按需付费', '高可用性', '安全可靠', '丰富的服务'],
    'Azure': ['混合云支持', 'AI集成', '企业级安全', '全球覆盖', 'DevOps工具'],
    'GCP': ['机器学习集成', '大数据分析', '全球网络', '开源友好', '可持续发展'],
    'Nginx': ['高性能', '负载均衡', '反向代理', '静态文件服务', '配置灵活'],
    'Apache': ['模块化架构', '多平台支持', '.htaccess支持', '虚拟主机', '稳定可靠'],
    'Jenkins': ['插件生态', '分布式构建', '管道支持', '集成能力', '开源免费'],
    'GitLab CI': ['一体化平台', '容器支持', '并行执行', '可视化管道', '环境管理'],
    'Claude': ['安全对齐', '长上下文理解', '代码生成', '多语言支持', '逻辑推理'],
    'GPT-4o': ['多模态能力', '强大的理解力', '创意生成', '代码编写', '实时对话'],
    'Deepseek': ['代码专精', '数学推理', '开源友好', '高效推理', '多语言支持'],
    'Qwen': ['中文优化', '多模态理解', '工具调用', '长文本处理', '知识丰富'],
    'Gemini': ['多模态融合', '实时处理', '代码理解', '创意表达', '逻辑推理'],
    'LLaMA': ['开源架构', '高效训练', '社区驱动', '可定制化', '研究友好'],
    'RAG': ['知识增强', '准确性提升', '实时更新', '领域专精', '可解释性'],
    'LoRA微调': ['参数效率', '快速适应', '资源节约', '性能保持', '部署友好'],
    'MCP': ['标准化协议', '工具集成', '安全通信', '扩展性强', '生态统一'],
    'TensorFlow': ['生产就绪', '分布式训练', '移动部署', '可视化工具', '社区活跃'],
    'PyTorch': ['动态计算图', '研究友好', '易于调试', 'Python原生', '灵活架构'],
    'Git': ['分布式架构', '分支管理', '版本追踪', '协作开发', '数据完整性'],
    'GitHub': ['代码托管', '协作功能', 'Actions CI/CD', '包管理', '社区生态'],
    'GitLab': ['一体化DevOps', '自托管选项', '项目管理', '安全扫描', '容器注册'],
    'Cursor': ['AI代码补全', '智能重构', '上下文理解', '多语言支持', '现代界面'],
    'VS Code': ['轻量快速', '扩展丰富', '调试支持', '内置Git', '跨平台'],
    'WebStorm': ['智能提示', '重构工具', '调试器', '版本控制', '框架支持'],
    'Postman': ['API测试', '协作功能', '自动化测试', '文档生成', '监控告警'],
    'Figma': ['实时协作', '组件系统', '原型制作', '设计系统', '开发者移交']
  }
  return features[techName] || ['功能强大', '易于使用', '社区活跃', '文档完善', '持续更新']
}

const getTechUsage = (techName) => {
  const usages = {
    'Vue 3': ['前端应用开发', '单页面应用', '移动端应用', '桌面应用', '微前端'],
    'React': ['Web应用开发', 'Native移动应用', '桌面应用', '静态网站', '企业级应用'],
    'Angular': ['企业级应用', '大型项目', 'PWA应用', '移动端开发', '管理系统'],
    'TypeScript': ['大型项目开发', 'API开发', '前端框架', '后端服务', '工具库开发'],
    'JavaScript': ['网页交互', '服务器开发', '移动应用', '桌面应用', '游戏开发'],
    'HTML5': ['网页结构', '移动网页', '混合应用', '邮件模板', '文档标记'],
    'CSS3': ['网页样式', '响应式设计', '动画效果', '移动适配', '打印样式'],
    'Sass': ['样式开发', '主题系统', '组件库', '设计系统', '响应式框架'],
    'Vite': ['前端构建', '开发环境', '库打包', '模块联邦', '静态站点'],
    'Webpack': ['应用打包', '资源优化', '代码分割', '多页应用', '库构建'],
    'Tailwind': ['快速原型', 'UI组件', '响应式设计', '设计系统', '实用样式'],
    'Bootstrap': ['快速开发', '响应式网站', '管理面板', '原型设计', '移动优先'],
    'Node.js': ['Web服务器', 'API接口', '实时应用', '命令行工具', '微服务'],
    'Express': ['Web API', '中间件服务', '微服务', '代理服务', '静态文件服务'],
    'NestJS': ['企业应用', '微服务架构', 'GraphQL API', '实时通信', '云原生应用'],
    'Java': ['企业应用', 'Android开发', 'Web服务', '大数据处理', '分布式系统'],
    'Spring Boot': ['微服务开发', 'Web应用', 'RESTful API', '企业集成', '云原生应用'],
    'Python': ['数据分析', '机器学习', 'Web开发', '自动化脚本', '科学计算'],
    'Django': ['Web应用', '内容管理', 'API开发', '电商平台', '社交网络'],
    'FastAPI': ['API开发', '机器学习服务', '微服务', '实时应用', '数据处理'],
    'Go': ['微服务', '网络编程', '系统工具', '容器化应用', '区块链开发'],
    'Rust': ['系统编程', 'WebAssembly', '区块链', '游戏引擎', '操作系统'],
    'C++': ['系统软件', '游戏开发', '嵌入式系统', '高性能计算', '桌面应用'],
    'C#': ['Windows应用', 'Web开发', '游戏开发', '企业软件', '移动应用'],
    'MySQL': ['Web应用', '电商系统', '内容管理', '数据仓库', '日志系统'],
    'PostgreSQL': ['企业应用', '地理信息系统', '数据分析', '时间序列', '全文搜索'],
    'MongoDB': ['内容管理', '实时分析', '物联网', '移动应用', '游戏数据'],
    'Redis': ['缓存系统', '会话存储', '消息队列', '实时排行榜', '分布式锁'],
    'Elasticsearch': ['搜索引擎', '日志分析', '监控系统', '商品搜索', '文档检索'],
    'SQLite': ['移动应用', '嵌入式系统', '桌面应用', '原型开发', '缓存存储'],
    'Docker': ['应用部署', '开发环境', '微服务架构', 'CI/CD', '云原生应用'],
    'Kubernetes': ['容器编排', '微服务管理', '云部署', '自动扩展', '多云部署'],
    'AWS': ['云端部署', '数据存储', '机器学习', '物联网', '企业应用'],
    'Azure': ['混合云', '企业应用', 'AI服务', 'DevOps', '数据分析'],
    'GCP': ['机器学习', '大数据分析', '容器服务', '无服务器', '移动后端'],
    'Nginx': ['Web服务器', '负载均衡', '反向代理', 'API网关', '静态资源'],
    'Apache': ['Web托管', '虚拟主机', '企业应用', '内容服务', '代理服务'],
    'Jenkins': ['持续集成', '自动化部署', '代码质量', '测试自动化', 'DevOps流水线'],
    'GitLab CI': ['代码集成', '自动化测试', '容器构建', '安全扫描', '部署管道'],
    'Claude': ['代码助手', '文档写作', '学习辅导', '创意写作', '问题解答'],
    'GPT-4o': ['智能对话', '内容创作', '代码生成', '图像理解', '教育培训'],
    'Git': ['版本控制', '代码管理', '团队协作', '开源项目', '发布管理']
  }
  return usages[techName] || ['软件开发', '系统集成', '项目管理', '技术学习', '产品创新']
}

// 鼠标移动事件处理
const handleMouseMove = (event) => {
  mousePosition.value = {
    x: event.clientX,
    y: event.clientY
  }
}

// 更新脸部位置
const updateFacePosition = () => {
  if (gridContainer.value) {
    const rect = gridContainer.value.getBoundingClientRect()
    facePosition.value = {
      x: rect.left + rect.width / 2,
      y: rect.top + rect.height / 2
    }
  }
}

// 拖拽功能
const startDrag = (event) => {
  isDragging.value = true
  const clientX = event.type === 'touchstart' ? event.touches[0].clientX : event.clientX
  const clientY = event.type === 'touchstart' ? event.touches[0].clientY : event.clientY
  
  dragStart.value = {
    x: clientX - dragOffset.value.x,
    y: clientY - dragOffset.value.y
  }
  
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('touchmove', onDrag)
  document.addEventListener('mouseup', stopDrag)
  document.addEventListener('touchend', stopDrag)
  
  event.preventDefault()
}

const onDrag = (event) => {
  if (!isDragging.value) return
  
  const clientX = event.type === 'touchmove' ? event.touches[0].clientX : event.clientX
  const clientY = event.type === 'touchmove' ? event.touches[0].clientY : event.clientY
  
  // 计算新的偏移值
  const newOffset = {
    x: clientX - dragStart.value.x,
    y: clientY - dragStart.value.y
  }
  
  // 使用更智能的更新策略：限制更新频率为60fps
  const now = performance.now()
  const timeSinceLastUpdate = now - lastUpdateTime.value
  
  if (!dragPending.value && timeSinceLastUpdate > 16) { // 约60fps
    dragPending.value = true
    lastUpdateTime.value = now
    requestAnimationFrame(() => {
      dragOffset.value = newOffset
      dragPending.value = false
    })
  }
  
  // 阻止默认滚动行为
  event.preventDefault()
}

const stopDrag = () => {
  isDragging.value = false
  
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('touchmove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
  document.removeEventListener('touchend', stopDrag)
}

// 恢复正常布局的方法
const restoreLayout = () => {
  router.replace({
    path: '/tech-stack',
    query: {
      ...Object.fromEntries(
        Object.entries(route.query).filter(([key]) => key !== 'showStripeReveal')
      )
    }
  });
}

// 技术点击处理
const handleTechClick = (tech, event) => {
  if (isDragging.value || showTechDetail.value || isClosingDetail.value) return
  
  event.stopPropagation()
  
  // 记录点击位置用于动画 - 获取相对于视口的精确位置
  const rect = event.currentTarget.getBoundingClientRect()
  selectedTechPosition.value = {
    x: rect.left + rect.width / 2,
    y: rect.top + rect.height / 2
  }
  
  // 设置选中的技术
  selectedTech.value = { ...tech }
  
  // 使用 requestAnimationFrame 确保 DOM 更新后再开始动画
  requestAnimationFrame(() => {
    showTechDetail.value = true
  })
}

// 关闭技术详情
const closeTechDetail = () => {
  // 设置关闭状态，触发标签回归动画
  isClosingDetail.value = true
  
  // 立即隐藏详情内容和overlay（不影响标签动画）
  showTechDetail.value = false
  
  // 等待标签移动回原位置后，立即清除选中状态实现无缝过渡
  setTimeout(() => {
    // 直接清除所有状态，让原网格的标签立即显示
    isClosingDetail.value = false
    selectedTech.value = null
    selectedTechPosition.value = { x: 0, y: 0 }
  }, 1200) // 精确匹配动画时长
}

onMounted(() => {
  // 添加鼠标移动监听器
  document.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('resize', updateFacePosition)
  
  // 立即显示页面内容作为背景
  isVisible.value = true
  
  // 显示黄色条带（初始状态铺满屏幕）
  showStripeEnter.value = true
  
  if (route.query.showStripeReveal === 'true') {
    showStripeTransition.value = true
    showStripeReveal.value = false
    
    nextTick(() => {
      setTimeout(() => {
        showStripeReveal.value = true
      }, 100)
      
      setTimeout(() => {
        showStripeTransition.value = false
        restoreLayout()
      }, 660)
    })
  } else {
    // 使用nextTick确保DOM完全渲染后再开始动画
    nextTick(() => {
      // 条带消失动画函数
      const startStripeAnimation = () => {
        const stripes = document.querySelectorAll('.enter-stripe')
        console.log('找到条带元素数量:', stripes.length) // 调试日志
        
        if (stripes.length === 0) {
          // 如果没找到条带元素，再等待一下
          setTimeout(startStripeAnimation, 50)
          return
        }
        
        stripes.forEach((stripe, index) => {
          stripe.classList.add('stripe-disappear')
        })
        
        // 条带消失完成后隐藏条带容器
        setTimeout(() => {
          showStripeEnter.value = false
          // 初始化脸部位置
          nextTick(() => {
            updateFacePosition()
          })
        }, 500)
      }
      
      // 短暂延迟后开始条带消失动画
      setTimeout(startStripeAnimation, 100)
    })
  }
})

onUnmounted(() => {
  // 清理事件监听器
  document.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('resize', updateFacePosition)
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('touchmove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
  document.removeEventListener('touchend', stopDrag)
})
</script>

<style scoped>
.tech-stack {
  min-height: 100vh;
  background-color: var(--background-dark);
  color: white;
  overflow: hidden;
  position: relative;
  width: 100vw;
  margin: 0;
  padding: 0;
}

.fade-in {
  opacity: 1;
  transition: opacity 0.8s ease-in-out;
}

.main-content {
  position: relative;
  height: 100vh;
  width: 100vw;
  margin: 0;
  padding: 0;
}

/* 左上角logo样式 - 与主页完全一致 */
/* Logo样式已移至AppLogo组件 */

/* 固定在中心的可爱圆脸样式 */
.center-face {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1010;
  pointer-events: none;
  transition: all 1.2s cubic-bezier(0.25, 0.1, 0.25, 1);
}

.center-face.bounce-in {
  animation: bounceIn 0.8s ease-out;
}

.face-container {
  width: 240px;
  height: 240px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  filter: drop-shadow(0 0 20px rgba(255, 215, 0, 0.6));
  transition: all 0.2s ease-out;
  transform-style: preserve-3d;
  transform-origin: center center;
}

.face-main {
  position: relative;
  width: 160px;
  height: 160px;
  border-radius: 50%;
  background: linear-gradient(135deg, 
    #FFD700 0%, 
    #FFC107 40%, 
    #FF9800 80%, 
    #FF8F00 100%);
  border: 4px solid #FF8F00;
  box-shadow: 
    0 8px 32px rgba(255, 215, 0, 0.4),
    inset 0 3px 10px rgba(255, 255, 255, 0.3),
    inset 0 -3px 10px rgba(0, 0, 0, 0.2),
    0 0 20px rgba(255, 215, 0, 0.3);
  transition: all 0.3s ease;
}

.face-main:hover {
  transform: scale(1.05);
  box-shadow: 
    0 12px 40px rgba(255, 215, 0, 0.5),
    inset 0 3px 10px rgba(255, 255, 255, 0.4),
    inset 0 -3px 10px rgba(0, 0, 0, 0.25),
    0 0 30px rgba(255, 215, 0, 0.5);
}

.eye {
  position: absolute;
  width: 42px;
  height: 48px;
  border-radius: 60% 40%;
  background: linear-gradient(145deg, 
    rgba(255, 255, 255, 0.95) 0%, 
    rgba(245, 245, 245, 0.9) 100%);
  border: 2.5px solid #2a2a2a;
  box-shadow: 
    inset 0 2px 4px rgba(255, 255, 255, 0.8),
    inset 0 -2px 4px rgba(0, 0, 0, 0.1),
    0 2px 8px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  z-index: 5;
  transition: all 0.2s ease-out;
  transform-origin: center center;
}

.eye:hover {
  transform: scale(1.1);
  box-shadow: 
    inset 0 2px 6px rgba(255, 255, 255, 0.9),
    inset 0 -2px 6px rgba(0, 0, 0, 0.15),
    0 4px 12px rgba(0, 0, 0, 0.2);
}

.left-eye {
  top: 38px;
  left: 32px;
  transform: rotate(-5deg);
}

.right-eye {
  top: 38px;
  right: 32px;
  transform: rotate(5deg);
}

.eye-white {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: inherit;
  background: inherit;
  overflow: hidden;
}

.eyeball {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: radial-gradient(
    circle at 35% 25%, 
    #2a2a2a 0%, 
    #1a1a1a 40%, 
    #000000 100%
  );
  border: 1px solid #000;
  transition: transform 0.1s ease-out;
  box-shadow: 
    0 2px 6px rgba(0, 0, 0, 0.7),
    inset 0 1px 2px rgba(255, 255, 255, 0.1);
}

.eye-shine {
  position: absolute;
  top: 15%;
  left: 25%;
  width: 35%;
  height: 35%;
  border-radius: 50%;
  background: radial-gradient(
    circle at center, 
    rgba(255, 255, 255, 0.9) 0%, 
    rgba(255, 255, 255, 0.6) 50%,
    transparent 100%
  );
  transition: all 0.3s ease;
  animation: eyeShine 3s ease-in-out infinite;
}

/* 眼睛高光闪烁动画 */
@keyframes eyeShine {
  0%, 90%, 100% { 
    opacity: 1; 
    transform: scale(1);
  }
  95% { 
    opacity: 0.3; 
    transform: scale(0.8);
  }
}

.mouth {
  position: absolute;
  bottom: 28px;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 10px;
  border: 3px solid #333;
  border-top: none;
  border-radius: 0 0 25px 25px;
  background: #1a1a1a;
  box-shadow: 
    inset 0 -2px 4px rgba(255, 255, 255, 0.1),
    0 2px 4px rgba(0, 0, 0, 0.2);
  transition: transform 0.1s ease-out;
}

.mouth::before {
  content: '';
  position: absolute;
  top: -3px;
  left: -3px;
  right: -3px;
  height: 6px;
  background: #333;
  border-radius: 3px;
}

.nose {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 6px;
  height: 6px;
  background: linear-gradient(135deg, #ffd700 0%, #ffb300 100%);
  border-radius: 50%;
  box-shadow: 
    0 1px 2px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
  z-index: 1;
}

.blush {
  position: absolute;
  top: 55%;
  width: 15px;
  height: 8px;
  background: linear-gradient(135deg, 
    rgba(255, 100, 100, 0.4) 0%, 
    rgba(255, 150, 150, 0.2) 100%);
  border-radius: 50%;
  opacity: 0.6;
  transition: opacity 0.3s ease;
}

.face-main:hover .blush {
  opacity: 0.8;
}

.left-blush {
  left: 12px;
}

.right-blush {
  right: 12px;
}

.collision-zone {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 240px;
  height: 240px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 215, 0, 0.1) 0%, transparent 70%);
  pointer-events: none;
}

/* 无限网格容器 */
.infinite-grid-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  cursor: grab;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

.infinite-grid-container:active {
  cursor: grabbing;
}

.tech-grid-infinite {
  position: relative;
  width: 100%;
  height: 100%;
  will-change: transform;
  transform-origin: 0 0;
  backface-visibility: hidden;
  transform: translateZ(0);
}

/* 技术标签项 */
.tech-item {
  position: absolute;
  width: 420px;
  height: 240px;
  transform: translate(
    calc(var(--grid-x) * 500px),
    calc(var(--grid-y) * 380px)
  );
  opacity: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  will-change: transform;
  backface-visibility: hidden;
  transform-origin: center center;
}

.tech-item.infinite-item {
  animation: fadeInTech 0.6s ease-out forwards;
  animation-delay: var(--delay);
}

.tech-item.fade-in-tech {
  opacity: 1;
}

.tech-item.no-animation {
  opacity: 1;
  animation: none;
}

.tech-item.hidden-by-emoji {
  opacity: 0;
  transform: translate(
    calc(var(--grid-x) * 500px),
    calc(var(--grid-y) * 380px)
  ) scale(0.5);
  transition: all 0.3s ease;
}

/* 大号技术标签卡片 */
.tech-badge-large {
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.1) 0%, 
    rgba(255, 255, 255, 0.05) 100%);
  border: 3px solid var(--primary-color);
  border-radius: 16px;
  padding: 20px;
  text-align: center;
  backdrop-filter: blur(10px);
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  transition: transform 0.2s ease-out, box-shadow 0.2s ease-out;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  will-change: transform, box-shadow;
  backface-visibility: hidden;
}

.tech-badge-large::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, 
    transparent, 
    rgba(255, 220, 0, 0.2), 
    transparent);
  transition: left 0.5s ease;
}

.tech-badge-large:hover::before {
  left: 100%;
}

.tech-badge-large:hover {
  transform: translateY(-5px) rotate(1deg);
  border-color: var(--primary-color);
  box-shadow: 
    0 12px 36px rgba(0, 0, 0, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.tech-name {
  font-size: 24px;
  font-weight: 900;
  font-style: italic;
  color: var(--primary-color);
  margin-bottom: 8px;
  text-shadow: 
    3px 3px 0px rgba(0, 0, 0, 1),
    -1px -1px 0px rgba(0, 0, 0, 0.8);
  transform: skew(-12deg) rotate(-2deg);
  letter-spacing: 2px;
  text-transform: uppercase;
  font-family: 'Arial Black', Arial, sans-serif;
}

.tech-category {
  font-size: 16px;
  font-weight: 700;
  font-style: italic;
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 
    2px 2px 0px rgba(0, 0, 0, 1),
    -1px -1px 0px rgba(0, 0, 0, 0.6);
  transform: skew(-8deg) rotate(-1deg);
  letter-spacing: 1px;
  font-family: 'Arial Black', Arial, sans-serif;
}

/* 特定技术类别样式 - 统一黄色边框 */
.tech-badge-large.vue,
.tech-badge-large.react,
.tech-badge-large.java,
.tech-badge-large.python,
.tech-badge-large.docker,
.tech-badge-large.aws,
.tech-badge-large.ai-badge {
  border-color: var(--primary-color);
}

.tech-badge-large.vue:hover,
.tech-badge-large.react:hover,
.tech-badge-large.java:hover,
.tech-badge-large.python:hover,
.tech-badge-large.docker:hover,
.tech-badge-large.aws:hover,
.tech-badge-large.ai-badge:hover {
  border-color: var(--primary-color);
  box-shadow: 
    0 12px 36px rgba(0, 0, 0, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

/* 选中的技术标签动画 */
.tech-item.selected-item {
  z-index: 1000;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.1s ease-out;
}

.tech-item.selected-item .tech-badge-large {
  border-color: var(--primary-color);
  box-shadow: 
    0 0 30px rgba(255, 215, 0, 0.8),
    0 12px 36px rgba(0, 0, 0, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

/* 其他标签淡出动画 */
.tech-item.fade-out-others {
  opacity: 0;
  transition: opacity 0.3s ease-out;
  transition-delay: calc((var(--grid-x) + var(--grid-y)) * 0.01s);
  pointer-events: none;
}

/* 恢复时的淡入动画 */
.tech-item {
  opacity: 1;
  transition: opacity 0.4s ease-out;
  transition-delay: calc((var(--grid-x) + var(--grid-y)) * 0.005s);
}

/* 表情缩小右移动画 */
.center-face.shrink-left {
  transform: translate(-40%, -50%) scale(0.35);
  transition: all 1.2s cubic-bezier(0.25, 0.1, 0.25, 1);
  z-index: 1010;
}

/* 技术详情页面样式 */
.tech-detail-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: transparent;
  z-index: 100000;
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.3s ease-out;
  pointer-events: none;
}

.tech-detail-overlay.active {
  opacity: 1;
  visibility: visible;
  pointer-events: all;
}



.selected-tech-badge {
  position: fixed;
  top: var(--start-y, 50%);
  left: var(--start-x, 35%);
  transform: translate(-50%, -50%) scale(1.0);
  width: 420px;
  height: 240px;
  z-index: 1002;
  transition: all 1.2s cubic-bezier(0.25, 0.1, 0.25, 1);
  pointer-events: none;
  opacity: 1;
  filter: drop-shadow(0 0 25px rgba(255, 215, 0, 0.9));
  will-change: transform, top, left;
}

.selected-tech-badge.initial-state {
  top: var(--start-y);
  left: var(--start-x);
  transform: translate(-50%, -50%) scale(1.0);
  transition: none;
}

.selected-tech-badge.moving-to-detail {
  top: 50%;
  left: 35%;
  transform: translate(-50%, -50%) scale(0.85);
  opacity: 1;
  filter: drop-shadow(0 0 20px rgba(255, 215, 0, 0.8));
}

.selected-tech-badge.moving-back {
  top: var(--start-y);
  left: var(--start-x);
  transform: translate(-50%, -50%) scale(0.85);
  opacity: 1;
  filter: drop-shadow(0 0 25px rgba(255, 215, 0, 0.9));
  transition: all 1.2s cubic-bezier(0.25, 0.1, 0.25, 1);
}

.tech-detail-content {
  position: absolute;
  top: 50%;
  right: 10%;
  transform: translateY(-50%) translateX(100px);
  width: 500px;
  max-height: 80vh;
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.15) 0%, 
    rgba(255, 255, 255, 0.05) 100%);
  border: 2px solid var(--primary-color);
  border-radius: 20px;
  padding: 30px;
  backdrop-filter: blur(20px);
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  opacity: 0;
  visibility: hidden;
  transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  transition-delay: 0.6s;
  overflow-y: auto;
  z-index: 1000;
  scrollbar-width: thin;
  scrollbar-color: var(--primary-color) transparent;
}

.tech-detail-content.visible {
  transform: translateY(-50%) translateX(0);
  opacity: 1;
  visibility: visible;
  transition-delay: 0s;
}

.tech-detail-content::-webkit-scrollbar {
  width: 6px;
}

.tech-detail-content::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

.tech-detail-content::-webkit-scrollbar-thumb {
  background: var(--primary-color);
  border-radius: 3px;
}

.detail-header {
  margin-bottom: 25px;
  text-align: center;
  border-bottom: 2px solid var(--primary-color);
  padding-bottom: 20px;
}

.detail-title {
  font-size: 32px;
  font-weight: 900;
  font-style: italic;
  color: var(--primary-color);
  margin: 0 0 10px 0;
  text-shadow: 
    3px 3px 0px rgba(0, 0, 0, 1),
    -1px -1px 0px rgba(0, 0, 0, 0.8);
  transform: skew(-8deg);
  letter-spacing: 1.5px;
  text-transform: uppercase;
  font-family: 'Arial Black', Arial, sans-serif;
}

.detail-category {
  font-size: 16px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.8);
  background: rgba(255, 215, 0, 0.2);
  padding: 6px 16px;
  border-radius: 15px;
  border: 1px solid var(--primary-color);
}

.detail-body {
  margin: 25px 0;
}

.detail-section {
  margin-bottom: 25px;
}

.detail-section h3 {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-color);
  margin: 0 0 15px 0;
  text-shadow: 2px 2px 0px rgba(0, 0, 0, 0.8);
  border-left: 4px solid var(--primary-color);
  padding-left: 12px;
}

.detail-section p {
  font-size: 16px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  text-shadow: 1px 1px 0px rgba(0, 0, 0, 0.8);
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-list li {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 8px;
  padding-left: 20px;
  position: relative;
  text-shadow: 1px 1px 0px rgba(0, 0, 0, 0.8);
}

.feature-list li::before {
  content: '⚡';
  position: absolute;
  left: 0;
  color: var(--primary-color);
  font-weight: bold;
}

.usage-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.usage-tag {
  background: linear-gradient(135deg, 
    rgba(255, 215, 0, 0.3) 0%, 
    rgba(255, 215, 0, 0.1) 100%);
  color: var(--primary-color);
  padding: 8px 16px;
  border-radius: 20px;
  border: 1px solid rgba(255, 215, 0, 0.5);
  font-size: 14px;
  font-weight: 600;
  text-shadow: 1px 1px 0px rgba(0, 0, 0, 0.8);
  transition: all 0.3s ease;
  cursor: default;
}

.usage-tag:hover {
  background: linear-gradient(135deg, 
    rgba(255, 215, 0, 0.5) 0%, 
    rgba(255, 215, 0, 0.2) 100%);
  border-color: var(--primary-color);
  transform: scale(1.05);
}



/* 发光脉冲动画 */
@keyframes pulseGlow {
  from {
    box-shadow: 
      0 0 20px rgba(255, 215, 0, 0.6),
      0 12px 36px rgba(0, 0, 0, 0.4),
      inset 0 1px 0 rgba(255, 255, 255, 0.2);
  }
  to {
    box-shadow: 
      0 0 40px rgba(255, 215, 0, 1),
      0 16px 40px rgba(0, 0, 0, 0.5),
      inset 0 1px 0 rgba(255, 255, 255, 0.3);
  }
}

/* 拖拽提示样式 */
.drag-hint {
  position: fixed;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.8);
  padding: 12px 24px;
  border-radius: 25px;
  border: 1px solid rgba(255, 215, 0, 0.3);
  backdrop-filter: blur(10px);
  opacity: 0;
  transition: all 0.5s ease;
  z-index: 1000;
  pointer-events: none;
}

.drag-hint.show {
  opacity: 1;
  animation: pulseHint 2s ease-in-out infinite;
}

.hint-text {
  color: var(--primary-yellow);
  font-size: 14px;
  font-weight: 500;
  text-shadow: 0 0 8px rgba(255, 215, 0, 0.5);
}

/* 过渡动画 */
/* 黄色条带进入过渡效果 */
.stripe-enter-transition {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  pointer-events: none;
  z-index: 99999;
}

.enter-stripe {
  position: absolute;
  top: 0;
  width: 100vw;
  height: calc(100vh / 6 + 1px); /* 增加1px避免缝隙 */
  background: var(--primary-color);
  will-change: transform;
  transform: translateX(0); /* 初始状态：铺满屏幕 */
  transition: transform 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

/* 条带位置 */
.enter-stripe:nth-child(1) { 
  top: 0; 
}
.enter-stripe:nth-child(2) { 
  top: calc(100vh / 6 * 1 - 1px); 
}
.enter-stripe:nth-child(3) { 
  top: calc(100vh / 6 * 2 - 1px); 
}
.enter-stripe:nth-child(4) { 
  top: calc(100vh / 6 * 3 - 1px); 
}
.enter-stripe:nth-child(5) { 
  top: calc(100vh / 6 * 4 - 1px); 
}
.enter-stripe:nth-child(6) { 
  top: calc(100vh / 6 * 5 - 1px); 
}

/* 条带消失动画 - 左右轴对称：前3个条带向左消失，后3个条带向右消失 */
.enter-stripe.stripe-disappear:nth-child(1) {
  transform: translateX(-100vw); /* 向左消失 */
  transition-delay: 0s;
}

.enter-stripe.stripe-disappear:nth-child(2) {
  transform: translateX(-100vw); /* 向左消失 */
  transition-delay: 0.05s;
}

.enter-stripe.stripe-disappear:nth-child(3) {
  transform: translateX(-100vw); /* 向左消失 */
  transition-delay: 0.10s;
}

.enter-stripe.stripe-disappear:nth-child(4) {
  transform: translateX(100vw); /* 向右消失 */
  transition-delay: 0.10s;
}

.enter-stripe.stripe-disappear:nth-child(5) {
  transform: translateX(100vw); /* 向右消失 */
  transition-delay: 0.05s;
}

.enter-stripe.stripe-disappear:nth-child(6) {
  transform: translateX(100vw); /* 向右消失 */
  transition-delay: 0s;
}

.stripe-transition {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 9999;
  pointer-events: none;
  display: flex;
  flex-direction: column;
}

.stripe {
  flex: 1;
  background: var(--primary-yellow);
  transform: translateX(-100%);
  transition: transform 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

.stripe:nth-child(odd) {
  transform: translateX(100%);
}

.stripe-transition.revealing .stripe {
  transform: translateX(0);
}

.stripe-transition.revealing .stripe:nth-child(1) { transition-delay: 0s; }
.stripe-transition.revealing .stripe:nth-child(2) { transition-delay: 0.1s; }
.stripe-transition.revealing .stripe:nth-child(3) { transition-delay: 0.2s; }
.stripe-transition.revealing .stripe:nth-child(4) { transition-delay: 0.3s; }
.stripe-transition.revealing .stripe:nth-child(5) { transition-delay: 0.4s; }
.stripe-transition.revealing .stripe:nth-child(6) { transition-delay: 0.5s; }

/* 响应式设计 */
@media (min-width: 768px) {
  /* Logo样式已移至AppLogo组件 */
  
  .face-container {
    width: 260px;
    height: 260px;
  }
  
  .face-main {
    width: 180px;
    height: 180px;
  }
  
  .collision-zone {
    width: 260px;
    height: 260px;
  }
  
  .tech-name {
    font-size: 26px;
    font-weight: 900;
    font-style: italic;
    text-shadow: 
      3px 3px 0px rgba(0, 0, 0, 1),
      -1px -1px 0px rgba(0, 0, 0, 0.8);
    transform: skew(-12deg) rotate(-2deg);
    letter-spacing: 2.2px;
    text-transform: uppercase;
    font-family: 'Arial Black', Arial, sans-serif;
  }
  
  .tech-category {
    font-size: 17px;
    font-weight: 700;
    font-style: italic;
    text-shadow: 
      2px 2px 0px rgba(0, 0, 0, 1),
      -1px -1px 0px rgba(0, 0, 0, 0.6);
    transform: skew(-8deg) rotate(-1deg);
    letter-spacing: 1.2px;
    font-family: 'Arial Black', Arial, sans-serif;
  }
}

@media (max-width: 767px) {
  /* Logo样式已移至AppLogo组件 */
  
  .face-container {
    width: 100px;
    height: 100px;
  }
  
  .face-main {
    width: 100px;
    height: 100px;
  }
  
  .collision-zone {
    width: 160px;
    height: 160px;
  }
  
  .tech-item {
    width: 280px;
    height: 160px;
    transform: translate(
      calc(var(--grid-x) * 350px),
      calc(var(--grid-y) * 280px)
    );
  }
  
  .tech-item.hidden-by-emoji {
    transform: translate(
      calc(var(--grid-x) * 350px),
      calc(var(--grid-y) * 280px)
    ) scale(0.5);
  }
  
  .tech-badge-large {
    padding: 15px;
  }
  
  .tech-name {
    font-size: 18px;
    font-weight: 900;
    font-style: italic;
    margin-bottom: 6px;
    text-shadow: 
      2px 2px 0px rgba(0, 0, 0, 1),
      -1px -1px 0px rgba(0, 0, 0, 0.8);
    transform: skew(-10deg) rotate(-1deg);
    letter-spacing: 1.5px;
    text-transform: uppercase;
    font-family: 'Arial Black', Arial, sans-serif;
  }
  
  .tech-category {
    font-size: 13px;
    font-weight: 700;
    font-style: italic;
    text-shadow: 
      1px 1px 0px rgba(0, 0, 0, 1),
      -1px -1px 0px rgba(0, 0, 0, 0.6);
    transform: skew(-6deg) rotate(-1deg);
    letter-spacing: 0.8px;
    font-family: 'Arial Black', Arial, sans-serif;
  }
  
  /* 移动端技术详情页面样式调整 */
  .center-face.shrink-left {
    transform: translate(-30%, -80%) scale(0.2);
  }
  
  .selected-tech-badge {
    width: 280px;
    height: 160px;
  }
  
  .selected-tech-badge.initial-state {
    top: var(--start-y);
    left: var(--start-x);
    width: 280px;
    height: 160px;
  }
  
  .selected-tech-badge.moving-to-detail {
    top: 20%;
    left: 50%;
  }
  
  .tech-detail-content {
    position: fixed;
    top: 40%;
    left: 5%;
    right: 5%;
    bottom: 5%;
    width: auto;
    max-height: none;
    transform: translateY(20px);
    padding: 20px;
    border-radius: 15px;
  }
  
  .tech-detail-content.visible {
    transform: translateY(0);
  }
  
  /* 移动端其他标签消失动画调整 */
  .tech-item.fade-out-others {
    opacity: 0;
  }
  
  .detail-title {
    font-size: 24px;
    margin-bottom: 8px;
  }
  
  .detail-category {
    font-size: 14px;
    padding: 4px 12px;
  }
  
  .detail-section h3 {
    font-size: 18px;
    margin-bottom: 10px;
  }
  
  .detail-section p {
    font-size: 14px;
    line-height: 1.5;
  }
  
  .feature-list li {
    font-size: 14px;
    margin-bottom: 6px;
  }
  
  .usage-tag {
    font-size: 12px;
    padding: 6px 12px;
  }
  

}

@media (min-width: 1200px) {
  .face-container {
    width: 320px;
    height: 320px;
  }
  
  .face-main {
    width: 220px;
    height: 220px;
  }
  
  .collision-zone {
    width: 320px;
    height: 320px;
  }
  
  .tech-item {
    width: 360px;
    height: 200px;
    transform: translate(
      calc(var(--grid-x) * 480px),
      calc(var(--grid-y) * 480px)
    );
  }
  
  .tech-item.hidden-by-emoji {
    transform: translate(
      calc(var(--grid-x) * 480px),
      calc(var(--grid-y) * 480px)
    ) scale(0.5);
  }
  
  /* 大屏幕其他标签消失动画调整 */
  .tech-item.fade-out-others {
    opacity: 0;
  }
  
  .tech-badge-large {
    padding: 25px;
  }
  
  .tech-name {
    font-size: 28px;
    font-weight: 900;
    font-style: italic;
    margin-bottom: 10px;
    text-shadow: 
      4px 4px 0px rgba(0, 0, 0, 1),
      -2px -2px 0px rgba(0, 0, 0, 0.8);
    transform: skew(-15deg) rotate(-3deg);
    letter-spacing: 2.5px;
    text-transform: uppercase;
    font-family: 'Arial Black', Arial, sans-serif;
  }
  
  .tech-category {
    font-size: 18px;
    font-weight: 700;
    font-style: italic;
    text-shadow: 
      3px 3px 0px rgba(0, 0, 0, 1),
      -1px -1px 0px rgba(0, 0, 0, 0.6);
    transform: skew(-10deg) rotate(-2deg);
    letter-spacing: 1.5px;
    font-family: 'Arial Black', Arial, sans-serif;
  }
}
</style> 