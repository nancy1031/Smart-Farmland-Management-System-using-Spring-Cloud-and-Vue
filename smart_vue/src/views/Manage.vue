<template>
  <el-container style="min-height: 100vh">
    <el-aside :width="sideWidth + 'px'" style="box-shadow: 2px 0 6px rgb(0 21 41);">
      <Aside :isCollapse="isCollapse" :logoTextShow="logoTextShow" style="padding-bottom: 20px" />
    </el-aside>

    <el-container>
      <el-header style="border-bottom: 1px solid #ccc;">
        <Header :collapseBtnClass="collapseBtnClass" @asideCollapse="collapse" :user="user" />
      </el-header>
      <el-main >
        <router-view @refreshUser="getUser" />
      </el-main>
    </el-container>

    <!-- AI悬浮组件 -->
    <AiChat />
  </el-container>
</template>

<script>
import Aside from "@/components/Aside";
import Header from "@/components/Header";
import AiChat from "@/components/AiChat";

export default {
  name: 'Home',
  data() {
    return {
      collapseBtnClass: 'el-icon-s-fold',
      isCollapse: false,
      sideWidth: 200,
      logoTextShow: true,
      user: {}
    }
  },
  components: {
    Aside,
    Header,
    AiChat
  },
  created() {
    this.getUser()
  },
  methods: {
    collapse() {
      this.isCollapse = !this.isCollapse
      if (this.isCollapse) {
        this.sideWidth = 64
        this.collapseBtnClass = 'el-icon-s-unfold'
        this.logoTextShow = false
      } else {
        this.sideWidth = 200
        this.collapseBtnClass = 'el-icon-s-fold'
      }
    },
    getUser() {
      const userStr = localStorage.getItem("user");
      if (!userStr) return;
      const userInfo = JSON.parse(userStr);
      const username = userInfo.username;
      this.request.get("/user/username/" + username)
          .then(res => {
            this.user = res.data || {}
          })
          .catch(err => {
            console.log("用户信息请求失败", err)
          })
    }
  }
}
</script>