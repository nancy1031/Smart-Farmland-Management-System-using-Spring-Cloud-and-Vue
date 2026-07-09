<template>
  <div class="ai-float-box">
    <div class="float-btn" @click="dialogVisible = !dialogVisible">
      {{ dialogVisible ? '关闭AI助手' : '智能问答' }}
    </div>
    <div v-if="dialogVisible" class="chat-dialog">
      <div class="chat-header">智慧农业AI业务助手</div>
      <div class="chat-content" ref="chatScroll">
        <div v-for="(item, index) in msgList" :key="index"
             :class="item.role === 'user' ? 'user-msg' : 'ai-msg'">
          <div class="msg-text" v-html="item.content"></div>
        </div>
        <div v-if="loading" class="ai-msg">
          <div class="msg-text">AI正在思考...</div>
        </div>
      </div>

      <div class="chat-input">
        <textarea
            v-model="inputText"
            placeholder="咨询农业种植、农资经营问题"
            @keyup.enter="sendMsg"
            :disabled="loading"
        ></textarea>
        <div class="btn-group">
          <el-button type="primary" @click="sendMsg" :disabled="loading">发送</el-button>
          <el-button type="info" @click="sendRagMsg" :disabled="loading">📚 知识库问答</el-button>
          <el-button type="success" @click="analyseData" :disabled="loading">一键业务分析</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { aiAxios } from '@/utils/request'

export default {
  name: 'AiChat',
  data() {
    return {
      dialogVisible: false,
      loading: false,
      inputText: '',
      sessionId: 'ai_session_' + new Date().getTime(),
      msgList: []
    }
  },
  mounted() {
    console.log('🤖 AI服务地址:', aiAxios.defaults.baseURL)
  },
  watch: {
    dialogVisible(newVal) {
      if (newVal) {
        this.$nextTick(() => this.scrollBottom())
      }
    }
  },
  methods: {
    scrollBottom() {
      const dom = this.$refs.chatScroll
      if (dom) dom.scrollTop = dom.scrollHeight
    },

    async sendMsg() {
      const text = this.inputText.trim()
      if (!text || this.loading) return

      this.msgList.push({ role: 'user', content: text })
      this.inputText = ''
      this.loading = true
      this.$nextTick(() => this.scrollBottom())

      try {
        const res = await aiAxios.post('/api/ai/chat/context', {
          sessionId: this.sessionId,
          message: text
        })
        console.log('AI响应:', res)

        if (res.code === 200) {
          this.msgList.push({ role: 'ai', content: res.data })
        } else {
          this.msgList.push({ role: 'ai', content: res.message || 'AI服务暂时不可用' })
        }
      } catch (err) {
        console.error('发送消息失败：', err)
        this.msgList.push({ role: 'ai', content: '网络请求失败，无法获取AI回答' })
      } finally {
        this.loading = false
        this.$nextTick(() => this.scrollBottom())
      }
    },

    // 🔥 新增：知识库问答
    async sendRagMsg() {
      const text = this.inputText.trim()
      if (!text || this.loading) return

      this.msgList.push({ role: 'user', content: '📚 ' + text })
      this.inputText = ''
      this.loading = true
      this.$nextTick(() => this.scrollBottom())

      try {
        const res = await aiAxios.post('/api/ai/chat/rag', {
          sessionId: this.sessionId,
          message: text
        })
        console.log('📚 RAG响应:', res)

        if (res.code === 200) {
          const content = (res.data || '').replace(/\n/g, '<br>')
          this.msgList.push({ role: 'ai', content: '📖 ' + content })
        } else {
          this.msgList.push({ role: 'ai', content: res.message || '知识库问答失败' })
        }
      } catch (err) {
        console.error('❌ 知识库问答失败:', err)
        this.msgList.push({ role: 'ai', content: '知识库问答失败，请稍后重试' })
      } finally {
        this.loading = false
        this.$nextTick(() => this.scrollBottom())
      }
    },

    async analyseData() {
      this.loading = true
      this.msgList.push({ role: 'user', content: '请分析当前农田种植经营情况' })

      try {
        const res = await aiAxios.post('/api/ai/chat/analyse')
        console.log('📊 分析响应:', res)

        if (res.code === 200 && res.data) {
          const content = res.data.replace(/\n/g, '<br>')
          this.msgList.push({ role: 'ai', content: content })
        } else {
          this.msgList.push({ role: 'ai', content: res?.message || '数据分析失败，请稍后重试' })
        }
      } catch (err) {
        console.error('❌ 数据分析请求失败:', err)
        this.msgList.push({ role: 'ai', content: '数据分析失败，请稍后重试' })
      } finally {
        this.loading = false
        this.$nextTick(() => this.scrollBottom())
      }
    }
  }
}
</script>

<style scoped>
.ai-float-box {
  position: fixed;
  right: 40px;
  bottom: 40px;
  z-index: 9999;
}
.float-btn {
  width: 130px;
  height: 48px;
  line-height: 48px;
  text-align: center;
  background: #409EFF;
  color: #fff;
  border-radius: 24px;
  cursor: pointer;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.4);
  transition: all 0.3s;
  font-size: 14px;
  user-select: none;
}
.float-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 20px rgba(64, 158, 255, 0.6);
}
.chat-dialog {
  width: 520px;
  height: 620px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  margin-top: 10px;
  overflow: hidden;
}
.chat-header {
  height: 50px;
  line-height: 50px;
  text-align: center;
  background: linear-gradient(135deg, #409EFF, #66b1ff);
  color: white;
  font-weight: bold;
  font-size: 16px;
  border-radius: 12px 12px 0 0;
  flex-shrink: 0;
}
.chat-content {
  flex: 1;
  padding: 14px;
  overflow-y: auto;
  background: #f8f9fa;
}
.user-msg {
  display: flex;
  justify-content: flex-end;
  margin: 8px 0;
}
.ai-msg {
  display: flex;
  justify-content: flex-start;
  margin: 8px 0;
}
.msg-text {
  max-width: 85%;
  padding: 10px 14px;
  border-radius: 16px;
  line-height: 1.8;
  word-wrap: break-word;
  white-space: pre-wrap;
}
.user-msg .msg-text {
  background: #409EFF;
  color: #fff;
  border-bottom-right-radius: 4px;
}
.ai-msg .msg-text {
  background: #e9ecef;
  color: #333;
  border-bottom-left-radius: 4px;
}
.chat-input {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 12px;
  border-top: 1px solid #eee;
  background: #fff;
  flex-shrink: 0;
}
.btn-group {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.btn-group .el-button {
  flex: 1;
  min-width: 80px;
}
.chat-input textarea {
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 8px;
  resize: none;
  min-height: 60px;
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.3s;
}
.chat-input textarea:focus {
  outline: none;
  border-color: #409EFF;
}
.chat-input textarea:disabled {
  background: #f5f7fa;
}
</style>