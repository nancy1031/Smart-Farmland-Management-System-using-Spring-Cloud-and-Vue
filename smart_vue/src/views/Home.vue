<template>
  <div style="color: #666; font-size: 14px; background: #f0f2f5; min-height: 100vh; padding: 20px;">

    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <div class="welcome-text">
          <span class="greeting-icon">👋</span>
          <span class="greeting-text">您好！<strong>{{ user.nickname || '用户' }}</strong>，欢迎使用智慧农业管理系统</span>
        </div>
        <div class="welcome-time">
          <span>{{ currentTime }}</span>
        </div>
      </div>
    </div>

    <!-- 公告区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="notice-card" shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-megaphone" style="color: #E6A23C; font-size: 18px;"></i> 系统公告</span>
            <span style="font-size: 12px; color: #999;">共 {{ notice.length }} 条</span>
          </div>

          <div v-if="notice.length > 0" class="notice-grid">
            <div 
              class="notice-item" 
              v-for="(item, index) in notice" 
              :key="index"
              @click="toggleNotice(index)"
            >
              <div class="notice-item-header">
                <div class="notice-item-title">{{ item.name }}</div>
                <div class="notice-item-time">{{ item.time }}</div>
              </div>
              <!-- 当前索引在数组中时显示 -->
              <div class="notice-item-body" v-show="activeIndexes.includes(index)">
                <div class="notice-item-images" v-if="getImageList(item.imgs).length > 0">
                  <div 
                    class="notice-item-image" 
                    v-for="(img, idx) in getImageList(item.imgs)" 
                    :key="idx"
                    @click.stop="previewImage(getImageList(item.imgs), idx)"
                  >
                    <img :src="img" class="notice-image" />
                  </div>
                </div>
                <div class="notice-item-content">{{ item.content }}</div>
              </div>
            </div>
          </div>

          <div v-else style="text-align: center; padding: 60px 0; color: #999;">
            <i class="el-icon-document" style="font-size: 56px; color: #ddd;"></i>
            <p style="margin-top: 16px; font-size: 16px;">暂无公告</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图片预览弹窗 -->
    <el-dialog :visible.sync="previewDialogVisible" width="60%" :close-on-click-modal="true">
      <img :src="previewImageUrl" style="width: 100%; max-height: 70vh; object-fit: contain;" />
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      activeIndexes: [],  // 存储所有展开的公告索引
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      notice: [],
      currentTime: '',
      previewDialogVisible: false,
      previewImageUrl: ''
    }
  },
  created() {
    this.updateTime()
    this.loadNotice()
    setInterval(this.updateTime, 60000)
  },
  methods: {
    getImageList(imgs) {
      if (!imgs) return []
      return imgs.split(',').filter(url => url.trim() !== '')
    },
    previewImage(imageList, index) {
      this.previewImageUrl = imageList[index]
      this.previewDialogVisible = true
    },
    toggleNotice(index) {
      // 如果已展开则收起，如果折叠则展开
      const idx = this.activeIndexes.indexOf(index)
      if (idx > -1) {
        this.activeIndexes.splice(idx, 1)  // 移除
      } else {
        this.activeIndexes.push(index)  // 添加
      }
    },
    updateTime() {
      const now = new Date()
      const hours = String(now.getHours()).padStart(2, '0')
      const minutes = String(now.getMinutes()).padStart(2, '0')
      const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
      const date = now.getFullYear() + '年' + (now.getMonth() + 1) + '月' + now.getDate() + '日'
      this.currentTime = date + ' ' + weekdays[now.getDay()] + ' ' + hours + ':' + minutes
    },
    loadNotice() {
      this.request.get("/notice").then(res => {
        if (res.data && Array.isArray(res.data)) {
          this.notice = res.data
        } else if (res.data && res.data.records) {
          this.notice = res.data.records
        } else {
          this.notice = []
        }
      }).catch(err => {
        console.error('加载公告失败:', err)
        this.notice = []
      })
    }
  }
};
</script>

<style>
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 24px 32px;
  color: #fff;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.35);
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
}

.welcome-text {
  font-size: 18px;
}

.greeting-icon {
  font-size: 28px;
  margin-right: 12px;
}

.greeting-text strong {
  color: #FFD700;
}

.welcome-time {
  font-size: 14px;
  opacity: 0.85;
  background: rgba(255, 255, 255, 0.2);
  padding: 6px 16px;
  border-radius: 20px;
}

.notice-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06) !important;
}

.notice-card .el-card__body {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.notice-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  align-items: start;
}

@media screen and (max-width: 768px) {
  .notice-grid {
    grid-template-columns: 1fr;
  }
}

.notice-item {
  background: #f8f9fc;
  border-radius: 12px;
  padding: 16px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  height: fit-content;
}

.notice-item:hover {
  background: #f0f2f7;
  border-color: #667eea;
}

.notice-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.notice-item-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.notice-item-title::before {
  content: '📌 ';
}

.notice-item-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

.notice-item-body {
  padding-top: 12px;
  border-top: 1px solid #eee;
  margin-top: 10px;
}

.notice-item-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 10px;
  margin-bottom: 12px;
}

.notice-item-image {
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
  min-height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.notice-image {
  width: 100%;
  min-height: 150px;
  max-height: 250px;
  object-fit: cover;
  display: block;
  background: #fff;
}

.notice-item-content {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  white-space: pre-wrap;
  word-wrap: break-word;
  max-height: 200px;
  overflow-y: auto;
}

.el-dialog__body {
  padding: 10px 20px;
}

.el-dialog__body img {
  max-height: 70vh;
  object-fit: contain;
}

@media screen and (max-width: 768px) {
  .welcome-banner {
    padding: 18px 20px;
  }
  .welcome-text {
    font-size: 15px;
  }
  .greeting-icon {
    font-size: 22px;
    margin-right: 8px;
  }
  .welcome-time {
    font-size: 12px;
    margin-top: 8px;
  }
  .notice-item-title {
    font-size: 14px;
  }
  .notice-item-images {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}

@media screen and (max-width: 480px) {
  .notice-item-images {
    grid-template-columns: 1fr 1fr;
  }
}
</style>