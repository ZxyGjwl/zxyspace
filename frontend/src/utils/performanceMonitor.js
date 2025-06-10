// 性能监测工具
export class PerformanceMonitor {
  constructor() {
    this.frameCount = 0;
    this.startTime = null;
    this.isMonitoring = false;
    this.callbacks = [];
  }

  start() {
    if (this.isMonitoring) return;
    
    this.isMonitoring = true;
    this.frameCount = 0;
    this.startTime = performance.now();
    this.monitorFrame();
  }

  stop() {
    this.isMonitoring = false;
    return this.getFPS();
  }

  monitorFrame() {
    if (!this.isMonitoring) return;
    
    this.frameCount++;
    const currentTime = performance.now();
    const elapsed = currentTime - this.startTime;
    
    // 每秒计算一次FPS
    if (elapsed >= 1000) {
      const fps = Math.round((this.frameCount * 1000) / elapsed);
      this.callbacks.forEach(callback => callback(fps));
      
      // 重置计数器
      this.frameCount = 0;
      this.startTime = currentTime;
    }
    
    requestAnimationFrame(() => this.monitorFrame());
  }

  getFPS() {
    if (!this.startTime) return 0;
    
    const elapsed = performance.now() - this.startTime;
    return Math.round((this.frameCount * 1000) / elapsed);
  }

  onFPSUpdate(callback) {
    this.callbacks.push(callback);
  }

  // 检测动画性能
  static monitorTransition(element, duration = 1000) {
    return new Promise((resolve) => {
      const monitor = new PerformanceMonitor();
      const fpsHistory = [];
      
      monitor.onFPSUpdate((fps) => {
        fpsHistory.push(fps);
        console.log(`当前FPS: ${fps}`);
      });
      
      monitor.start();
      
      setTimeout(() => {
        monitor.stop();
        const avgFPS = fpsHistory.reduce((a, b) => a + b, 0) / fpsHistory.length;
        const minFPS = Math.min(...fpsHistory);
        
        resolve({
          averageFPS: Math.round(avgFPS),
          minimumFPS: minFPS,
          fpsHistory,
          performance: avgFPS >= 50 ? 'excellent' : avgFPS >= 30 ? 'good' : 'poor'
        });
      }, duration);
    });
  }

  // 检测内存使用
  static getMemoryUsage() {
    if (performance.memory) {
      return {
        used: Math.round(performance.memory.usedJSHeapSize / 1048576), // MB
        total: Math.round(performance.memory.totalJSHeapSize / 1048576), // MB
        limit: Math.round(performance.memory.jsHeapSizeLimit / 1048576) // MB
      };
    }
    return null;
  }

  // 检测长任务
  static observeLongTasks() {
    if ('PerformanceObserver' in window) {
      const observer = new PerformanceObserver((list) => {
        for (const entry of list.getEntries()) {
          if (entry.duration > 50) { // 超过50ms的任务被认为是长任务
            console.warn(`检测到长任务: ${entry.duration.toFixed(2)}ms`, entry);
          }
        }
      });
      
      try {
        observer.observe({ entryTypes: ['longtask'] });
        return observer;
      } catch (e) {
        console.log('浏览器不支持longtask监测');
        return null;
      }
    }
    return null;
  }
} 