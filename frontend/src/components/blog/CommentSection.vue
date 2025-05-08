<script setup>
import { ref, computed } from 'vue';
import { useUserStore } from '@/store/user';
import { ElMessage } from 'element-plus';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/zh-cn';

dayjs.extend(relativeTime);
dayjs.locale('zh-cn');

const props = defineProps({
  comments: {
    type: Array,
    default: () => []
  },
  postId: {
    type: Number,
    required: true
  }
});

const emit = defineEmits(['comment-added']);

const userStore = useUserStore();
const commentContent = ref('');
const submitting = ref(false);

const isLoggedIn = computed(() => userStore.isLoggedIn);

const formattedComments = computed(() => {
  return props.comments.map(comment => ({
    ...comment,
    formattedTime: dayjs(comment.createdAt).fromNow()
  }));
});

const submitComment = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录后再发表评论');
    return;
  }

  if (!commentContent.value.trim()) {
    ElMessage.warning('评论内容不能为空');
    return;
  }

  try {
    submitting.value = true;
    // 这里应该有一个API调用来提交评论
    // const response = await commentService.addComment({
    //   postId: props.postId,
    //   content: commentContent.value
    // });
    
    // 模拟API调用成功
    const newComment = {
      id: Date.now(),
      content: commentContent.value,
      createdAt: new Date().toISOString(),
      author: userStore.userInfo
    };
    
    emit('comment-added', newComment);
    commentContent.value = '';
    ElMessage.success('评论发表成功');
  } catch (error) {
    ElMessage.error('评论发表失败，请稍后重试');
    console.error('Failed to submit comment:', error);
  } finally {
    submitting.value = false;
  }
};
</script>

<template>
  <div class="comment-section">
    <h3 class="section-title">评论 ({{ comments.length }})</h3>
    
    <div class="comment-form">
      <el-avatar 
        v-if="isLoggedIn" 
        :size="40" 
        :src="userStore.userInfo?.avatar || ''"
      >
        {{ userStore.userInfo?.username?.substring(0, 1).toUpperCase() || 'U' }}
      </el-avatar>
      <el-avatar v-else :size="40">游</el-avatar>
      
      <el-input
        v-model="commentContent"
        type="textarea"
        :rows="3"
        placeholder="写下你的评论..."
        :disabled="!isLoggedIn"
      />
    </div>
    
    <div class="form-actions">
      <el-button 
        type="primary" 
        @click="submitComment" 
        :loading="submitting"
        :disabled="!isLoggedIn"
      >
        {{ isLoggedIn ? '发表评论' : '请先登录' }}
      </el-button>
    </div>
    
    <div class="comments-list" v-if="formattedComments.length > 0">
      <el-divider content-position="center">全部评论</el-divider>
      
      <div v-for="comment in formattedComments" :key="comment.id" class="comment-item">
        <div class="comment-avatar">
          <el-avatar 
            :size="40" 
            :src="comment.author.avatar || ''"
          >
            {{ comment.author.username.substring(0, 1).toUpperCase() }}
          </el-avatar>
        </div>
        <div class="comment-content">
          <div class="comment-header">
            <span class="comment-author">{{ comment.author.username }}</span>
            <span class="comment-time">{{ comment.formattedTime }}</span>
          </div>
          <div class="comment-text">{{ comment.content }}</div>
          <div class="comment-actions">
            <el-button type="text" size="small">
              <el-icon><ChatLineRound /></el-icon> 回复
            </el-button>
            <el-button type="text" size="small">
              <el-icon><Star /></el-icon> 点赞
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <el-empty 
      v-else 
      description="暂无评论，来发表第一条评论吧！" 
      :image-size="100"
    />
  </div>
</template>

<style scoped>
.comment-section {
  margin-top: 40px;
}

.section-title {
  font-size: 1.5rem;
  margin-bottom: 20px;
  color: #303133;
}

.comment-form {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.comment-form .el-input {
  flex: 1;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 30px;
}

.comments-list {
  margin-top: 20px;
}

.comment-item {
  display: flex;
  margin-bottom: 25px;
  gap: 15px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.comment-author {
  font-weight: 600;
  color: #303133;
}

.comment-time {
  color: #909399;
  font-size: 0.9rem;
}

.comment-text {
  line-height: 1.6;
  color: #606266;
  margin-bottom: 10px;
}

.comment-actions {
  display: flex;
  gap: 15px;
}
</style> 