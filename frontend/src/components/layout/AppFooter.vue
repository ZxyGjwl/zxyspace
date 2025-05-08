<script setup>
const currentYear = new Date().getFullYear();
</script>

<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="footer-section">
        <h3>ZXY Space</h3>
        <p>个人技术/娱乐博客网站，分享技术经验、生活见闻和各种有趣的内容。</p>
      </div>
      <div class="footer-section">
        <h3>快速链接</h3>
        <ul>
          <li><router-link to="/">首页</router-link></li>
          <li><router-link to="/blog">博客</router-link></li>
          <li><router-link to="/about">关于</router-link></li>
        </ul>
      </div>
      <div class="footer-section">
        <h3>联系方式</h3>
        <p>
          <el-icon><Message /></el-icon> Email: contact@zxyspace.com
        </p>
        <p>
          <el-icon><Location /></el-icon> 地址: 北京市朝阳区
        </p>
      </div>
    </div>
    <div class="footer-bottom">
      <p>&copy; {{ currentYear }} ZXY Space. 保留所有权利。</p>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  background-color: #f5f7fa;
  padding: 40px 0 20px;
  margin-top: 60px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  padding: 0 20px;
}

.footer-section {
  flex: 1;
  min-width: 250px;
  margin-bottom: 20px;
}

.footer-section h3 {
  color: #333;
  margin-bottom: 15px;
  font-size: 1.2rem;
}

.footer-section p {
  color: #666;
  line-height: 1.6;
}

.footer-section ul {
  list-style: none;
  padding: 0;
}

.footer-section ul li {
  margin-bottom: 8px;
}

.footer-section a {
  color: #409EFF;
  text-decoration: none;
}

.footer-section a:hover {
  text-decoration: underline;
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  margin-top: 20px;
  border-top: 1px solid #e0e0e0;
}

.footer-bottom p {
  color: #888;
}
</style> 