<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import dayjs from 'dayjs';

const props = defineProps({
  post: {
    type: Object,
    required: true
  }
});

const router = useRouter();

const formattedDate = computed(() => {
  return dayjs(props.post.createdAt).format('YYYY-MM-DD');
});

const navigateToBlog = () => {
  router.push(`/blog/${props.post.id}`);
};
</script>

<template>
  <el-card class="blog-card" shadow="hover" @click="navigateToBlog">
    <div class="blog-card-container">
      <div class="blog-image" v-if="post.coverImage">
        <img :src="post.coverImage" :alt="post.title" />
      </div>
      <div class="blog-content">
        <h3 class="blog-title">{{ post.title }}</h3>
        <div class="blog-meta">
          <span class="blog-date">
            <el-icon><Calendar /></el-icon>
            {{ formattedDate }}
          </span>
          <span class="blog-category" v-if="post.category">
            <el-icon><Folder /></el-icon>
            {{ post.category.name }}
          </span>
          <span class="blog-author">
            <el-icon><User /></el-icon>
            {{ post.author.username }}
          </span>
        </div>
        <p class="blog-excerpt">{{ post.excerpt }}</p>
        <div class="blog-tags" v-if="post.tags && post.tags.length > 0">
          <el-tag
            v-for="tag in post.tags"
            :key="tag.id"
            size="small"
            effect="plain"
            class="tag"
          >
            {{ tag.name }}
          </el-tag>
        </div>
        <div class="blog-stats">
          <span class="blog-views">
            <el-icon><View /></el-icon>
            {{ post.views }} 阅读
          </span>
          <span class="blog-likes">
            <el-icon><Star /></el-icon>
            {{ post.likes }} 点赞
          </span>
          <span class="blog-comments">
            <el-icon><ChatDotRound /></el-icon>
            {{ post.comments ? post.comments.length : 0 }} 评论
          </span>
        </div>
      </div>
    </div>
  </el-card>
</template>

<style scoped>
.blog-card {
  margin-bottom: 20px;
  transition: transform 0.3s ease;
  cursor: pointer;
}

.blog-card:hover {
  transform: translateY(-5px);
}

.blog-card-container {
  display: flex;
  gap: 20px;
}

.blog-image {
  flex: 0 0 200px;
  height: 150px;
  overflow: hidden;
  border-radius: 4px;
}

.blog-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.blog-content {
  flex: 1;
}

.blog-title {
  margin-top: 0;
  margin-bottom: 10px;
  color: #303133;
}

.blog-meta {
  display: flex;
  gap: 15px;
  color: #909399;
  font-size: 0.9rem;
  margin-bottom: 10px;
}

.blog-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.blog-excerpt {
  color: #606266;
  margin-bottom: 15px;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.blog-tags {
  margin-bottom: 15px;
}

.tag {
  margin-right: 5px;
}

.blog-stats {
  display: flex;
  gap: 15px;
  color: #909399;
  font-size: 0.9rem;
}

.blog-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

@media (max-width: 768px) {
  .blog-card-container {
    flex-direction: column;
  }
  
  .blog-image {
    flex: 0 0 auto;
    width: 100%;
    height: 200px;
  }
}
</style> 