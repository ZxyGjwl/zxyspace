<script setup>
const currentYear = new Date().getFullYear();
</script>

<template>
  <footer class="footer">
    <div class="footer-container">
      <div class="footer-section">
        <h3 class="footer-heading">ZXY Space</h3>
        <p>个人技术/娱乐博客网站，分享技术经验、生活见闻和各种有趣的内容。</p>
        <div class="social-links">
          <a href="#" class="social-link" aria-label="GitHub">
            <i class="el-icon-platform-eleme"></i>
          </a>
          <a href="#" class="social-link" aria-label="Twitter">
            <i class="el-icon-s-promotion"></i>
          </a>
          <a href="#" class="social-link" aria-label="LinkedIn">
            <i class="el-icon-connection"></i>
          </a>
        </div>
      </div>
      <div class="footer-section">
        <h3 class="footer-heading">快速链接</h3>
        <ul class="footer-links">
          <li><router-link to="/">首页</router-link></li>
          <li><router-link to="/blog">博客</router-link></li>
          <li><router-link to="/about">关于</router-link></li>
        </ul>
      </div>
      <div class="footer-section">
        <h3 class="footer-heading">联系方式</h3>
        <ul class="footer-links">
          <li>Email: contact@zxyspace.com</li>
          <li>地址: 北京市朝阳区</li>
        </ul>
      </div>
    </div>
    <div class="footer-bottom">
      <p>&copy; {{ currentYear }} ZXY Space. 保留所有权利。</p>
    </div>
  </footer>
</template>

<style lang="scss" scoped>
.footer {
  background-color: var(--background-lighter);
  padding: var(--spacing-xxl) 0;
  margin-top: var(--spacing-xxl);
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.footer-container {
  max-width: var(--max-width-content);
  margin: 0 auto;
  padding: 0 var(--spacing-lg);
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-xl);
}

.footer-section {
  p {
    color: var(--text-secondary);
    margin-bottom: var(--spacing-lg);
    line-height: 1.6;
  }
}

.footer-heading {
  color: var(--primary-color);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-md);
  position: relative;
  padding-bottom: var(--spacing-xs);
  
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 30px;
    height: 2px;
    background-color: var(--primary-color);
  }
}

.footer-links {
  list-style: none;
  padding: 0;
  
  li {
    margin-bottom: var(--spacing-sm);
  }
  
  a {
    color: var(--text-secondary);
    text-decoration: none;
    transition: color var(--transition-fast);
    
    &:hover {
      color: var(--primary-color);
    }
  }
}

.social-links {
  display: flex;
  gap: var(--spacing-md);
  margin-top: var(--spacing-md);
  
  .social-link {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background-color: rgba(255, 255, 255, 0.05);
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--text-secondary);
    transition: all var(--transition-normal);
    
    &:hover {
      background-color: var(--primary-color);
      color: var(--background-dark);
      transform: translateY(-3px);
    }
    
    i {
      font-size: var(--font-size-md);
    }
  }
}

.footer-bottom {
  max-width: var(--max-width-content);
  margin: var(--spacing-xl) auto 0;
  padding: var(--spacing-lg) var(--spacing-lg) 0;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  text-align: center;
  color: var(--text-tertiary);
  font-size: var(--font-size-sm);
}

@media (max-width: 768px) {
  .footer {
    padding: var(--spacing-xl) 0;
  }
  
  .footer-container {
    grid-template-columns: 1fr;
    gap: var(--spacing-lg);
    padding: 0 var(--spacing-md);
  }
  
  .footer-section {
    margin-bottom: var(--spacing-lg);
  }
}
</style>