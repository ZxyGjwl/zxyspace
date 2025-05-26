<template>
  <div class="about">
    <h1>关于我</h1>
    
    <div class="about-content">
      <div class="profile-section">
        <div class="profile-image">
          <img src="https://randomuser.me/api/portraits/men/1.jpg" alt="个人照片">
        </div>
        
        <div class="profile-info">
          <h2>张学洋</h2>
          <p class="profile-tagline">全栈开发工程师 | 旅行爱好者 | 摄影师</p>
          
          <div class="profile-social">
            <a href="#" class="social-link">GitHub</a>
            <a href="#" class="social-link">微博</a>
            <a href="#" class="social-link">知乎</a>
            <a href="#" class="social-link">LinkedIn</a>
          </div>
        </div>
      </div>
      
      <div class="biography-section">
        <h3>个人简介</h3>
        <p>
          大家好，我是张学洋，一名热爱技术和创新的全栈开发工程师。我拥有多年的软件开发经验，专注于Web应用程序开发，擅长使用Java、Spring Boot、Vue.js等技术。
        </p>
        <p>
          除了编程，我还热爱旅行和摄影。我相信旅行能够开阔视野，摄影可以记录美好瞬间。在空闲时间，我喜欢探索新的地方，用相机捕捉风景和文化。
        </p>
        <p>
          创建这个个人空间是为了分享我在技术领域的探索和生活中的见闻。我希望通过博客文章，与志同道合的朋友交流经验和想法。
        </p>
      </div>
      
      <div class="skills-section">
        <h3>技能专长</h3>
        
        <div class="skills-category">
          <h4>前端技术</h4>
          <div class="skills-grid">
            <div class="skill-item">
              <div class="skill-name">HTML/CSS</div>
              <div class="skill-bar">
                <div class="skill-level" style="width: 95%"></div>
              </div>
            </div>
            <div class="skill-item">
              <div class="skill-name">JavaScript</div>
              <div class="skill-bar">
                <div class="skill-level" style="width: 90%"></div>
              </div>
            </div>
            <div class="skill-item">
              <div class="skill-name">Vue.js</div>
              <div class="skill-bar">
                <div class="skill-level" style="width: 85%"></div>
              </div>
            </div>
            <div class="skill-item">
              <div class="skill-name">React</div>
              <div class="skill-bar">
                <div class="skill-level" style="width: 75%"></div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="skills-category">
          <h4>后端技术</h4>
          <div class="skills-grid">
            <div class="skill-item">
              <div class="skill-name">Java</div>
              <div class="skill-bar">
                <div class="skill-level" style="width: 90%"></div>
              </div>
            </div>
            <div class="skill-item">
              <div class="skill-name">Spring Boot</div>
              <div class="skill-bar">
                <div class="skill-level" style="width: 85%"></div>
              </div>
            </div>
            <div class="skill-item">
              <div class="skill-name">Node.js</div>
              <div class="skill-bar">
                <div class="skill-level" style="width: 70%"></div>
              </div>
            </div>
            <div class="skill-item">
              <div class="skill-name">MySQL</div>
              <div class="skill-bar">
                <div class="skill-level" style="width: 80%"></div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="skills-category">
          <h4>其他技能</h4>
          <div class="skills-grid">
            <div class="skill-item">
              <div class="skill-name">Docker</div>
              <div class="skill-bar">
                <div class="skill-level" style="width: 75%"></div>
              </div>
            </div>
            <div class="skill-item">
              <div class="skill-name">Git</div>
              <div class="skill-bar">
                <div class="skill-level" style="width: 90%"></div>
              </div>
            </div>
            <div class="skill-item">
              <div class="skill-name">UI/UX设计</div>
              <div class="skill-bar">
                <div class="skill-level" style="width: 65%"></div>
              </div>
            </div>
            <div class="skill-item">
              <div class="skill-name">摄影</div>
              <div class="skill-bar">
                <div class="skill-level" style="width: 85%"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="experience-section">
        <h3>工作经历</h3>
        
        <div class="timeline">
          <div class="timeline-item">
            <div class="timeline-dot"></div>
            <div class="timeline-date">2020 - 至今</div>
            <div class="timeline-content">
              <h4>高级全栈开发工程师</h4>
              <p class="timeline-company">某科技有限公司</p>
              <p class="timeline-description">
                负责公司核心产品的前后端开发，使用Vue.js和Spring Boot构建企业级应用。主导了多个关键项目，优化了系统性能和用户体验。
              </p>
            </div>
          </div>
          
          <div class="timeline-item">
            <div class="timeline-dot"></div>
            <div class="timeline-date">2017 - 2020</div>
            <div class="timeline-content">
              <h4>后端开发工程师</h4>
              <p class="timeline-company">某网络科技有限公司</p>
              <p class="timeline-description">
                使用Java和Spring框架开发和维护企业级应用程序。参与了系统架构设计，提高了服务的可扩展性和性能。
              </p>
            </div>
          </div>
          
          <div class="timeline-item">
            <div class="timeline-dot"></div>
            <div class="timeline-date">2015 - 2017</div>
            <div class="timeline-content">
              <h4>软件开发实习生</h4>
              <p class="timeline-company">某信息技术有限公司</p>
              <p class="timeline-description">
                参与Web应用开发，学习和实践前后端技术。协助团队完成多个项目，积累了宝贵的实际开发经验。
              </p>
            </div>
          </div>
        </div>
      </div>
      
      <div class="contact-section">
        <h3>联系我</h3>
        <p>如果您有任何问题、建议或合作意向，欢迎通过以下方式与我联系：</p>
        
        <div class="contact-info">
          <div class="contact-item">
            <div class="contact-label">邮箱：</div>
            <div class="contact-value">example@email.com</div>
          </div>
          <div class="contact-item">
            <div class="contact-label">微信：</div>
            <div class="contact-value">my_wechat_id</div>
          </div>
        </div>
        
        <form class="contact-form">
          <div class="form-group">
            <label for="name">姓名</label>
            <input type="text" id="name" name="name" placeholder="请输入您的姓名">
          </div>
          
          <div class="form-group">
            <label for="email">邮箱</label>
            <input type="email" id="email" name="email" placeholder="请输入您的邮箱">
          </div>
          
          <div class="form-group">
            <label for="subject">主题</label>
            <input type="text" id="subject" name="subject" placeholder="请输入主题">
          </div>
          
          <div class="form-group">
            <label for="message">留言</label>
            <textarea id="message" name="message" rows="5" placeholder="请输入您的留言"></textarea>
          </div>
          
          <button type="submit" class="submit-button">发送留言</button>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.about {
  max-width: 900px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

h1 {
  text-align: center;
  margin-bottom: 2rem;
  color: var(--text-primary);
}

.about-content {
  display: flex;
  flex-direction: column;
  gap: 3rem;
}

.profile-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 1.5rem;
}

