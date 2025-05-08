<script setup>
import AppLayout from './components/layout/AppLayout.vue';
</script>

<template>
  <AppLayout />
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;700&display=swap');

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: 'Noto Sans SC', sans-serif;
  line-height: 1.6;
  color: #333;
  background-color: #f8f9fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

a {
  color: #409EFF;
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}

h1, h2, h3, h4, h5, h6 {
  margin-bottom: 0.5em;
  font-weight: 600;
  line-height: 1.25;
}

.page-title {
  font-size: 2rem;
  color: #303133;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eaeaea;
}

.text-center {
  text-align: center;
}

.mt-1 { margin-top: 0.25rem; }
.mt-2 { margin-top: 0.5rem; }
.mt-3 { margin-top: 1rem; }
.mt-4 { margin-top: 1.5rem; }
.mt-5 { margin-top: 3rem; }

.mb-1 { margin-bottom: 0.25rem; }
.mb-2 { margin-bottom: 0.5rem; }
.mb-3 { margin-bottom: 1rem; }
.mb-4 { margin-bottom: 1.5rem; }
.mb-5 { margin-bottom: 3rem; }

.ml-1 { margin-left: 0.25rem; }
.ml-2 { margin-left: 0.5rem; }
.ml-3 { margin-left: 1rem; }
.ml-4 { margin-left: 1.5rem; }
.ml-5 { margin-left: 3rem; }

.mr-1 { margin-right: 0.25rem; }
.mr-2 { margin-right: 0.5rem; }
.mr-3 { margin-right: 1rem; }
.mr-4 { margin-right: 1.5rem; }
.mr-5 { margin-right: 3rem; }

.mx-auto {
  margin-left: auto;
  margin-right: auto;
}

.flex {
  display: flex;
}

.flex-col {
  flex-direction: column;
}

.items-center {
  align-items: center;
}

.justify-center {
  justify-content: center;
}

.justify-between {
  justify-content: space-between;
}

.gap-1 { gap: 0.25rem; }
.gap-2 { gap: 0.5rem; }
.gap-3 { gap: 1rem; }
.gap-4 { gap: 1.5rem; }
.gap-5 { gap: 3rem; }
</style>