.profile-image img {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  object-fit: cover;
  border: 5px solid var(--primary-color);
  box-shadow: 0 0 15px rgba(255, 220, 0, 0.3);
}

.profile-info h2 {
  margin-bottom: 0.5rem;
  color: var(--primary-color);
}

.profile-tagline {
  color: var(--text-secondary);
  margin-bottom: 1rem;
}

.profile-social {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.social-link {
  display: inline-block;
  padding: 0.5rem 1rem;
  background-color: var(--background-lighter);
  border-radius: 4px;
  transition: all 0.3s;
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.social-link:hover {
  background-color: var(--primary-color);
  color: var(--background-dark);
}

.biography-section, .skills-section, .experience-section, .contact-section {
  background-color: var(--background-card);
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  border: 1px solid var(--border-color);
}

.biography-section h3, .skills-section h3, .experience-section h3, .contact-section h3 {
  margin-bottom: 1.5rem;
  border-bottom: 2px solid var(--primary-color);
  padding-bottom: 0.5rem;
  color: var(--primary-color);
}

.biography-section p {
  margin-bottom: 1rem;
  line-height: 1.6;
  color: var(--text-primary);
}

.skills-category {
  margin-bottom: 2rem;
}

.skills-category h4 {
  margin-bottom: 1rem;
  color: var(--text-primary);
}

.skills-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 1rem;
}

.skill-item {
  margin-bottom: 1rem;
}

.skill-name {
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: var(--text-primary);
}

.skill-bar {
  height: 8px;
  background-color: var(--background-lighter);
  border-radius: 4px;
  overflow: hidden;
}

.skill-level {
  height: 100%;
  background-color: var(--primary-color);
  border-radius: 4px;
  box-shadow: 0 0 5px rgba(255, 220, 0, 0.5);
}

.timeline {
  position: relative;
  padding-left: 2rem;
}

.timeline:before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 2px;
  background-color: var(--border-color);
}

.timeline-item {
  position: relative;
  padding-bottom: 2rem;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-dot {
  position: absolute;
  left: -2.5rem;
  top: 0;
  width: 1rem;
  height: 1rem;
  border-radius: 50%;
  background-color: var(--primary-color);
  border: 2px solid var(--background-dark);
  box-shadow: 0 0 5px rgba(255, 220, 0, 0.5);
}

.timeline-date {
  font-weight: 600;
  color: var(--primary-color);
  margin-bottom: 0.5rem;
}

.timeline-content h4 {
  margin-bottom: 0.25rem;
  color: var(--text-primary);
}

.timeline-company {
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
  font-style: italic;
}

.timeline-description {
  color: var(--text-primary);
}

.contact-info {
  margin-bottom: 1.5rem;
}

.contact-item {
  display: flex;
  margin-bottom: 0.5rem;
}

.contact-label {
  font-weight: 600;
  width: 80px;
  color: var(--text-primary);
}

.contact-value {
  color: var(--text-secondary);
}

.contact-form .form-group {
  margin-bottom: 1.5rem;
}

.contact-form label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: var(--text-primary);
}

.contact-form input, .contact-form textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-family: inherit;
  font-size: 1rem;
  background-color: var(--background-lighter);
  color: var(--text-primary);
}

.contact-form textarea {
  resize: vertical;
}

.submit-button {
  background-color: var(--primary-color);
  color: var(--background-dark);
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s;
  font-weight: 600;
}

.submit-button:hover {
  background-color: var(--primary-variant);
  box-shadow: 0 0 10px rgba(255, 220, 0, 0.4);
}

@media (min-width: 768px) {
  .profile-section {
    flex-direction: row;
    text-align: left;
  }
  
  .profile-social {
    justify-content: flex-start;
  }
}
</style>